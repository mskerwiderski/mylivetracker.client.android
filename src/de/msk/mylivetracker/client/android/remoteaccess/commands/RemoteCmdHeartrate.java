package de.msk.mylivetracker.client.android.remoteaccess.commands;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;

/**
 * classname: RemoteCmdHeartrate
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.6.0
 * 
 * history:
 * 001	2014-03-09	origin.
 * 
 */
public class RemoteCmdHeartrate extends ARemoteCmdExecutor {

	public static final String NAME = "hr";
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
	public String executeCmdAndCreateSmsResponse(String... params) {
		String response = "";
		if (!AntPlusHardware.initialized()) {
			response = ResponseCreator.getResultOfHeartrateDetectionNotSupported();
		} else if (!PrefsRegistry.get(OtherPrefs.class).isAntPlusEnabledIfAvailable()) {
			response = ResponseCreator.getResultOfHeartrateDetectionNotEnabled();
		} else {
			boolean antPlusFoundActive = 
				AntPlusManager.get().hasSensorListeners();
			if (StringUtils.equals(params[0], Options.start.name())) {
				if (!antPlusFoundActive) {
					AntPlusManager.start();
					response = "heartrate detection started";
				} else {
					response = "heartrate detection already running";
				}
			} else if (StringUtils.equals(params[0], Options.stop.name())) {
				if (antPlusFoundActive) {
					AntPlusManager.stop();
					response = "heartrate detection stopped";
				} else {
					response = "heartrate detection already stopped";
				}
			} else if (StringUtils.equals(params[0], Options.info.name())) {
				boolean detect = (params.length > 0);
				int timeoutInSecs = ((params.length == 3) ? 
					Integer.valueOf(params[2]) : DETECT_MAX_TIMEOUT_IN_SECS);
				if (detect) {
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
				response = ResponseCreator.getResultOfGetHeartrate(HeartrateInfo.get());
			}
		}			
		return response;
	}
}
