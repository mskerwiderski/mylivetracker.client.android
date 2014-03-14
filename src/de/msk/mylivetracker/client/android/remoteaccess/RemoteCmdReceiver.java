package de.msk.mylivetracker.client.android.remoteaccess;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdConfig;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdError;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdHeartrate;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdHelp;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdLocalization;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdStatus;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdTracking;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdUpload;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdVersion;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: RemoteCmdReceiver
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
public class RemoteCmdReceiver extends BroadcastReceiver {

	private static class CmdPackage {
		public ARemoteCmdDsc dsc;
		public Class<? extends ARemoteCmdExecutor> executor;
		public CmdPackage(ARemoteCmdDsc dsc,
			Class<? extends ARemoteCmdExecutor> executor) {
			this.dsc = dsc;
			this.executor = executor;
		}
		@Override
		public String toString() {
			return "CmdPackage [dsc=" + dsc + ", executor=" + executor + "]";
		}
	}
	
	private static Map<String, CmdPackage> cmdRegistry = new HashMap<String, CmdPackage>();
	
	static {
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdHelp.NAME), 
			new CmdPackage(new RemoteCmdHelp.CmdDsc(), RemoteCmdHelp.class));
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdVersion.NAME), 
			new CmdPackage(new RemoteCmdVersion.CmdDsc(), RemoteCmdVersion.class));
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdLocalization.NAME), 
			new CmdPackage(new RemoteCmdLocalization.CmdDsc(), RemoteCmdLocalization.class));
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdHeartrate.NAME), 
			new CmdPackage(new RemoteCmdHeartrate.CmdDsc(), RemoteCmdHeartrate.class));
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdTracking.NAME), 
			new CmdPackage(new RemoteCmdTracking.CmdDsc(), RemoteCmdTracking.class));
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdConfig.NAME), 
			new CmdPackage(new RemoteCmdConfig.CmdDsc(), RemoteCmdConfig.class));
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdUpload.NAME), 
			new CmdPackage(new RemoteCmdUpload.CmdDsc(), RemoteCmdUpload.class));
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdStatus.NAME), 
			new CmdPackage(new RemoteCmdStatus.CmdDsc(), RemoteCmdStatus.class));
	}

	public static boolean containsCommand(String commandStr) {
		return cmdRegistry.containsKey(commandStr);
	}
	
	public static String getCommandsAsStr() {
		String commandsAsStr = "";
		Set<String> commands = cmdRegistry.keySet();
		for (Iterator<String> it=commands.iterator(); it.hasNext();) {
			commandsAsStr += it.next();
			if (it.hasNext()) {
				commandsAsStr += ", ";
			}
		}
		return commandsAsStr;
	}
	
	public static String getCommandSyntax(String commandStr) {
		if (StringUtils.isEmpty(commandStr)) {
			throw new IllegalArgumentException("commandStr must not be empty.");
		}
		if (!cmdRegistry.containsKey(commandStr)) {
			throw new IllegalArgumentException("unknown commandStr.");
		}
		CmdPackage cmdPackage = cmdRegistry.get(commandStr);
		return cmdPackage.dsc.getDescription();
	}
	
	private static boolean cmdExecutorExists(String cmdName) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be empty.");
		}
		LogUtils.infoMethodIn(RemoteCmdReceiver.class, "cmdExecutorExists", cmdName);
		boolean res = cmdRegistry.containsKey(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(RemoteCmdReceiver.class, "cmdExecutorExists", res);
		return res;
	}
	
	private static CmdPackage getCmdExecutor(String cmdName) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be empty.");
		}
		LogUtils.infoMethodIn(RemoteCmdReceiver.class, "getCmdExecutor", cmdName);
		CmdPackage res = 
			cmdRegistry.get(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(RemoteCmdReceiver.class, "getCmdExecutor", res);
		return res;
	}
	
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	public static final String CMD_INDICATOR = "#mlt";
	
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
					!StringUtils.isEmpty(message) && StringUtils.startsWithIgnoreCase(message, CMD_INDICATOR)) {
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
					} else if (!cmdExecutorExists(messageParts[2])) {
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
						CmdPackage cmdPackage = getCmdExecutor(messageParts[2]);
						Constructor<? extends ARemoteCmdExecutor> cmdExecutorConstructor = cmdPackage.executor.getConstructor();
						ARemoteCmdExecutor cmdExecutor = cmdExecutorConstructor.newInstance();
						cmdExecutor.init(cmdPackage.dsc, sender, params);
						executorService.execute(cmdExecutor);
						response = null;
						LogUtils.infoMethodState(this.getClass(), "onReceive", "command executed");
					}
				}
			} catch (Exception e) {
				response = ResponseCreator.getResultOfError(e.getMessage());
			} finally {
				if (!StringUtils.isEmpty(response)) {
					RemoteCmdError cmdError = new RemoteCmdError();
					cmdError.init(new RemoteCmdError.CmdDsc(), sender, response);
					executorService.execute(cmdError);
					LogUtils.infoMethodState(this.getClass(), "onReceive", "command failed", sender, response);
				}
			}
		}

		LogUtils.infoMethodOut(this.getClass(), "onReceive");
	}
}	
