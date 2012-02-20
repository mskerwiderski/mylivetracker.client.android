package de.msk.mylivetracker.client.android.receiver;

import org.apache.commons.lang.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;

/**
 * BatteryReceiver.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2011-11-27 bugfix: expected battery voltage must be 4 digits long.
 * 000 	2011-08-11 initial.
 * 
 */
public class BatteryReceiver extends BroadcastReceiver {

	private static BatteryReceiver batteryReceiver = null;
	private boolean active = false;
	
	public static BatteryReceiver get() {
		if (batteryReceiver == null) {
			batteryReceiver = new BatteryReceiver();			
		} 
		return batteryReceiver;
	}
	
	private boolean batteryCharging = false;
	
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		int istate = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 
			BatteryManager.BATTERY_STATUS_UNKNOWN);
		int ilevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int iscale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        BatteryStateInfo.State state = BatteryStateInfo.State.Unknown;
        if (istate == BatteryManager.BATTERY_STATUS_CHARGING){
        	state = BatteryStateInfo.State.Charging;
        	batteryCharging = true;
        } else if (istate == BatteryManager.BATTERY_STATUS_DISCHARGING){
        	state = BatteryStateInfo.State.Discharging;
        	batteryCharging = false;
        } else if (istate == BatteryManager.BATTERY_STATUS_NOT_CHARGING){
        	state = BatteryStateInfo.State.NotCharging;
        	batteryCharging = false;
        } else if (istate == BatteryManager.BATTERY_STATUS_FULL){
        	state = BatteryStateInfo.State.Full;
        	batteryCharging = true;
        } else {
        	state = BatteryStateInfo.State.Unknown;
        	batteryCharging = false;
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
        
        // 001
        String voltStr = String.valueOf(ivolt);
        voltStr = StringUtils.rightPad(voltStr, 4, '0');
        ivolt = Integer.valueOf(voltStr);
        
        Double voltage = null;
        if (ivolt != -1) {
        	voltage = Math.round(ivolt / 1000.0d * 100.0d) / 100d;
        }        
        BatteryStateInfo.update(state, percent, temperature, voltage);
        MainActivity.logInfo("BatteryReceiver: battery charging=" + isBatteryCharging());
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isBatteryCharging() {
		return batteryCharging;
	}	
}
