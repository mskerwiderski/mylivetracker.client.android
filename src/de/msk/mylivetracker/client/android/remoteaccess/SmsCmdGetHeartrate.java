package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;

/**
 * classname: SmsCmdGetHeartrate
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.6.0
 * 
 * history:
 * 001	2014-03-09	origin.
 * 
 */
public class SmsCmdGetHeartrate extends ASmsCmdExecutor {

	public static String NAME = "hr";
	public static String SYNTAX = "start|stop|(info[detect [<timeout in secs>]])";
	
	public static class CmdDsc extends ARemoteCmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 0, 0);
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
						if (timeoutInSecs > 180) {
							matches = false;
						}
					}
				}
			}
			return matches;
		}
	}
	
	public SmsCmdGetHeartrate(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}
	
	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		boolean detect = (params.length > 0);
		int timeoutInSecs = ((params.length == 2) ? Integer.valueOf(params[1]) : 180);
		
		if (detect) {
			boolean antPlusFoundActive = 
				AntPlusManager.get().hasSensorListeners();
			
			if (!antPlusFoundActive) {
				AntPlusManager.start();
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
				HeartrateInfo heartrateInfo = HeartrateInfo.get();
				if ((heartrateInfo != null) && heartrateInfo.hasValidHrData() && 
					heartrateInfo.isUpToDate()) {
					cancel = true;
				}
				curr = (new Date()).getTime();
			}
			
			if (!antPlusFoundActive) {
				AntPlusManager.stop();
			}
		}
		
		return ResponseCreator.getResultOfGetHeartrate(HeartrateInfo.get());
	}
}
