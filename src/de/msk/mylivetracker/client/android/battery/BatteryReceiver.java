package de.msk.mylivetracker.client.android.battery;

import org.apache.commons.lang.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;

/**
 * classname: BatteryReceiver
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class BatteryReceiver extends BroadcastReceiver {

	private static BatteryReceiver batteryReceiver = null;
	
	public static void start() {
		if (batteryReceiver == null) {
			batteryReceiver = new BatteryReceiver();
			IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	        App.getCtx().registerReceiver(batteryReceiver, filter);
		}
	}
	
	public static void stop() {
		if (batteryReceiver != null) {
			App.getCtx().unregisterReceiver(batteryReceiver);
			batteryReceiver = null;
		}
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		int istate = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 
			BatteryManager.BATTERY_STATUS_UNKNOWN);
		int ilevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int iscale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        BatteryStateInfo.State state = BatteryStateInfo.State.Unknown;
        if (istate == BatteryManager.BATTERY_STATUS_CHARGING){
        	state = BatteryStateInfo.State.Charging;
        } else if (istate == BatteryManager.BATTERY_STATUS_DISCHARGING){
        	state = BatteryStateInfo.State.Discharging;
        } else if (istate == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
        	state = BatteryStateInfo.State.NotCharging;
        } else if (istate == BatteryManager.BATTERY_STATUS_FULL){
        	state = BatteryStateInfo.State.Full;
        } else {
        	state = BatteryStateInfo.State.Unknown;
        }
        Integer percent = null;
        if ((ilevel != -1) && (iscale != -1)) {
        	percent = Math.round(ilevel*1.0f / iscale*1.0f * 100.0f);
        }
        int itemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        Integer temperature = null;
        if (itemp != -1) {
        	temperature = Math.round(itemp / 10.0f);
        }
        int ivolt = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        
        String voltStr = String.valueOf(ivolt);
        voltStr = StringUtils.rightPad(voltStr, 4, '0');
        ivolt = Integer.valueOf(voltStr);
        
        Double voltage = null;
        if (ivolt != -1) {
        	voltage = Math.round(ivolt / 1000.0d * 100.0d) / 100d;
        }        
        BatteryStateInfo.update(state, percent, temperature, voltage);
	}
}
