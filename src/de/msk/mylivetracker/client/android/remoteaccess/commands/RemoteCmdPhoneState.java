package de.msk.mylivetracker.client.android.remoteaccess.commands;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.battery.BatteryReceiver;
import de.msk.mylivetracker.client.android.phonestate.PhoneStateReceiver;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.util.FormatUtils;


/**
 * classname: RemoteCmdPhoneState
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.7.0
 * 
 * history:
 * 000	2014-04-12	origin.
 * 
 */
public class RemoteCmdPhoneState extends ARemoteCmdExecutor {

	public static String NAME = "phstat";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, "", 0, 0, 
				R.string.txRemoteCommand_Status, false);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		BatteryReceiver.updateBatteryStateInfo();
		PhoneStateReceiver.updatePhoneStateInfo();
		
		BatteryStateInfo batteryStateInfo = BatteryStateInfo.get();
		PhoneStateInfo phoneStateInfo = PhoneStateInfo.get();
		
		String res = ResponseCreator.addParamValue("", "batt", 
			FormatUtils.getDoubleAsSimpleStr(batteryStateInfo.getVoltage(), 2));	
		res = ResponseCreator.addParamValue(res, "mcc", 
			phoneStateInfo.getMobileCountryCode());

		return new Result(true, res);
	}
}
