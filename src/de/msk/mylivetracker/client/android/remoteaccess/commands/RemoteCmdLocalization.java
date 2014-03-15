package de.msk.mylivetracker.client.android.remoteaccess.commands;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.util.LocalizationUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

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
		start, stop, info, detect;
	}
	public static final int DETECT_MAX_TIMEOUT_IN_SECS = 180;
	public static final String SYNTAX = 
		Options.start.name() + ARemoteCmdDsc.OPT_SEP + 
		Options.stop.name() + ARemoteCmdDsc.OPT_SEP +
		"(" + Options.info.name() + "[detect [<timeout in secs>]])";
	
	public static class CmdDsc extends ARemoteCmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 1, 3, 
				R.string.txRemoteCommand_Localization);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			boolean matches = true;
			if ((params.length < 1) || (params.length > 3)) {
				matches = false;
			}		
			if (matches) {
				matches = 
					(StringUtils.equals(params[0], Options.start.name()) && 
						(params.length == 1)) ||
					(StringUtils.equals(params[0], Options.stop.name()) && 
						(params.length == 1)) ||
					(StringUtils.equals(params[0], Options.info.name()) && 
						(params.length >= 1) && (params.length <= 3));
			}
			if (matches && 
				StringUtils.equals(params[0], Options.info.name()) && 
				(params.length > 1)) {
				if (!StringUtils.equals(params[1], Options.detect.name())) {
					matches = false;
				} else if (params.length == 3) {
					if (!StringUtils.isNumeric(params[2])) {
						matches = false;
					} else {
						int timeoutInSecs = Integer.valueOf(params[2]);
						if (timeoutInSecs > DETECT_MAX_TIMEOUT_IN_SECS) {
							matches = false;
						}
					}
				}
			}
			return matches;
		}
	}
	
	@Override
	public String executeCmdAndCreateResponse(String... params) {
		String response = "";
		boolean localizationFoundActive = 
			AbstractService.isServiceRunning(LocalizationService.class);
		if (StringUtils.equals(params[0], Options.start.name())) {
			if (!localizationFoundActive) {
				LocalizationUtils.startLocalization();
				response = "localization started";
			} else {
				response = "localization already running";
			}
		} else if (StringUtils.equals(params[0], Options.stop.name())) {
			if (localizationFoundActive) {
				LocalizationUtils.stopLocalization();
				response = "localization stopped";
			} else {
				response = "localization already stopped";
			}
		} else if (StringUtils.equals(params[0], Options.info.name())) {
			boolean detect = (params.length > 1);
			int timeoutInSecs = ((params.length == 3) ? 
				Integer.valueOf(params[2]) : DETECT_MAX_TIMEOUT_IN_SECS);
			
			if (detect) {
				if (!localizationFoundActive) {
					LocalizationUtils.startLocalization();
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
						locationInfo.isUpToDate() && locationInfo.isAccurate()) {
						cancel = true;
					}
					curr = (new Date()).getTime();
				}
				
				if (!localizationFoundActive) {
					LocalizationUtils.stopLocalization();
				}			
			}
			response = ResponseCreator.getResultOfGetLocation(LocationInfo.get());
		}
		return response;
	}
}
