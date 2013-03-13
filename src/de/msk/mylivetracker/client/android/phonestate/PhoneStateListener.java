package de.msk.mylivetracker.client.android.phonestate;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import de.msk.mylivetracker.client.android.App;
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
	
	private static TelephonyManager getTelephonyManager() {
		return (TelephonyManager)App.getCtx().
			getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public static boolean isPhoneTypeGsm() {
		int phoneType = getTelephonyManager().getPhoneType();
		return (phoneType == TelephonyManager.PHONE_TYPE_GSM);
	}
	
	public static void start() {
		if (phoneStateListener == null) {
			phoneStateListener = new PhoneStateListener();
			getTelephonyManager().listen(
				phoneStateListener, 
				PhoneStateListener.LISTEN_SERVICE_STATE |
				PhoneStateListener.LISTEN_CELL_LOCATION |
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
	}
	
	public static void stop() {
		if (phoneStateListener != null) {
			getTelephonyManager().listen(
				phoneStateListener, PhoneStateListener.LISTEN_NONE);
			phoneStateListener = null;
		}
	}
	
	@Override
	public void onCellLocationChanged(CellLocation location) {
		if (isPhoneTypeGsm()) {
			PhoneStateInfo.update(null, (GsmCellLocation)location, null, null);
		}
	}

	@Override
	public void onDataConnectionStateChanged(int state, int networkType) {
		PhoneStateInfo.update(networkType, null, null, null);
	}

	@Override
	public void onDataConnectionStateChanged(int state) {
	}
	
	@Override
	public void onServiceStateChanged(ServiceState serviceState) {
		PhoneStateInfo.update(null, null, serviceState, null);
	}

	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		PhoneStateInfo.update(null, null, null, signalStrength);
	}
}
