package de.msk.mylivetracker.client.android.remoteaccess.commands;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.util.LogUtils;


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
		LogUtils.infoMethodIn(RemoteCmdMobNwCell.class, "executeCmdAndCreateResponse");
		PhoneStateInfo phoneStateInfo = PhoneStateInfo.get();
		Result res = null;
		if (phoneStateInfo != null) { 
			String resStr = ResponseCreator.addTimestampValue("", 
				phoneStateInfo.getTimestamp());
			resStr = ResponseCreator.addParamValue(resStr, 
				"phone", phoneStateInfo.getPhoneType());
			resStr = ResponseCreator.addParamValue(resStr,  
				"network", phoneStateInfo.getNetworkType());
			resStr = ResponseCreator.addParamValue(resStr, 
				"mcc", phoneStateInfo.getMobileCountryCode());	
			resStr = ResponseCreator.addParamValue(resStr, 
				"mnc", phoneStateInfo.getMobileNetworkCode());
			resStr = ResponseCreator.addParamValue(resStr, 
				"mnn", phoneStateInfo.getMobileNetworkName());
			resStr = ResponseCreator.addParamValue(resStr, 
				"lac", phoneStateInfo.getLocalAreaCode());
			resStr = ResponseCreator.addParamValue(resStr, 
				"cellid", phoneStateInfo.getCellId());
			res = new Result(true, resStr);
		} else {
			res = new Result(false, "no valid cell information found");
		}
		LogUtils.infoMethodOut(RemoteCmdMobNwCell.class, res.toString());
		return res;
	}
}
