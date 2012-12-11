package de.msk.mylivetracker.client.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * LaunchAppBroadcast.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 2012-02-22 initial.
 * 
 */
public class LaunchAppBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent unused) {
		LogUtils.info(LaunchAppBroadcast.class, "onReceive called.");
		Preferences prefs = Preferences.get();
		if (prefs.isAutoStartEnabled()) {
			// start MainActivity.
			Intent intent = new Intent(context, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			LogUtils.info(LaunchAppBroadcast.class, "MainActivity started.");
		}
	}
}
