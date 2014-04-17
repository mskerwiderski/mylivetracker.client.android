package de.msk.mylivetracker.client.android.util.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: AbstractService
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class AbstractService extends Service {

	private static final String RUN_SERVICE_ONLY_ONCE = "RUN_SERVICE_ONLY_ONCE";
	
	private AbstractServiceThread thread = null;
	
	private static void startServiceAux(Class<? extends AbstractService> serviceClass, 
		boolean runServiceOnlyOnce) {
		if (isServiceRunning(serviceClass)) {
			LogUtils.info(serviceClass, "service is already running.");
		} else {
			Intent serviceIntent = new Intent(App.getCtx(), serviceClass);
			serviceIntent.putExtra(RUN_SERVICE_ONLY_ONCE, runServiceOnlyOnce);
			App.getCtx().startService(serviceIntent);
			waitStartStopServiceDone(serviceClass, true);
			LogUtils.info(serviceClass, "service successfully started.");
		}
	}
	
	public static void runServiceOnlyOnce(Class<? extends AbstractService> serviceClass) {
		startServiceAux(serviceClass, true);
	}
	
	public static void startService(Class<? extends AbstractService> serviceClass) {
		startServiceAux(serviceClass, false);
	}
	
	public static void stopService(Class<? extends AbstractService> serviceClass) {
		if (!isServiceRunning(serviceClass)) {
			LogUtils.info(serviceClass, "service is not running.");
		} else {
			App.getCtx().stopService(
				new Intent(App.getCtx(), serviceClass));
			waitStartStopServiceDone(serviceClass, false);
			LogUtils.info(serviceClass, "service successfully stopped.");
		}
	}

	public static void startStopService(Class<? extends AbstractService> serviceClass, boolean start) {
		if (!start && AbstractService.isServiceRunning(LocalizationService.class)) {
			AbstractService.stopService(LocalizationService.class);						
		} else if (start && 
			!AbstractService.isServiceRunning(LocalizationService.class)){
			AbstractService.startService(LocalizationService.class);				
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
	
	protected abstract Class<? extends AbstractServiceThread> getServiceThreadClass();

	protected AbstractServiceThread createServiceThread(boolean runOnlyOnce) throws Exception {
		AbstractServiceThread serviceThread = this.getServiceThreadClass().newInstance();
		serviceThread.setRunOnlyOnce(runOnlyOnce);
		return serviceThread;
	}
	
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
	
	protected static class MessageFromServiceThreadHandler extends Handler {
	}

	protected static final int MSG_STOP_SERVICE = -1;
	private MessageFromServiceThreadHandler messageFromServiceThreadHandler = 
		new MessageFromServiceThreadHandler() {
			@Override
			public void handleMessage(Message msg) {
				onReceiveMessage(msg);
				if ((msg != null) && (msg.what == MSG_STOP_SERVICE)) {
					stopSelf();
				}
			}
	};
	
	public void onReceiveMessage(Message msg) {
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
		if (this.thread != null) {
			this.thread.stopAndWaitUntilTerminated();
			this.thread = null;
		}
		this.stopForeground(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		NotificationDsc notificationDsc = getNotificationDsc();
		MainActivity activity = MainActivity.get();
		if ((activity != null) && (notificationDsc != null)) {
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
				this.thread = this.createServiceThread(
					intent.getExtras().getBoolean(RUN_SERVICE_ONLY_ONCE, false));
				this.thread.initThreadObject(this.messageFromServiceThreadHandler);
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
