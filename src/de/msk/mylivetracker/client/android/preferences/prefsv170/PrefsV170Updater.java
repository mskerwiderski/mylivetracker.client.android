package de.msk.mylivetracker.client.android.preferences.prefsv170;

import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs.PinCodeQueryMode;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;


/**
 * classname: PrefsV170Updater
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.8.0
 * 
 * history:
 * 000	2014-10-17	origin.
 * 
 */
public class PrefsV170Updater {
	@SuppressWarnings("deprecation")
	public static boolean run() {
		PinCodeQueryPrefs pinCodeQueryPrefs = PrefsRegistry.get(PinCodeQueryPrefs.class);
		pinCodeQueryPrefs.setPinCodeQueryMode(PinCodeQueryMode.Disabled);
		if (pinCodeQueryPrefs.isPinCodeQueryEnabled()) {
			if (!pinCodeQueryPrefs.isProtectSettingsOnly()) {
				pinCodeQueryPrefs.setPinCodeQueryMode(
					PinCodeQueryMode.ProtectWholeApp);
			} else {
				pinCodeQueryPrefs.setPinCodeQueryMode(
					PinCodeQueryMode.ProtectPrefsOnly);
			}
		}
		OtherPrefs otherPrefs = PrefsRegistry.get(OtherPrefs.class);
		otherPrefs.setUseGoogleUrlShortener(true);
		return true;
	}
}
