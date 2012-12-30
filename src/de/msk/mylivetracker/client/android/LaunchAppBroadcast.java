package de.msk.mylivetracker.client.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.msk.mylivetracker.client.android.auto.AutoPrefs;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: LaunchAppBroadcast
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class LaunchAppBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent unused) {
		LogUtils.info(LaunchAppBroadcast.class, "onReceive called.");
		AutoPrefs prefs = PrefsRegistry.get(AutoPrefs.class);
		if (prefs.isAutoStartEnabled()) {
			// start MainActivity.
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			LogUtils.info(LaunchAppBroadcast.class, "MainActivity started.");
		}
	}
}
