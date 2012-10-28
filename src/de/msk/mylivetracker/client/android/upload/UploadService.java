package de.msk.mylivetracker.client.android.upload;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;

/**
 * UploadService.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 initial 2012-10-27
 * 
 */
public class UploadService extends Service {

	public static void start() {
		if (!isRunning()) {
			MainActivity.get().startService(
				new Intent(MainActivity.get(), UploadService.class));
			waitRunning(true);
		}
	}
	
	public static void stop() {
		if (isRunning()) {
			MainActivity.get().stopService(
				new Intent(MainActivity.get(), UploadService.class));
			waitRunning(false);
		}
	}

	public static void waitRunning(boolean waitUntilServiceIsRunning) {
		boolean interrupted = false;
		while ((waitUntilServiceIsRunning ? !isRunning() : isRunning()) && !interrupted) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}
	}
	
	public static boolean isRunning() {
		return UploadManager.isUploadManagerRunning();
	}
	
	private static final int NOTIFIER_UPLOAD_SERVICE_IS_RUNNING = 1;
	private void setToForeground() {
		MainActivity act = MainActivity.get();
		Notification notification = new Notification(
			R.drawable.icon_notification_red, 
			act.getText(R.string.app_name), System.currentTimeMillis());
		Context context = act.getApplicationContext();
		CharSequence contentTitle = act.getText(R.string.app_name);
		CharSequence contentText = act.getText(R.string.nfTrackRunning);
		Intent notificationIntent = act.getIntent();
		PendingIntent contentIntent = PendingIntent.getActivity(act, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		this.startForeground(NOTIFIER_UPLOAD_SERVICE_IS_RUNNING, notification);
	}
	
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
		UploadManager.stopUploadManager();
		this.stopForeground(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.setToForeground();
		UploadManager.startUploadManager();
		return START_NOT_STICKY;
	}
}
