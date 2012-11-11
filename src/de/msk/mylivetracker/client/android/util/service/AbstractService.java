package de.msk.mylivetracker.client.android.util.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.mainview.MainActivity;

/**
 * AbstractService.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 initial 2012-11-11
 * 
 */
public abstract class AbstractService extends Service {

	public abstract void startServiceThread();
	public abstract void stopServiceThread();

	public static class NotificationDsc {
		public int notificationId;
		public int iconId;
		public int titleId;
		public int messageId;
		public NotificationDsc(
			int notificationId, int iconId, 
			int titleId, int messageId) {
			this.notificationId = notificationId;
			this.iconId = iconId;
			this.titleId = titleId;
			this.messageId = messageId;
		}
	}
	
	public abstract NotificationDsc getNotificationDsc();
	
	@Override
	public IBinder onBind(Intent intent) {
		// Do not provide binding, so return null.
		return null;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDestroy() {
		stopServiceThread();
		this.stopForeground(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		NotificationDsc notificationDsc = getNotificationDsc();
		if (notificationDsc != null) {
			Context ctx = App.getCtx();
			CharSequence contentTitle = ctx.getText(notificationDsc.titleId);
			CharSequence contentText = ctx.getText(notificationDsc.messageId);
			Notification notification = new Notification(
				notificationDsc.iconId, 
				contentTitle, System.currentTimeMillis());
			Intent notificationIntent = MainActivity.get().getIntent();
			PendingIntent contentIntent = PendingIntent.getActivity(
				MainActivity.get(), 0, notificationIntent, 0);
			notification.setLatestEventInfo(ctx, contentTitle, contentText, contentIntent);
			this.startForeground(notificationDsc.notificationId, notification);
		}
		startServiceThread();
		return START_NOT_STICKY;
	}
}
