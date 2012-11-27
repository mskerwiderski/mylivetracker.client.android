package de.msk.mylivetracker.client.android.remoteaccess;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
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
		cmdRegistry.put(StringUtils.lowerCase("getversion"), SmsCmdGetAppVersion.class);
		cmdRegistry.put(StringUtils.lowerCase("getlocation"), SmsCmdGetLocation.class);
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
	
	public static final String SMS_CMD_INDICATOR = "#mlt";
	public static final String SMS_CMD_ERROR_PREFIX = "FAILED: ";
	public static final String SMS_CMD_ERROR_COMMAND_INVALID = "invalid command.";
	public static final String SMS_CMD_ERROR_COMMAND_SYNTAX_INVALID = "invalid command syntax. correct syntax is: '$SYNTAX'";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Preferences prefs = Preferences.get();
		if (!prefs.isRemoteAccessEnabled()) return;
		LogUtils.infoMethodIn(this.getClass(), "onReceive");
		Bundle bundle = intent.getExtras();
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			String response = null;
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
			String sender = smsMessage[0].getOriginatingAddress();
			if (prefs.isRemoteAccessUseReceiver()) {
				sender = prefs.getRemoteAccessReceiver();
			}
			String message = smsMessage[0].getMessageBody();
			try {
				if (!StringUtils.isEmpty(sender) && PhoneNumberUtils.isGlobalPhoneNumber(sender) && 
					!StringUtils.isEmpty(message) && StringUtils.startsWithIgnoreCase(message, SMS_CMD_INDICATOR)) {
					LogUtils.infoMethodState(this.getClass(), "onReceive", "sms details", sender, message);
					String[] messageParts = StringUtils.split(message, ' ');
					LogUtils.infoMethodState(this.getClass(), "onReceive", "message parts", Arrays.toString(messageParts));
					if (messageParts.length < 3) {
						// do not send a response in case of an error before password has been checked.
						LogUtils.infoMethodState(this.getClass(), "onReceive", "sms message check", "invalid syntax");
						response = null;
					} else if (!StringUtils.equals(messageParts[1], 
						prefs.getRemoteAccessPassword())) {
						LogUtils.infoMethodState(this.getClass(), "onReceive", "sms message check", "invalid password");
						// do not send a response in case of a wrong password.
						response = null;
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
				response = e.toString();
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
}	