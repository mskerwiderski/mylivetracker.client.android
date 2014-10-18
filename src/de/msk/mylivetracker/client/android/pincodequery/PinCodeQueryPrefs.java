package de.msk.mylivetracker.client.android.pincodequery;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;

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
	
	public enum PinCodeQueryMode {
		Disabled("disabled"),
		ProtectWholeApp("protect whole app"),
		ProtectPrefsOnly("protect preferences only"),
		ProtectPrefsViaAdminMode("protect preferences via admin mode");
		
		private String dsc;
		
		private PinCodeQueryMode(String dsc) {
			this.dsc = dsc;
		}
		public String getDsc() {
			return dsc;
		}
	};
	
	private PinCodeQueryMode pinCodeQueryMode;
	
	public static boolean pinCodeQueryEnabled() {
		return !PrefsRegistry.get(PinCodeQueryPrefs.class).
			pinCodeQueryMode.equals(PinCodeQueryMode.Disabled);
	}
	
	public static boolean pinCodeQueryEnabledForWholeApp() {
		return PrefsRegistry.get(PinCodeQueryPrefs.class).
			pinCodeQueryMode.equals(PinCodeQueryMode.ProtectWholeApp);
	}
	
	public static boolean pinCodeQueryEnabledForPrefsOnly() {
		return PrefsRegistry.get(PinCodeQueryPrefs.class).
			pinCodeQueryMode.equals(PinCodeQueryMode.ProtectPrefsOnly);
	}
	
	public static boolean pinCodeQueryEnabledForPrefsViaAdminMode() {
		return PrefsRegistry.get(PinCodeQueryPrefs.class).
			pinCodeQueryMode.equals(PinCodeQueryMode.ProtectPrefsViaAdminMode);
	}
	
	private String pinCode;
	
	@Deprecated
	private boolean pinCodeQueryEnabled;
	@Deprecated
	private boolean protectSettingsOnly;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.pinCodeQueryEnabled = false;
		this.protectSettingsOnly = false;
		this.pinCodeQueryMode = PinCodeQueryMode.Disabled;
		this.pinCode = "";
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	@Deprecated
	public static boolean protectEntireAppConfigured() {
		return 
			PrefsRegistry.get(PinCodeQueryPrefs.class).isPinCodeQueryEnabled() && 
			!PrefsRegistry.get(PinCodeQueryPrefs.class).isProtectSettingsOnly();
	}
	@Deprecated
	public static boolean protectSettingsOnlyConfigured() {
		return 
			PrefsRegistry.get(PinCodeQueryPrefs.class).isPinCodeQueryEnabled() && 
			PrefsRegistry.get(PinCodeQueryPrefs.class).isProtectSettingsOnly();
	}
	@Deprecated
	public boolean isPinCodeQueryEnabled() {
		return pinCodeQueryEnabled;
	}
	@Deprecated
	public boolean isProtectSettingsOnly() {
		return protectSettingsOnly;
	}
	@Deprecated
	public void setPinCodeQueryEnabled(boolean pinCodeQueryEnabled) {
		this.pinCodeQueryEnabled = pinCodeQueryEnabled;
	}
	@Deprecated
	public void setProtectSettingsOnly(boolean protectSettingsOnly) {
		this.protectSettingsOnly = protectSettingsOnly;
	}
	public PinCodeQueryMode getPinCodeQueryMode() {
		return pinCodeQueryMode;
	}
	public void setPinCodeQueryMode(PinCodeQueryMode pinCodeQueryMode) {
		this.pinCodeQueryMode = pinCodeQueryMode;
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
				new ConfigPair("pinCodeQueryMode", 
					this.pinCodeQueryMode.getDsc()),
				new ConfigPair("pinCode", this.pinCode),
		});
	}
	@Override
	public String toString() {
		return "PinCodeQueryPrefs [pinCodeQueryMode=" + pinCodeQueryMode
			+ ", pinCode=" + pinCode + "]";
	}
}
