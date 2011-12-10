package de.msk.mylivetracker.client.android.mainview.updater;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.listener.LocationListener;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.TrackStatus;

/**
 * StatusBarUpdater.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-09-05
 * 
 */
public class StatusBarUpdater {
	private static final int NOTIFIER_ID_APP_STATUS = 1;
	
	private static NotificationManager getNotifMgr() {
		return 
			(NotificationManager)MainActivity.get().
			getSystemService(Context.NOTIFICATION_SERVICE);
	}
	
	public static void cancelAppStatus() {
		getNotifMgr().cancel(NOTIFIER_ID_APP_STATUS);
	}
	
	public static void updateAppStatus() {
		MainActivity act = MainActivity.get();
				
		int icon = R.drawable.icon;
		int strId = R.string.nfAppWaiting;
		
		if (TrackStatus.get().trackIsRunning()) {
			icon = R.drawable.icon_notification_red;
			strId = R.string.nfTrackRunning;
		} else if (LocationListener.get().isActive() || 
			act.getAntPlusManager().hasSensorListeners()) {
			icon = R.drawable.icon_notification_yellow;
			strId = R.string.nfListenerRunning;
		}

		Notification notification = new Notification(
			icon, act.getText(R.string.app_name), System.currentTimeMillis());
		
		Context context = act.getApplicationContext();
		CharSequence contentTitle = act.getText(R.string.app_name);
		CharSequence contentText = act.getText(strId);
		Intent notificationIntent = act.getIntent();
		PendingIntent contentIntent = PendingIntent.getActivity(act, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
				
		getNotifMgr().notify(NOTIFIER_ID_APP_STATUS, notification);
	}
}
