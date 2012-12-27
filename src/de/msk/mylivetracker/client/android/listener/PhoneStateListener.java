package de.msk.mylivetracker.client.android.listener;

import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.gsm.GsmCellLocation;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;

/**
 * PhoneStateListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2011-08-11 	initial.
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

	/* (non-Javadoc)
	 * @see android.telephony.PhoneStateListener#onCellLocationChanged(android.telephony.CellLocation)
	 */
	@Override
	public void onCellLocationChanged(CellLocation location) {
		if (MainActivity.get().isPhoneTypeGsm()) {
			PhoneStateInfo.update(null, (GsmCellLocation)location, null, null);
			MainActivity.get().updateView();
		}
	}

	/* (non-Javadoc)
	 * @see android.telephony.PhoneStateListener#onDataConnectionStateChanged(int, int)
	 */
	@Override
	public void onDataConnectionStateChanged(int state, int networkType) {
		PhoneStateInfo.update(networkType, null, null, null);
		MainActivity.get().updateView();
	}

	/* (non-Javadoc)
	 * @see android.telephony.PhoneStateListener#onDataConnectionStateChanged(int)
	 */
	@Override
	public void onDataConnectionStateChanged(int state) {
		MainActivity.get().updateView();
	}
	
	/* (non-Javadoc)
	 * @see android.telephony.PhoneStateListener#onServiceStateChanged(android.telephony.ServiceState)
	 */
	@Override
	public void onServiceStateChanged(ServiceState serviceState) {
		PhoneStateInfo.update(null, null, serviceState, null);
		MainActivity.get().updateView();
	}

	/* (non-Javadoc)
	 * @see android.telephony.PhoneStateListener#onSignalStrengthsChanged(android.telephony.SignalStrength)
	 */
	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		PhoneStateInfo.update(null, null, null, signalStrength);
		MainActivity.get().updateView();
	}
}
