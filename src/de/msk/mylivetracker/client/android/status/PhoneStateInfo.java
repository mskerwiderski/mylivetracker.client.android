package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.phonestate.PhoneStateData;

/**
 * classname: PhoneStateInfo
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class PhoneStateInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = 2253858092459738806L;
	
	private static PhoneStateInfo phoneStateInfo = null;
	
	public static void update(
		PhoneStateData phoneStateData) {
		if (TrackStatus.isInResettingState()) return;
		phoneStateInfo = 
			PhoneStateInfo.createNewPhoneStateInfo(
				phoneStateInfo, phoneStateData);
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
	
	private String mobileCountryCode = null;
	private String mobileNetworkCode = null;
	private String mobileNetworkName = null;
	private String localAreaCode = null;
	private String cellId = null;
	private String networkType = null;
	private String phoneType = null;
	
	private PhoneStateInfo() {
	}
	
	private PhoneStateInfo(
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
		PhoneStateData phoneStateData) {
		
		String mobileCountryCode = phoneStateData.getMobileCountryCode();
		String mobileNetworkCode = phoneStateData.getMobileNetworkCode();
		String mobileNetworkName = phoneStateData.getMobileNetworkName();
		String localAreaCode = phoneStateData.getLocalAreaCode();
		String cellId = phoneStateData.getCellId();
		String networkType = phoneStateData.getNetworkType();
		String phoneType = phoneStateData.getPhoneType();
				
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
