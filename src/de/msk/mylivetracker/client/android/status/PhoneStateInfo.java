package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import de.msk.mylivetracker.client.android.mainview.MainActivity;

/**
 * PhoneStateInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2012-02-20 default value for getNetworkTypeAsStr added.
 * 000	2011-08-11 initial.
 * 
 */
public class PhoneStateInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = 2253858092459738806L;
	
	private static PhoneStateInfo phoneStateInfo = null;
	
	public static void update(
		Integer networkType, GsmCellLocation gsmCellLocation,
		ServiceState serviceState, SignalStrength signalStrength) {
		phoneStateInfo = 
			PhoneStateInfo.createNewPhoneStateInfo(
				phoneStateInfo, 
				networkType, gsmCellLocation,
				serviceState, signalStrength);
	}
	public static PhoneStateInfo get() {
		return phoneStateInfo;
	}
	public static void reset() {
		phoneStateInfo = null;
	}
	public static void set(PhoneStateInfo phoneStateInfo) {
		PhoneStateInfo.phoneStateInfo = phoneStateInfo;
	}
	
	private Integer networkType;
	private GsmCellLocation gsmCellLocation;
	private ServiceState serviceState;
	private SignalStrength signalStrength;
	
	private PhoneStateInfo() {
	}
	
	private PhoneStateInfo(Integer networkType, GsmCellLocation gsmCellLocation,
		ServiceState serviceState, SignalStrength signalStrength) {
		this.networkType = networkType;
		this.gsmCellLocation = gsmCellLocation;
		this.serviceState = serviceState;
		this.signalStrength = signalStrength;
	}	

	public static PhoneStateInfo createNewPhoneStateInfo(
		PhoneStateInfo currPhoneStateInfo,
		Integer networkType,
		GsmCellLocation gsmCellLocation,
		ServiceState serviceState, 
		SignalStrength signalStrength) {
		Integer newNetworkType = networkType;
		GsmCellLocation newGsmCellLocation = gsmCellLocation;
		ServiceState newServiceState = serviceState;
		SignalStrength newSignalStrength = signalStrength;
		
		if (currPhoneStateInfo != null) {
			if (newNetworkType == null) {
				newNetworkType = currPhoneStateInfo.networkType;
			}
			if (newGsmCellLocation == null) {
				newGsmCellLocation = currPhoneStateInfo.gsmCellLocation;
			}
			if (newServiceState == null) {
				newServiceState = currPhoneStateInfo.serviceState;
			}
			if (newSignalStrength == null) {
				newSignalStrength = currPhoneStateInfo.signalStrength;
			}
		}
		
		return new PhoneStateInfo(
			newNetworkType, 
			newGsmCellLocation,
			newServiceState,
			newSignalStrength); 
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhoneStateInfo [networkType=").append(networkType)
			.append(", gsmCellLocation=").append(gsmCellLocation)
			.append(", serviceState=").append(serviceState)
			.append(", signalStrength=").append(signalStrength).append("]");
		return builder.toString();
	}
	
	public static String getPhoneTypeAsStr() {
		return MainActivity.get().isPhoneTypeGsm() ? "GSM" : "CDMA";
	}
	
	public String getNetworkTypeAsStr(String defValue) {
		String networkTypeAsStr = null;
		if (this.networkType != null) {			
			switch (networkType) {
			case TelephonyManager.NETWORK_TYPE_EDGE:
				networkTypeAsStr = "EDGE";
				break;
			case TelephonyManager.NETWORK_TYPE_GPRS:
				networkTypeAsStr = "GPRS";
				break;
			case TelephonyManager.NETWORK_TYPE_UMTS:
				networkTypeAsStr = "UMTS";
				break;
			case TelephonyManager.NETWORK_TYPE_HSDPA:
				networkTypeAsStr = "HSDPA";
				break;	
			case TelephonyManager.NETWORK_TYPE_HSPA:
				networkTypeAsStr = "HSPA";
				break;	
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				networkTypeAsStr = "HSUPA";
				break;	
			default:
				networkTypeAsStr = defValue;
				break;
			}
		}
		return networkTypeAsStr;
	}
	
	private String getNwOpCodeOrNull() {
		ServiceState serviceState = this.getServiceState();
		if (serviceState == null) return null;
		String nwOpCode = serviceState.getOperatorNumeric();
		if (StringUtils.length(nwOpCode) < 5) return null;
		return nwOpCode;
	}
	
	public String getMCC() {
		String nwOpCode = getNwOpCodeOrNull();
		if (nwOpCode == null) return null;
		return StringUtils.left(nwOpCode, 3);		
	}
	
	public String getMNC() {
		String nwOpCode = getNwOpCodeOrNull();
		if (nwOpCode == null) return null;
		return StringUtils.substring(nwOpCode, 3);
	}
	
	public Integer getLAC() {
		if (this.gsmCellLocation == null) return null;
		return ((this.gsmCellLocation.getLac() == -1) ? null : this.gsmCellLocation.getLac());
	}
	
	public Integer getCellId() {
		if (this.gsmCellLocation == null) return null;
		return ((this.gsmCellLocation.getCid() == -1) ? null : this.gsmCellLocation.getCid());
	}
	
	/**
	 * @return the networkType
	 */
	public Integer getNetworkType() {
		return networkType;
	}

	/**
	 * @return the gsmCellLocation
	 */
	public GsmCellLocation getGsmCellLocation() {
		return gsmCellLocation;
	}

	/**
	 * @return the serviceState
	 */
	public ServiceState getServiceState() {
		return serviceState;
	}

	/**
	 * @return the signalStrength
	 */
	public SignalStrength getSignalStrength() {
		return signalStrength;
	}
}
