package de.msk.mylivetracker.client.android.remoteaccess.commands;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.battery.BatteryReceiver;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.util.FormatUtils;


/**
 * classname: RemoteCmdBattery
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.7.0
 * 
 * history:
 * 000	2014-04-12	origin.
 * 
 */
public class RemoteCmdBattery extends ARemoteCmdExecutor {

	public static String NAME = "batt";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, "", 0, 0, 
				R.string.txRemoteCommand_Battery, false);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		BatteryReceiver.updateBatteryStateInfo();
		BatteryStateInfo batteryStateInfo = BatteryStateInfo.get();
		String res = ResponseCreator.addParamValue("", 
			"state", batteryStateInfo.getState().getLabel());
		res = ResponseCreator.addIntValue(res, 
			"capacity", 
			batteryStateInfo.getPercent(), 
			FormatUtils.Unit.PercentAsTxt);
		res = ResponseCreator.addDoubleValue(res, 
			"power", 
			batteryStateInfo.getVoltage(), 2,
			FormatUtils.Unit.Volt);	
		res = ResponseCreator.addIntValue(res, 
			"temp", 
			batteryStateInfo.getDegrees(),
			FormatUtils.Unit.DegreeCelsiusAsTxt);
		return new Result(true, res);
	}
}
