package de.msk.mylivetracker.client.android.phonestate;

import java.io.Serializable;

/**
 * classname: PhoneStateData
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.7.0
 * 
 * history:
 * 000	2014-04-17	origin.
 * 
 */
public class PhoneStateData implements Serializable {
	private static final long serialVersionUID = 3936196738261920143L;
	
	private String mobileCountryCode = null;
	private String mobileNetworkCode = null;
	private String mobileNetworkName = null;
	private String localAreaCode = null;
	private String cellId = null;
	private String networkType = null;
	private String phoneType = null;
	
	public PhoneStateData() {
	}
	
	public PhoneStateData(String mobileCountryCode, String mobileNetworkCode,
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


	public String getMobileCountryCode() {
		return mobileCountryCode;
	}

	public String getMobileNetworkCode() {
		return mobileNetworkCode;
	}

	public String getMobileNetworkName() {
		return mobileNetworkName;
	}

	public String getLocalAreaCode() {
		return localAreaCode;
	}

	public String getCellId() {
		return cellId;
	}

	public String getNetworkType() {
		return networkType;
	}

	public String getPhoneType() {
		return phoneType;
	}
}