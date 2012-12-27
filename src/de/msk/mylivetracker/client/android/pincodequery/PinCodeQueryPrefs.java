package de.msk.mylivetracker.client.android.pincodequery;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;

/**
 * PinCodeQueryPrefs.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-23 	revised for v1.5.x.
 * 000 	2012-12-23 	initial.
 * 
 */
public class PinCodeQueryPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = -1168163978533273694L;

	public static final int VERSION = 1;
	
	private boolean pinCodeQueryEnabled;
	private String pinCode;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.pinCodeQueryEnabled = false;
		this.pinCode = "";
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	public boolean isPinCodeQueryEnabled() {
		return pinCodeQueryEnabled;
	}
	public void setPinCodeQueryEnabled(boolean pinCodeQueryEnabled) {
		this.pinCodeQueryEnabled = pinCodeQueryEnabled;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	@Override
	public String toString() {
		return "PinCodeQueryPrefs [pinCodeQueryEnabled=" + pinCodeQueryEnabled
			+ ", pinCode=" + pinCode + "]";
	}
}
