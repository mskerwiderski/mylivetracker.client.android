package de.msk.mylivetracker.client.android.listener;

import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.gsm.GsmCellLocation;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;

/**
 * classname: PhoneStateListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class PhoneStateListener extends android.telephony.PhoneStateListener {
	private static PhoneStateListener phoneStateListener = null;
	
	public static PhoneStateListener get() {
		if (phoneStateListener == null) {
			phoneStateListener = new PhoneStateListener();			
		} 
		return phoneStateListener;
	}

	@Override
	public void onCellLocationChanged(CellLocation location) {
		if (MainActivity.get().isPhoneTypeGsm()) {
			PhoneStateInfo.update(null, (GsmCellLocation)location, null, null);
			MainActivity.get().updateView();
		}
	}

	@Override
	public void onDataConnectionStateChanged(int state, int networkType) {
		PhoneStateInfo.update(networkType, null, null, null);
		MainActivity.get().updateView();
	}

	@Override
	public void onDataConnectionStateChanged(int state) {
		MainActivity.get().updateView();
	}
	
	@Override
	public void onServiceStateChanged(ServiceState serviceState) {
		PhoneStateInfo.update(null, null, serviceState, null);
		MainActivity.get().updateView();
	}

	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		PhoneStateInfo.update(null, null, null, signalStrength);
		MainActivity.get().updateView();
	}
}
