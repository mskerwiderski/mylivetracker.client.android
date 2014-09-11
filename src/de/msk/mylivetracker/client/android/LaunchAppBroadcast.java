package de.msk.mylivetracker.client.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: LaunchAppBroadcast
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 001	2014-03-12	start app if autoStartApp is true (whetcher trackingMode is set to auto or not).
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class LaunchAppBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent unused) {
		LogUtils.info(LaunchAppBroadcast.class, "onReceive called.");
		TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);
		LogUtils.info(LaunchAppBroadcast.class, "isAuto=" + TrackingModePrefs.isAuto());
		LogUtils.info(LaunchAppBroadcast.class, "isStartAfterReboot=" + prefs.isStartAfterReboot());
		if (TrackingModePrefs.isAuto() && prefs.isStartAfterReboot()) {
			LogUtils.info(LaunchAppBroadcast.class, "starting tracking mode 'auto' ...");
			AppControl.startAppBase();
			LogUtils.info(LaunchAppBroadcast.class, "tracking mode 'auto' started.");
		}
	}
}
