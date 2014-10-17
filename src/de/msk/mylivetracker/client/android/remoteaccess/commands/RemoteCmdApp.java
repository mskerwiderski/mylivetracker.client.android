package de.msk.mylivetracker.client.android.remoteaccess.commands;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.auto.AutoService;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

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
		start, exit, auto, status;
	}
	public static String SYNTAX = 
		Options.start.name() + ARemoteCmdDsc.OPT_SEP +
		Options.exit.name() + " " + "[<timeout in seconds>]" + ARemoteCmdDsc.OPT_SEP +
		Options.auto.name() + ARemoteCmdDsc.OPT_SEP +
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
					StringUtils.equals(params[0], Options.exit.name()) &&
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
			if (AppControl.appRunningFrontend()) {
				result = new Result(false, "application already running");
			} else {
				AppControl.startApp();
				result = new Result(true, "application started");
			}
		} else if (StringUtils.equals(params[0], Options.auto.name())) {
			if (!TrackingModePrefs.isAuto()) {
				result = new Result(false, "tracking mode is not set to 'auto'");
			} else if (AppControl.appRunning()) {
				result = new Result(false, "tracking mode 'auto' already running");
			} else {
				AbstractService.startService(AutoService.class);
				result = new Result(true, "tracking mode 'auto' started");
			}
		} else if (StringUtils.equals(params[0], Options.status.name())) {
			String status = "application ";
			if (!AppControl.appRunning()) {
				status += "not running";
			} else {
				if (AppControl.appRunningFrontend()) {
					status += "running (frontend)";
				} else {
					status += "running (without frontend)";
				}
			} 
			result = new Result(true, status);
		} else if (StringUtils.equals(params[0], Options.exit.name())) {
			if (!AppControl.appRunning()) {
				result = new Result(false, "application not running");
			} else {
				int timeoutInSecs = STOP_DEF_TIMEOUT_IN_SECS;
				if (params.length == 2) { 
					timeoutInSecs = Integer.valueOf(params[1]);
				} 
				AppControl.exitApp(timeoutInSecs * 1000);
				result = new Result(true, "application has been exit");
			}
		}
		return result;
	}
}
