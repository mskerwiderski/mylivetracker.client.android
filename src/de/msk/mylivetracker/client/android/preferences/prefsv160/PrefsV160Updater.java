package de.msk.mylivetracker.client.android.preferences.prefsv160;

import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
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
		TrackingModePrefs trackingModePrefs = 
			PrefsRegistry.get(TrackingModePrefs.class);
		trackingModePrefs.setCountdownInSecs(CountdownInSecs.Off);
		trackingModePrefs.setStartAfterReboot(
			PrefsRegistry.get(OtherPrefs.class).isAutoStartApp());
		trackingModePrefs.setRunOnlyIfBattFullOrCharging(true);
		return true;
	}
}
