package de.msk.mylivetracker.client.android.remoteaccess.commands;

import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.phonestate.PhoneStateReceiver;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;


/**
 * classname: RemoteCmdMobNwCell
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.7.0
 * 
 * history:
 * 000	2014-04-21	origin.
 * 
 */
public class RemoteCmdMobNwCell extends ARemoteCmdExecutor {

	public static String NAME = "cell";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, "", 0, 0, 
				R.string.txRemoteCommand_MobNwCell, false);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		PhoneStateReceiver.updatePhoneStateInfo();
		PhoneStateInfo phoneStateInfo = PhoneStateInfo.get();
		String res = ResponseCreator.addParamValue("", 
			"phone", phoneStateInfo.getPhoneType());
		res = ResponseCreator.addParamValue(res,  
			"network", phoneStateInfo.getNetworkType());
		res = ResponseCreator.addParamValue(res, 
			"mcc", phoneStateInfo.getMobileCountryCode());	
		res = ResponseCreator.addParamValue(res, 
			"mnc", phoneStateInfo.getMobileNetworkCode());
		res = ResponseCreator.addParamValue(res, 
			"mnn", phoneStateInfo.getMobileNetworkName());
		res = ResponseCreator.addParamValue(res, 
			"lac", phoneStateInfo.getLocalAreaCode());
		res = ResponseCreator.addParamValue(res, 
			"cellid", phoneStateInfo.getCellId());
		return new Result(true, res);
	}
}
