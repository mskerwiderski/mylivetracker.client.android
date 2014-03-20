package de.msk.mylivetracker.client.android.remoteaccess;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import android.content.BroadcastReceiver;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdApp;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdConfig;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdHeartrate;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdHelp;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdLocalization;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdStatus;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdTracking;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdUpload;
import de.msk.mylivetracker.client.android.remoteaccess.commands.RemoteCmdVersion;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: ARemoteCmdReceiver
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
public abstract class ARemoteCmdReceiver extends BroadcastReceiver {

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
		cmdRegistry.put(StringUtils.lowerCase(RemoteCmdApp.NAME), 
			new CmdPackage(new RemoteCmdApp.CmdDsc(), RemoteCmdApp.class));
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
		LogUtils.infoMethodIn(ARemoteCmdReceiver.class, "cmdExecutorExists", cmdName);
		boolean res = cmdRegistry.containsKey(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(ARemoteCmdReceiver.class, "cmdExecutorExists", res);
		return res;
	}
	
	private static CmdPackage getCmdExecutor(String cmdName) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be empty.");
		}
		LogUtils.infoMethodIn(ARemoteCmdReceiver.class, "getCmdExecutor", cmdName);
		CmdPackage res = 
			cmdRegistry.get(StringUtils.lowerCase(cmdName));
		LogUtils.infoMethodOut(ARemoteCmdReceiver.class, "getCmdExecutor", res);
		return res;
	}
	
	public void onProcessRemoteCmd(String sender, String[] messageParts) {
		LogUtils.infoMethodIn(this.getClass(), "onProcessRemoteCmd");
		String response = null;
		if (StringUtils.isEmpty(sender)) {
			throw new IllegalArgumentException("sender must not be empty.");
		}
		if (messageParts == null) {
			throw new IllegalArgumentException("messageParts must not be null.");
		}
		try {
			LogUtils.infoMethodState(this.getClass(), "onProcessCmd", "message parts", Arrays.toString(messageParts));
			if (!cmdExecutorExists(messageParts[0])) {
				response = "unknown command";
			} else {
				CmdPackage cmdPackage = getCmdExecutor(messageParts[0]);
				if (cmdPackage.dsc.isNeedsAppRunning() && !App.running()) {
					response = "command needs app running (please start app first)";
				} else {
					String[] params = new String[0];
					if (messageParts.length > 1) {
						String[] paramsCs = (String[])ArrayUtils.subarray(messageParts, 1, messageParts.length);
						params = new String[paramsCs.length];
						for (int i=0; i < paramsCs.length; i++) {
							params[i] = StringUtils.lowerCase(paramsCs[i]);
						}
					}
					LogUtils.infoMethodState(this.getClass(), "onProcessCmd", "params", Arrays.toString(params));
					Constructor<? extends ARemoteCmdExecutor> cmdExecutorConstructor = cmdPackage.executor.getConstructor();
					ARemoteCmdExecutor cmdExecutor = cmdExecutorConstructor.newInstance();
					cmdExecutor.init(cmdPackage.dsc, sender, params, 
						this.getResponseSender(), this.getExecutorService());
					this.getExecutorService().execute(cmdExecutor);
					response = null;
					LogUtils.infoMethodState(this.getClass(), "onProcessCmd", "command executed");
				}
			}
		} catch (Exception e) {
			response = "failed:internal error";
		} finally {
			if (!StringUtils.isEmpty(response)) {
				response = "["+ messageParts[0] +"]:failed:" + response;
				this.getExecutorService().execute(new ErrorResponse(sender, this.getResponseSender(), response));
				LogUtils.infoMethodState(this.getClass(), "onProcessCmd", "sendResponse", response);
			}
		}
		LogUtils.infoMethodOut(this.getClass(), "onProcessRemoteCmd");
	}
	
	private class ErrorResponse implements Runnable {

		private String sender;
		private IResponseSender responseSender;
		private String response;
		
		public ErrorResponse(String sender, IResponseSender responseSender, String response) {
			this.sender = sender;
			this.responseSender = responseSender;
			this.response = response;
		}

		@Override
		public void run() {
			this.responseSender.sendResponse(this.sender, response);			
		}
	}
	
	public abstract IResponseSender getResponseSender();
	public abstract ExecutorService getExecutorService();
}	
