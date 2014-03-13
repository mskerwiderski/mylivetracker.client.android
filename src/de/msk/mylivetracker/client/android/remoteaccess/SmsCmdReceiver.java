package de.msk.mylivetracker.client.android.remoteaccess;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdTrack;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdUpload;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdVersion;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: SmsCmdReceiver
 * 
 * @author michael skerwiderski, (c)2012
 * @version 001
 * @since 1.5.0
 * 
 * history:
 * 001	2014-02-28	revised for v1.6.0.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class SmsCmdReceiver extends BroadcastReceiver {

	private static Map<String, Class<? extends ASmsCmdExecutor>> cmdRegistry = 
		new HashMap<String, Class<? extends ASmsCmdExecutor>>();
	
	static {
		cmdRegistry.put(SmsCmdGetHelp.NAME, SmsCmdGetHelp.class);
		cmdRegistry.put(RemoteCmdVersion.NAME, RemoteCmdVersion.class);
		cmdRegistry.put(SmsCmdLocalization.NAME, SmsCmdLocalization.class);
		cmdRegistry.put(SmsCmdGetHeartrate.NAME, SmsCmdGetHeartrate.class);
		cmdRegistry.put(RemoteCmdTrack.NAME, RemoteCmdTrack.class);
		cmdRegistry.put(SmsCmdGetConfig.NAME, SmsCmdGetConfig.class);
		cmdRegistry.put(RemoteCmdUpload.NAME, RemoteCmdUpload.class);
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
	
	private static Class<? extends ASmsCmdExecutor> getSmsCmdExecutor(String cmdName) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be empty.");
		}
		LogUtils.infoMethodIn(SmsCmdReceiver.class, "getSmsCmdExecutor", cmdName);
		Class<? extends ASmsCmdExecutor> res = 
			cmdRegistry.get(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(SmsCmdReceiver.class, "getSmsCmdExecutor", res);
		return res;
	}
	
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	public static final String SMS_CMD_INDICATOR = "#mlt";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		RemoteAccessPrefs prefs = PrefsRegistry.get(RemoteAccessPrefs.class);
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
			String message = StringUtils.lowerCase(smsMessage[0].getMessageBody());
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
						response = ResponseCreator.getResultOfError("unknown command '" + messageParts[2] + "'");
					} else {
						String[] params = new String[0];
						if (messageParts.length > 3) {
							String[] paramsCs = (String[])ArrayUtils.subarray(messageParts, 3, messageParts.length);
							params = new String[paramsCs.length];
							for (int i=0; i < paramsCs.length; i++) {
								params[i] = StringUtils.lowerCase(paramsCs[i]);
							}
						}
						LogUtils.infoMethodState(this.getClass(), "onReceive", "params", Arrays.toString(params));
						Class<? extends ASmsCmdExecutor> smsCmdExecutorClass = 
							getSmsCmdExecutor(messageParts[2]);
						Constructor<? extends ASmsCmdExecutor> smsCmdExecutorConstructor = 
							smsCmdExecutorClass.getConstructor(String.class, String[].class);
						ASmsCmdExecutor smsCmdExecutor = smsCmdExecutorConstructor.newInstance(sender, params);
						executorService.execute(smsCmdExecutor);
						response = null;
						LogUtils.infoMethodState(this.getClass(), "onReceive", "command executed");
					}
				}
			} catch (Exception e) {
				response = ResponseCreator.getResultOfError(e.getMessage());
			} finally {
				if (!StringUtils.isEmpty(response)) {
					SmsCmdError smsCmdError = new SmsCmdError(sender, response);
					executorService.execute(smsCmdError);
					LogUtils.infoMethodState(this.getClass(), "onReceive", "command failed", sender, response);
				}
			}
		}

		LogUtils.infoMethodOut(this.getClass(), "onReceive");
	}
}	
