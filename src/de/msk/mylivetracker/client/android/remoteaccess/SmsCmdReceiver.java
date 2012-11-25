package de.msk.mylivetracker.client.android.remoteaccess;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.msk.mylivetracker.client.android.app.AbstractApp;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.remoteaccess.AbstractSmsCmdExecutor.CmdDsc;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * SmsCmdReceiver.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 000 2012-11-23 initial.
 * 
 */
public class SmsCmdReceiver extends BroadcastReceiver {

	private static Map<String, Class<? extends AbstractSmsCmdExecutor>> cmdRegistry = 
		new HashMap<String, Class<? extends AbstractSmsCmdExecutor>>();
	
	static {
		cmdRegistry.put(StringUtils.lowerCase("version"), SmsCmdGetAppVersion.class);
		cmdRegistry.put(StringUtils.lowerCase("location"), SmsCmdGetLocation.class);
		cmdRegistry.put(StringUtils.lowerCase("track"), SmsCmdTrack.class);
	}
	
	private static boolean smsCmdExecutorExists(String cmdName) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be empty.");
		}
		LogUtils.infoMethodIn(SmsCmdReceiver.class, "smsCmdExecutorExists", cmdName);
		boolean res = cmdRegistry.containsKey(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(SmsCmdReceiver.class, "smsCmdExecutorExists", res);
		return res;
	}
	
	private static Class<? extends AbstractSmsCmdExecutor> getSmsCmdExecutor(String cmdName) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be empty.");
		}
		LogUtils.infoMethodIn(SmsCmdReceiver.class, "getSmsCmdExecutor", cmdName);
		Class<? extends AbstractSmsCmdExecutor> res = 
			cmdRegistry.get(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(SmsCmdReceiver.class, "getSmsCmdExecutor", res);
		return res;
	}
	
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private static final String SMS_CMD_INDICATOR = "#mlt";
	private static final String SMS_CMD_ERROR_PREFIX = "FAILED: ";
	private static final String SMS_CMD_SYNTAX = SMS_CMD_INDICATOR + " <password> <command> [<param-0> <param-1> ... <param-n>]";
	private static final String SMS_CMD_ERROR_SYNTAX_INVALID = "command format must be '" + SMS_CMD_SYNTAX + "'.";
	private static final String SMS_CMD_ERROR_PASSWORD_INVALID = "invalid password.";
	private static final String SMS_CMD_ERROR_COMMAND_INVALID = "invalid command.";
	private static final String SMS_CMD_ERROR_COMMAND_SYNTAX_INVALID = "invalid command syntax. correct syntax is: '$SYNTAX'";
	
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
					!StringUtils.isEmpty(message) && StringUtils.startsWithIgnoreCase(message, SMS_CMD_INDICATOR)) {
					LogUtils.infoMethodState(this.getClass(), "onReceive", "sms details", sender, message);
					String[] messageParts = StringUtils.split(message, ' ');
					LogUtils.infoMethodState(this.getClass(), "onReceive", "message parts", Arrays.toString(messageParts));
					if (messageParts.length < 3) {
						response = SMS_CMD_ERROR_SYNTAX_INVALID;
					} else if (!StringUtils.equals(messageParts[1], 
						Preferences.get().getRemoteAccessPassword())) {
						response = SMS_CMD_ERROR_PASSWORD_INVALID;
					} else if (!smsCmdExecutorExists(messageParts[2])) {
						response = SMS_CMD_ERROR_COMMAND_INVALID;
					} else {
						String cmdName = messageParts[2];
						String[] params = new String[0];
						if (messageParts.length > 3) {
							String[] paramsCs = (String[])ArrayUtils.subarray(messageParts, 3, messageParts.length);
							params = new String[paramsCs.length];
							for (int i=0; i < paramsCs.length; i++) {
								params[i] = StringUtils.lowerCase(paramsCs[i]);
							}
						}
						LogUtils.infoMethodState(this.getClass(), "onReceive", "params", Arrays.toString(params));
						Class<? extends AbstractSmsCmdExecutor> smsCmdExecutorClass = 
							getSmsCmdExecutor(messageParts[2]);
						Constructor<? extends AbstractSmsCmdExecutor> smsCmdExecutorConstructor = 
							smsCmdExecutorClass.getConstructor(String.class, String.class, String[].class);
						AbstractSmsCmdExecutor smsCmdExecutor = smsCmdExecutorConstructor.newInstance(cmdName, sender, params);
						CmdDsc cmdDsc = smsCmdExecutor.getCmdDsc();
						if ((params.length < cmdDsc.getMinParams()) || 
							(params.length > cmdDsc.getMaxParams())) {
							response = SMS_CMD_ERROR_COMMAND_SYNTAX_INVALID;
							response = StringUtils.replace(response, "$SYNTAX", 
								cmdName + " " + cmdDsc.getParamSyntax());
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
					SmsCmdError smsCmdError = new SmsCmdError(sender, SMS_CMD_ERROR_PREFIX, response);
					executorService.execute(smsCmdError);
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
		LogUtils.infoMethodIn(SmsCmdReceiver.class, "sendSms", phoneNumber, message);
		SmsManager manager = SmsManager.getDefault();

		PendingIntent piSend = PendingIntent.getBroadcast(AbstractApp.getCtx(), 0,
				new Intent(SEND_SMS_STATUS_SMS_SENT), 0);
		PendingIntent piDelivered = PendingIntent.getBroadcast(AbstractApp.getCtx(), 0,
				new Intent(SEND_SMS_STATUS_SMS_DELIVERED), 0);

		int length = message.length();
		LogUtils.infoMethodState(SmsCmdReceiver.class, "sendSms", "length of message", new Integer(length));
		
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
		LogUtils.infoMethodOut(SmsCmdReceiver.class, "sendSms");
	}
}
