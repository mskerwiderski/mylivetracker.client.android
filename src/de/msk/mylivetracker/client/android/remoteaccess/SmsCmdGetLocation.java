package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.util.LocalizationUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: SmsCmdGetLocation
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
public class SmsCmdGetLocation extends ASmsCmdExecutor {

	public static String NAME = "getloc";
	public static String SYNTAX = "[detect [<timeout in secs>]]";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 0, 2);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			boolean matches = true;
			if (params.length > 2) {
				matches = false;
			}
			if (matches && (params.length > 0)) {
				if (!StringUtils.equals(params[0], "detect")) {
					matches = false;
				} else if (params.length == 2) {
					if (!StringUtils.isNumeric(params[1])) {
						matches = false;
					} else {
						int timeoutInSecs = Integer.valueOf(params[1]);
						if (timeoutInSecs > 900) {
							matches = false;
						}
					}
				}
			}
			return matches;
		}
		
	}
	
	public SmsCmdGetLocation(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	
	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		boolean detect = (params.length > 0);
		int timeoutInSecs = ((params.length == 2) ? Integer.valueOf(params[1]) : 180);
		
		if (detect) {
			boolean localizationFoundActive = 
				AbstractService.isServiceRunning(LocalizationService.class);
			
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
		
		return ResponseCreator.getResultOfGetLocation(LocationInfo.get());
	}
}
