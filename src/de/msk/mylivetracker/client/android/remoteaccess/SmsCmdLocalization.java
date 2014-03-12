package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.util.LocalizationUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: SmsCmdLocalization
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
public class SmsCmdLocalization extends ASmsCmdExecutor {

	public static String NAME = "loc";
	public static String SYNTAX = "start|stop|(info[detect [<timeout in secs>]])";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 0, 2);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			boolean matches = true;
			if ((params.length < 1) || (params.length > 3)) {
				matches = false;
			}		
			if (matches) {
				matches = 
					(StringUtils.equals(params[0], "start") && 
						(params.length == 1)) ||
					(StringUtils.equals(params[0], "stop") && 
						(params.length == 1)) ||
					(StringUtils.equals(params[0], "info") && 
						((params.length == 1) || (params.length == 3)));
			}
			if (matches && 
				StringUtils.equals(params[0], "info") && 
				(params.length > 1)) {
				if (!StringUtils.equals(params[1], "detect")) {
					matches = false;
				} else if (params.length == 3) {
					if (!StringUtils.isNumeric(params[1])) {
						matches = false;
					} else {
						int timeoutInSecs = Integer.valueOf(params[1]);
						if (timeoutInSecs > 180) {
							matches = false;
						}
					}
				}
			}
			return matches;
		}
	}
	
	public SmsCmdLocalization(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	
	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		String response = "";
		boolean localizationFoundActive = 
			AbstractService.isServiceRunning(LocalizationService.class);
		if (StringUtils.equals(params[0], "start")) {
			if (!localizationFoundActive) {
				LocalizationUtils.startLocalization();
				response = "localization started";
			}
			response = "localization already running";
		} else if (StringUtils.equals(params[0], "stop")) {
			if (localizationFoundActive) {
				LocalizationUtils.stopLocalization();
				response = "localization stopped";
			}
			response = "localization already stopped";
		} else if (StringUtils.equals(params[0], "info")) {
			boolean detect = (params.length > 1);
			int timeoutInSecs = ((params.length == 2) ? Integer.valueOf(params[2]) : 180);
			
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
