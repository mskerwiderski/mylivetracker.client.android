package de.msk.mylivetracker.client.android.remoteaccess;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.remoteaccess.AbstractSmsCmdExecutor.ParamsDsc;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * SmsReceiver.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 000 2012-11-23 initial.
 * 
 */
public class SmsReceiver extends BroadcastReceiver {

	private static Map<String, Class<? extends AbstractSmsCmdExecutor>> cmdRegistry = 
		new HashMap<String, Class<? extends AbstractSmsCmdExecutor>>();
	
	static {
		cmdRegistry.put(StringUtils.lowerCase("version"), SmsCmdGetAppVersion.class);
		cmdRegistry.put(StringUtils.lowerCase("location"), SmsCmdGetLocation.class);
	}
	
	private static boolean smsCmdExecutorExists(String cmdName) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be empty.");
		}
		LogUtils.infoMethodIn(SmsReceiver.class, "smsCmdExecutorExists", cmdName);
		boolean res = cmdRegistry.containsKey(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(SmsReceiver.class, "smsCmdExecutorExists", res);
		return res;
	}
	
	private static Class<? extends AbstractSmsCmdExecutor> getSmsCmdExecutor(String cmdName) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be empty.");
		}
		LogUtils.infoMethodIn(SmsReceiver.class, "getSmsCmdExecutor", cmdName);
		Class<? extends AbstractSmsCmdExecutor> res = 
			cmdRegistry.get(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(SmsReceiver.class, "getSmsCmdExecutor", res);
		return res;
	}
	
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (!Preferences.get().isRemoteAccessEnabled()) return;
		LogUtils.infoMethodIn(this.getClass(), "onReceive");
		Bundle bundle = intent.getExtras();
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			String response = null;
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
			String sender = smsMessage[0].getOriginatingAddress();
			String message = smsMessage[0].getMessageBody();
			try {
				if (!StringUtils.isEmpty(sender) && PhoneNumberUtils.isGlobalPhoneNumber(sender) && 
					!StringUtils.isEmpty(message) && StringUtils.startsWithIgnoreCase(message, "#mlt")) {
					LogUtils.infoMethodState(this.getClass(), "onReceive", "sms details", sender, message);
					String[] messageParts = StringUtils.split(message, ' ');
					LogUtils.infoMethodState(this.getClass(), "onReceive", "message parts", messageParts.toString());
					if (messageParts.length < 3) {
						response = "command format must be '#MLT <password> <command> [<param-0> <param-1> ... <param-n>]'.";
					} else if (!StringUtils.equals(messageParts[1], 
						Preferences.get().getRemoteAccessPassword())) {
						response = "invalid password.";
					} else if (!smsCmdExecutorExists(messageParts[2])) {
						response = "invalid command.";
					} else {
						String[] params = new String[0];
						if (messageParts.length > 3) {
							String[] paramsCs = (String[])ArrayUtils.subarray(messageParts, 3, messageParts.length);
							params = new String[paramsCs.length];
							for (int i=0; i < paramsCs.length; i++) {
								params[i] = StringUtils.lowerCase(paramsCs[i]);
							}
						}
						LogUtils.infoMethodState(this.getClass(), "onReceive", "params", params.toString());
						Class<? extends AbstractSmsCmdExecutor> smsCmdExecutorClass = 
							getSmsCmdExecutor(messageParts[2]);
						Constructor<? extends AbstractSmsCmdExecutor> smsCmdExecutorConstructor = 
							smsCmdExecutorClass.getConstructor(String.class, String[].class);
						AbstractSmsCmdExecutor smsCmdExecutor = smsCmdExecutorConstructor.newInstance(sender, messageParts);
						ParamsDsc paramsDsc = smsCmdExecutor.getParamsDsc();
						if ((params.length < paramsDsc.getMinParams()) || 
							(params.length > paramsDsc.getMaxParams())) {
							response = "invalid count of parameters.";
						} else {
							executorService.execute(smsCmdExecutor);
							response = null;
							LogUtils.infoMethodState(this.getClass(), "onReceive", "command executed");
						}
					}
				}
			} catch (Exception e) {
				response = StringUtils.abbreviate(e.toString(), 80);
			} finally {
				if (!StringUtils.isEmpty(response)) {
					SmsReceiver.sendSms(sender, "FAILED: " + response);
					LogUtils.infoMethodState(this.getClass(), "onReceive", "command failed", sender, response);
				}
			}
		}

		LogUtils.infoMethodOut(this.getClass(), "onReceive");
	}
	
	public static final String SEND_SMS_STATUS_SMS_SENT = "de.msk.mylivetracker.intent.action.SMS_SENT";
	public static final String SEND_SMS_STATUS_SMS_DELIVERED = "de.msk.mylivetracker.intent.action.SMS_DELIVERED";

	private static final int MAX_SMS_MESSAGE_LENGTH = 160;

	public static void sendSms(String phoneNumber, String message) {
		if (StringUtils.isEmpty(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber must not be null.");
		}
		if (StringUtils.isEmpty(message)) {
			throw new IllegalArgumentException("message must not be null.");
		}
		if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber '" + phoneNumber + "' is invalid.");
		}
		LogUtils.infoMethodIn(SmsReceiver.class, "sendSms", phoneNumber, message);
		SmsManager manager = SmsManager.getDefault();

		PendingIntent piSend = PendingIntent.getBroadcast(App.getCtx(), 0,
				new Intent(SEND_SMS_STATUS_SMS_SENT), 0);
		PendingIntent piDelivered = PendingIntent.getBroadcast(App.getCtx(), 0,
				new Intent(SEND_SMS_STATUS_SMS_DELIVERED), 0);

		int length = message.length();
		LogUtils.infoMethodState(SmsReceiver.class, "sendSms", "length of message", new Integer(length));
		
		if (length > MAX_SMS_MESSAGE_LENGTH) {
			ArrayList<String> messagelist = manager.divideMessage(message);
			manager.sendMultipartTextMessage(
				phoneNumber, null, messagelist,
				null, null);
		} else {
			manager.sendTextMessage(
				phoneNumber, null, message, piSend,
				piDelivered);
		}
		LogUtils.infoMethodOut(SmsReceiver.class, "sendSms");
	}
}
