package de.msk.mylivetracker.client.android.remoteaccess.commands;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.LocationInfo;

/**
 * classname: RemoteCmdLocalization
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
public class RemoteCmdLocalization extends ARemoteCmdExecutor {

	public static final String NAME = "loc";
	public static enum Options {
		start, stop, info, detect, acc;
	}
	public static final int DETECT_DEF_TIMEOUT_IN_SECS = 60;
	public static final int DETECT_MAX_TIMEOUT_IN_SECS = 300;
	public static final String SYNTAX = 
		Options.start.name() + ARemoteCmdDsc.OPT_SEP + 
		Options.stop.name() + ARemoteCmdDsc.OPT_SEP +
		"(" + Options.info.name() + "[" + Options.detect + 
		"[" + Options.acc + "] [<timeout in secs>]])";
	
	public static class CmdDsc extends ARemoteCmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 1, 3, 
				R.string.txRemoteCommand_Localization, 
				false);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			boolean matches = true;
			if ((params.length < 1) || (params.length > 4)) {
				matches = false;
			}		
			if (matches) {
				matches = 
					(StringUtils.equals(params[0], Options.start.name()) && 
						(params.length == 1)) ||
					(StringUtils.equals(params[0], Options.stop.name()) && 
						(params.length == 1)) ||
					(StringUtils.equals(params[0], Options.info.name()) && 
						(params.length >= 1) && (params.length <= 4));
			}
			if (matches && 
				StringUtils.equals(params[0], Options.info.name()) && 
				(params.length >= 2)) {
				if (!StringUtils.equals(params[1], Options.detect.name())) {
					matches = false;
				} else {
					if (params.length == 3) {
						if (!StringUtils.equals(params[2], Options.acc.name()) && 
							!StringUtils.isNumeric(params[2])) {
							matches = false;
						} else if (StringUtils.isNumeric(params[2])) {
							int timeoutInSecs = Integer.valueOf(params[2]);
							if (timeoutInSecs > DETECT_MAX_TIMEOUT_IN_SECS) {
								matches = false;
							}	
						}
					} else if (params.length == 4) {
						if (!StringUtils.equals(params[2], Options.acc.name()) || 
							!StringUtils.isNumeric(params[3])) {
							matches = false;
						} else if (StringUtils.isNumeric(params[3])) {
							int timeoutInSecs = Integer.valueOf(params[3]);
							if (timeoutInSecs > DETECT_MAX_TIMEOUT_IN_SECS) {
								matches = false;
							}	
						}
					}
				}
			}
			return matches;
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		Result result = null;
		boolean localizationFoundActive = 
			AppControl.localizationIsRunning();
		if (StringUtils.equals(params[0], Options.start.name())) {
			if (!localizationFoundActive) {
				AppControl.startLocalization();
				result = new Result(true, "localization started");
			} else {
				result = new Result(true, "localization already running");
			}
		} else if (StringUtils.equals(params[0], Options.stop.name())) {
			if (localizationFoundActive) {
				AppControl.stopLocalization();
				result = new Result(true, "localization stopped");
			} else {
				result = new Result(true, "localization already stopped");
			}
		} else if (StringUtils.equals(params[0], Options.info.name())) {
			boolean detect = (params.length > 1);
			boolean accurate = (params.length > 2) && 
				StringUtils.equals(params[2], Options.acc.name());
			int timeoutInSecs = DETECT_DEF_TIMEOUT_IN_SECS;
			if (!accurate && (params.length == 3)) { 
				timeoutInSecs = Integer.valueOf(params[2]);
			} else if (accurate && (params.length == 4)) { 
				timeoutInSecs = Integer.valueOf(params[3]);
			}
			
			if (detect) {
				if (!localizationFoundActive) {
					AppControl.startLocalization();
				}
				
				long curr = (new Date()).getTime();
				long stop = curr + timeoutInSecs * 1000L;
				boolean cancel = false;
				while (!cancel && (curr < stop)) {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						cancel = true;
					}
					LocationInfo locationInfo = LocationInfo.get();
					
					if ((locationInfo != null) && locationInfo.hasValidLatLon() && 
						locationInfo.isUpToDate() && 
						(!accurate || locationInfo.isAccurate())) {
						cancel = true;
					}
					curr = (new Date()).getTime();
				}
				
				if (!localizationFoundActive) {
					AppControl.stopLocalization();
				}			
			}
			result = ResponseCreator.getResultOfGetLocation(LocationInfo.get(), accurate);
		}
		return result;
	}
}
