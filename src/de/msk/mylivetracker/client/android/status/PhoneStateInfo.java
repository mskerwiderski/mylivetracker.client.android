package de.msk.mylivetracker.client.android.status;

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
public class PhoneStateInfo extends AbstractInfo {
	public static String UNKNOWN = "unknown";
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
	
	private String mobileCountryCode = null;
	private String mobileNetworkCode = null;
	private String mobileNetworkName = null;
	private String localAreaCode = null;
	private String cellId = null;
	private String networkType = null;
	private String phoneType = null;
	
	public PhoneStateInfo(
		String mobileCountryCode, String mobileNetworkCode,
		String mobileNetworkName, String localAreaCode, String cellId,
		String networkType, String phoneType) {
		this.mobileCountryCode = mobileCountryCode;
		this.mobileNetworkCode = mobileNetworkCode;
		this.mobileNetworkName = mobileNetworkName;
		this.localAreaCode = localAreaCode;
		this.cellId = cellId;
		this.networkType = networkType;
		this.phoneType = phoneType;
	}
	
	public static PhoneStateInfo createNewPhoneStateInfo(
		PhoneStateInfo currPhoneStateInfo,
		Integer networkTypeValue,
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
		String networkType = getNetworkTypeAsStr(networkTypeValue);
		
		// phType.
		String phoneType = MainActivity.get().isPhoneTypeGsm() ? "GSM" : "CDMA";
		
		if (currPhoneStateInfo != null) {
			if (StringUtils.isEmpty(networkType)) {
				networkType = currPhoneStateInfo.networkType;
			}
			if (StringUtils.isEmpty(mobileNetworkName)) {
				mobileNetworkName = currPhoneStateInfo.mobileNetworkName;
			}
			if (StringUtils.isEmpty(mobileCountryCode)) {
				mobileCountryCode = currPhoneStateInfo.mobileCountryCode;
			}
			if (StringUtils.isEmpty(mobileNetworkCode)) {
				mobileNetworkCode = currPhoneStateInfo.mobileNetworkCode;
			}
			if (localAreaCode == null) {
				localAreaCode = currPhoneStateInfo.localAreaCode;
			}
			if (cellId == null) {
				cellId = currPhoneStateInfo.cellId;
			}
		}
		
		return new PhoneStateInfo(
			mobileCountryCode, mobileNetworkCode, mobileNetworkName, 
			localAreaCode, cellId, networkType, phoneType);
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhoneStateInfo [mobileCountryCode=")
			.append(mobileCountryCode).append(", mobileNetworkCode=")
			.append(mobileNetworkCode).append(", mobileNetworkName=")
			.append(mobileNetworkName).append(", localAreaCode=")
			.append(localAreaCode).append(", cellId=").append(cellId)
			.append(", networkType=").append(networkType)
			.append(", phoneType=").append(phoneType).append("]");
		return builder.toString();
	}
	public boolean hasMobileCountryCode() {
		return !StringUtils.isEmpty(mobileCountryCode);
	}
	public String getMobileCountryCode() {
		return mobileCountryCode;
	}
	public String getMobileCountryCode(String defValue) {
		return StringUtils.isEmpty(mobileCountryCode) ? 
			defValue : mobileCountryCode;
	}
	public boolean hasMobileNetworkCode() {
		return !StringUtils.isEmpty(mobileNetworkCode);
	}
	public String getMobileNetworkCode() {
		return mobileNetworkCode;
	}
	public String getMobileNetworkCode(String defValue) {
		return StringUtils.isEmpty(mobileNetworkCode) ? 
			defValue : mobileNetworkCode;
	}
	public boolean hasMobileNetworkName() {
		return !StringUtils.isEmpty(mobileNetworkName);
	}
	public String getMobileNetworkName() {
		return mobileNetworkName;
	}
	public String getMobileNetworkName(String defValue) {
		return StringUtils.isEmpty(mobileNetworkName) ? 
			defValue : mobileNetworkName;
	}
	public boolean hasLocalAreaCode() {
		return !StringUtils.isEmpty(localAreaCode);
	}
	public String getLocalAreaCode() {
		return localAreaCode;
	}
	public String getLocalAreaCode(String defValue) {
		return StringUtils.isEmpty(localAreaCode) ? 
			defValue : localAreaCode;
	}
	public String getLocalAreaCodeAsHex(String defValue) {
		if (StringUtils.isEmpty(localAreaCode)) return defValue; 
		String hex = Integer.toHexString(Integer.valueOf(localAreaCode));
		hex = StringUtils.upperCase(hex);
		hex = StringUtils.leftPad(hex, 4, '0');
		return hex;
	}
	public boolean hasCellId() {
		return !StringUtils.isEmpty(cellId);
	}
	public String getCellId() {
		return cellId;
	}
	public String getCellId(String defValue) {
		return StringUtils.isEmpty(cellId) ? 
			defValue : cellId;
	}
	public String getCellIdAsHex(String defValue) {
		if (StringUtils.isEmpty(cellId)) return defValue; 
		String hex = Integer.toHexString(Integer.valueOf(cellId));
		hex = StringUtils.upperCase(hex);
		hex = StringUtils.leftPad(hex, 4, '0');
		return hex;
	}
	public boolean hasNetworkType() {
		return !StringUtils.isEmpty(networkType);
	}
	public String getNetworkType() {
		return networkType;
	}
	public String getNetworkType(String defValue) {
		return StringUtils.isEmpty(networkType) ? 
			defValue : networkType;
	}
	public boolean hasPhoneType() {
		return !StringUtils.isEmpty(phoneType);
	}
	public String getPhoneType() {
		return phoneType;
	}
	public String getPhoneType(String defValue) {
		return StringUtils.isEmpty(phoneType) ? 
			defValue : phoneType;
	}
}
