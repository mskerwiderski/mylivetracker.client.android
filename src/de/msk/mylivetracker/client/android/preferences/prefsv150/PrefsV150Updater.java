package de.msk.mylivetracker.client.android.preferences.prefsv150;

import de.msk.mylivetracker.client.android.auto.AutoPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;


/**
 * classname: PrefsV150Updater
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-01	origin.
 * 
 */
@SuppressWarnings("deprecation")
public class PrefsV150Updater {

	public static boolean run() {
		AutoPrefs autoPrefs = PrefsRegistry.get(AutoPrefs.class);
		TrackingModePrefs trackingModePrefs = PrefsRegistry.get(TrackingModePrefs.class);
		OtherPrefs otherPrefs = PrefsRegistry.get(OtherPrefs.class);
		if (autoPrefs.isAutoModeEnabled()) {
			trackingModePrefs.setTrackingMode(TrackingMode.Auto);
		} else {
			trackingModePrefs.setTrackingMode(TrackingMode.Standard);
		}
		trackingModePrefs.setAutoModeResetTrackMode(
			TrackingModePrefs.AutoModeResetTrackMode.values()[
                  autoPrefs.getAutoModeResetTrackMode().ordinal()]);
		otherPrefs.setAutoStartApp(autoPrefs.isAutoStartEnabled());
		return true;
	}
}
