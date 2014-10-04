package de.msk.mylivetracker.client.android.battery;

import org.apache.commons.lang3.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.util.LogUtils;

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
	
	public static boolean isRegistered() {
		return (batteryReceiver != null);
	}
		
	public static void reset() {
		if (isRegistered()) {
			unregister();
			register();
		}
	}
	
	public static void register() {
		if (!isRegistered()) {
			batteryReceiver = new BatteryReceiver();
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_BATTERY_CHANGED);
			filter.addAction(Intent.ACTION_POWER_CONNECTED);
			filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
	        App.getCtx().registerReceiver(batteryReceiver, filter);
	        LogUtils.infoMethodState(BatteryReceiver.class,
				"register", "battery receiver", "registered");
		}
	}
	
	public static void unregister() {
		if (isRegistered()) {
			App.getCtx().unregisterReceiver(batteryReceiver);
			batteryReceiver = null;
			LogUtils.infoMethodState(BatteryReceiver.class,
				"unregister", "battery receiver", "unregistered");
		}
	}
	
	public static void updateBatteryStateInfo() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent intent = App.getCtx().registerReceiver(null,filter);
		updateBatteryStateInfo(intent);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		updateBatteryStateInfo(intent);
	}
	
	private static void updateBatteryStateInfo(Intent intent) {
		LogUtils.infoMethodIn(BatteryReceiver.class, 
			"updateBatteryStateInfo", intent.getAction());
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
        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        
        BatteryStateInfo.update(state, percent, 
        	temperature, voltage, usbCharge, acCharge);

        LogUtils.infoMethodState(BatteryReceiver.class, 
    			"updateBatteryStateInfo", "BatteryStateInfo", 
    			BatteryStateInfo.get().toString());
            
        LogUtils.infoMethodOut(BatteryReceiver.class, 
			"updateBatteryStateInfo", intent.getAction());
	}
}
