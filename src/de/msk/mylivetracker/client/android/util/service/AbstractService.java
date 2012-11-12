package de.msk.mylivetracker.client.android.util.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.util.LogUtils;

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

	private AbstractServiceThread thread = null;
	
	private static Map<Class<? extends AbstractService>, Boolean> serviceStatus = 
		new HashMap<Class<? extends AbstractService>, Boolean>();
	
	public static void startService(Class<? extends AbstractService> serviceClass) {
		if (((serviceStatus.get(serviceClass) != null) && serviceStatus.get(serviceClass)) || 
			isServiceRunning(serviceClass)) {
			LogUtils.info(serviceClass, "service is already running.");
		} else {
			App.getCtx().startService(
				new Intent(App.getCtx(), serviceClass));
			waitStartStopServiceDone(serviceClass, true);
			serviceStatus.put(serviceClass, Boolean.TRUE);
			LogUtils.info(serviceClass, "service successfully started.");
		}
	}
	
	public static void stopService(Class<? extends AbstractService> serviceClass) {
		if ((serviceStatus.get(serviceClass) == null) || 
			!serviceStatus.get(serviceClass) || 
			!isServiceRunning(serviceClass)) {
			LogUtils.info(serviceClass, "service is not running.");
		} else {
			App.getCtx().stopService(
				new Intent(App.getCtx(), serviceClass));
			waitStartStopServiceDone(serviceClass, false);
			serviceStatus.put(serviceClass, Boolean.FALSE);
			LogUtils.info(serviceClass, "service successfully stopped.");
		}
	}

	public static boolean isServiceRunning(Class<? extends AbstractService> serviceClass) {
		boolean found = false;
	    ActivityManager manager = (ActivityManager) App.getCtx().getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningServiceInfo> runningServiceInfos = manager.getRunningServices(Integer.MAX_VALUE);
	    for (int i=0; !found && (i < runningServiceInfos.size()); i++) {
	        if (StringUtils.equals(serviceClass.getName(), 
	        	runningServiceInfos.get(i).service.getClassName())) {
	            found = true;
	        }
	    }
	    return found;
	}
	
	public abstract Class<? extends AbstractServiceThread> getServiceThreadClass();

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
		if (this.thread != null) {
			this.thread.stopAndWaitUntilTerminated();
			this.thread = null;
		}
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
		if (this.thread == null) {
			try {
				this.thread = this.getServiceThreadClass().newInstance();
				this.thread.start();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return START_NOT_STICKY;
	}
	
	private static void waitStartStopServiceDone(
		Class<? extends AbstractService> serviceClass, 
		boolean running) {
		boolean interrupted = false;
		while ((running ? 
			!isServiceRunning(serviceClass) : 
			isServiceRunning(serviceClass)) && 
			!interrupted) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}
	}
}