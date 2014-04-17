package de.msk.mylivetracker.client.android.phonestate;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import de.msk.mylivetracker.client.android.App;

/**
 * classname: PhoneStateListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2014-04-12	revised for v1.7.x.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class PhoneStateListener extends android.telephony.PhoneStateListener {

	private void sendBroadcast(Integer networkType, GsmCellLocation gsmCellLocation,
		ServiceState serviceState, SignalStrength signalStrength) {
		Intent intent = new Intent();
		intent.setAction(PhoneStateReceiver.ACTION_PHONE_STATE_CHANGED);
		intent.putExtra(PhoneStateReceiver.ACTION_PARAM_PHONE_STATE, 
			createPhoneStateData(
				networkType, gsmCellLocation, 
				serviceState, signalStrength));
		App.getCtx().sendBroadcast(intent);
	}
	
	public static PhoneStateData createPhoneStateData(
		Integer networkType,
		GsmCellLocation gsmCellLocation,
		ServiceState serviceState, 
		SignalStrength signalStrength) {
			
		// mnc.
		String nwOpCode = null;
		String mobileNetworkName = null;
		if (serviceState != null) {
			nwOpCode = serviceState.getOperatorNumeric();
			if (StringUtils.length(nwOpCode) < 5) {
				nwOpCode = null;
			}
			mobileNetworkName = serviceState.getOperatorAlphaLong();
		}
			
		// mcc, mnc.
		String mobileCountryCode = null;
		String mobileNetworkCode = null;
		if (nwOpCode != null) {
			mobileCountryCode = StringUtils.left(nwOpCode, 3);
			mobileNetworkCode = StringUtils.substring(nwOpCode, 3);
		}
		
		// lac, cid.
		String localAreaCode = null;
		String cellId = null;
		if (gsmCellLocation != null) {
			if (gsmCellLocation.getLac() != -1) {
				cellId = String.valueOf(gsmCellLocation.getLac());
			}
			if (gsmCellLocation.getCid() != -1) {
				localAreaCode = String.valueOf(gsmCellLocation.getCid());
			}
		}
			
		// nwType.
		String networkTypeStr = getNetworkTypeAsStr(networkType);
			
		// phType.
		String phoneType = PhoneStateReceiver.isPhoneTypeGsm() ? "GSM" : "CDMA";
		
		return new PhoneStateData(
			mobileCountryCode, mobileNetworkCode, mobileNetworkName, 
			localAreaCode, cellId, networkTypeStr, phoneType);
	}
	
	private static String getNetworkTypeAsStr(Integer networkTypeValue) {
		String networkType = null;
		if (networkTypeValue != null) {			
			switch (networkTypeValue) {
				case TelephonyManager.NETWORK_TYPE_EDGE:
					networkType = "EDGE";
					break;
				case TelephonyManager.NETWORK_TYPE_GPRS:
					networkType = "GPRS";
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
					networkType = "UMTS";
					break;
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					networkType = "HSDPA";
					break;	
				case TelephonyManager.NETWORK_TYPE_HSPA:
					networkType = "HSPA";
					break;	
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					networkType = "HSUPA";
					break;	
				default:
					networkType = null;
					break;
			}
		}
		return networkType;
	}
	
	@Override
	public void onCellLocationChanged(CellLocation location) {
		if (PhoneStateReceiver.isPhoneTypeGsm()) {
			this.sendBroadcast(null, (GsmCellLocation)location, null, null);
		}
	}

	@Override
	public void onDataConnectionStateChanged(int state, int networkType) {
		this.sendBroadcast(networkType, null, null, null);
	}

	@Override
	public void onServiceStateChanged(ServiceState serviceState) {
		this.sendBroadcast(null, null, serviceState, null);
	}

	@Override
	public void onSignalStrengthsChanged(SignalStrength signalStrength) {
		this.sendBroadcast(null, null, null, signalStrength);
	}
}
