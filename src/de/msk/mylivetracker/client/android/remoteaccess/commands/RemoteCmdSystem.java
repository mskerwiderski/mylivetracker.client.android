package de.msk.mylivetracker.client.android.remoteaccess.commands;

import org.apache.commons.lang3.BooleanUtils;

import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.SystemInfo;


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
public class RemoteCmdSystem extends ARemoteCmdExecutor {

	public static String NAME = "sys";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, "", 0, 0, 
				R.string.txRemoteCommand_System, false);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		SystemInfo.update();
		SystemInfo systemInfo = SystemInfo.get();
		String res = ResponseCreator.addParamValue("", "BatRcv", 
			BooleanUtils.toString(systemInfo.isBatteryReceiverRegistered(), "1", "0"));
		res = ResponseCreator.addParamValue(res, "PhSRcv", 
			BooleanUtils.toString(systemInfo.isPhoneStateReceiverRegistered(), "1", "0"));
		res = ResponseCreator.addParamValue(res, "VwUSvc", 
			BooleanUtils.toString(systemInfo.isViewUpdateServiceRunning(), "1", "0"));
		res = ResponseCreator.addParamValue(res, "UplSvc", 
			BooleanUtils.toString(systemInfo.isUploadServiceRunning(), "1", "0"));
		res = ResponseCreator.addParamValue(res, "LocSvc", 
			BooleanUtils.toString(systemInfo.isLocalizationServiceRunning(), "1", "0"));
		res = ResponseCreator.addParamValue(res, "AutSvc", 
			BooleanUtils.toString(systemInfo.isAutoServiceRunning(), "1", "0"));
		return new Result(true, res);
	}
}
