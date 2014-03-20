package de.msk.mylivetracker.client.android.remoteaccess.commands;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.service.AbstractService;


/**
 * classname: RemoteCmdStatus
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-14	origin.
 * 
 */
public class RemoteCmdStatus extends ARemoteCmdExecutor {

	public static String NAME = "stat";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, "", 0, 0, 
				R.string.txRemoteCommand_Status);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		String res = ResponseCreator.addParamValue("", "tracking", 
			(TrackStatus.get().trackIsRunning() ? "running" : "idle"));
		res = ResponseCreator.addParamValue(res, "localization", 
			(AbstractService.isServiceRunning(LocalizationService.class) ? "running" : "idle"));
		if (AntPlusHardware.initialized()) {
			res = ResponseCreator.addParamValue(res, "heartrate detection",
				(!PrefsRegistry.get(OtherPrefs.class).isAntPlusEnabledIfAvailable()) ? 
					"disabled (maybe ANT+ driver not installed)" : 
					(AntPlusManager.get().hasSensorListeners() ? "running" : "idle"));
		} 
		return new Result(true, res);
	}
}
