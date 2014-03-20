package de.msk.mylivetracker.client.android.remoteaccess.commands;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.exit.ExitService;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;


/**
 * classname: RemoteCmdExit
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-18	origin.
 * 
 */
public class RemoteCmdApp extends ARemoteCmdExecutor {

	public static String NAME = "app";
	public static enum Options {
		start, stop, status;
	}
	public static String SYNTAX = 
		Options.start.name() + ARemoteCmdDsc.OPT_SEP +
		Options.stop.name() + " " + "[<timeout in seconds>]" + 
		ARemoteCmdDsc.OPT_SEP + Options.status.name();
	
	public static final int STOP_DEF_TIMEOUT_IN_SECS = 10;
	public static final int STOP_MAX_TIMEOUT_IN_SECS = 300;
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, SYNTAX, 1, 2, 
				R.string.txRemoteCommand_App, false);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			boolean matches = 
				(params.length >= 1) &&	
				(params.length <= 2) && 
				EnumUtils.isValidEnum(Options.class, params[0]);
			
			if (params.length == 2) {
				matches = 
					StringUtils.equals(params[0], Options.stop.name()) &&
					StringUtils.isNumeric(params[1]); 
				if (matches) {
					int timeoutInSecs = Integer.valueOf(params[1]);
					if (timeoutInSecs > STOP_MAX_TIMEOUT_IN_SECS) {
						matches = false;
					}	
				}
			}
			return matches;
		}
	}

	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		Result result = null;
		if (StringUtils.equals(params[0], Options.start.name())) {
			if (MainActivity.exists()) {
				result = new Result(false, "application already started");
			} else {
				App.start();
				result = new Result(true, "application started");
			}
		} else if (StringUtils.equals(params[0], Options.status.name())) {
			result = new Result(true, ResponseCreator.addParamValue(
				"", "application", (App.running() ? "running" : "not running")));
		} else if (StringUtils.equals(params[0], Options.stop.name())) {
			if (!MainActivity.exists()) {
				result = new Result(false, "application already stopped");
			} else {
				int timeoutInSecs = STOP_DEF_TIMEOUT_IN_SECS;
				if (params.length == 2) { 
					timeoutInSecs = Integer.valueOf(params[1]);
				} 
				ExitService.markAsExit(timeoutInSecs * 1000L);
				result = new Result(true, "application stopped");
			}
		}
		return result;
	}
}
