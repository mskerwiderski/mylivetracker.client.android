package de.msk.mylivetracker.client.android.pincodequery;

import java.io.Serializable;

import org.apache.commons.lang.BooleanUtils;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: PinCodeQueryPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
	public String getShortName() {
		return "pincode";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("PinCodeQueryPrefs", 
			new ConfigPair[] {
				new ConfigPair("pinCodeQueryEnabled", 
					BooleanUtils.toStringTrueFalse(this.pinCodeQueryEnabled)),
				new ConfigPair("pinCode", this.pinCode),
		});
	}
	@Override
	public String toString() {
		return "PinCodeQueryPrefs [pinCodeQueryEnabled=" + pinCodeQueryEnabled
			+ ", pinCode=" + pinCode + "]";
	}
}
