package de.msk.mylivetracker.client.android.preferences.prefsv160;

import de.msk.mylivetracker.client.android.emergency.EmergencyPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.server.ServerPrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.CountdownInSecs;


/**
 * classname: PrefsV160Updater
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.7.0
 * 
 * history:
 * 000	2014-04-17	origin.
 * 
 */
public class PrefsV160Updater {

	@SuppressWarnings("deprecation")
	public static boolean run() {
		ServerPrefs serverPrefs = PrefsRegistry.get(ServerPrefs.class);
		serverPrefs.setSmsReceivers("");
		EmergencyPrefs emergencyPrefs = PrefsRegistry.get(EmergencyPrefs.class);
		emergencyPrefs.setSendAsSms(false);
		PinCodeQueryPrefs pinCodeQueryPrefs = PrefsRegistry.get(PinCodeQueryPrefs.class);
		pinCodeQueryPrefs.setProtectSettingsOnly(false);
		TrackingModePrefs trackingModePrefs = 
			PrefsRegistry.get(TrackingModePrefs.class);
		trackingModePrefs.setCountdownInSecs(CountdownInSecs.Off);
		trackingModePrefs.setSendAnyValidLocationBeforeTimeout(false);
		trackingModePrefs.setInterruptibleOnMainWindow(true);
		trackingModePrefs.setStartAfterReboot(
			PrefsRegistry.get(OtherPrefs.class).isAutoStartApp());
		trackingModePrefs.setRunOnlyIfBattFullOrCharging(true);
		return true;
	}
}
