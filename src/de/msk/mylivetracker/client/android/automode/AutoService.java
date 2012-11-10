package de.msk.mylivetracker.client.android.automode;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;

/**
 * AutoService.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 initial 2012-11-10
 * 
 */
public class AutoService extends Service {

	private static class StartProgressDialog extends AbstractProgressDialog<Activity> {
		@Override
		public void doTask() {
			LogUtils.info("startAutoService...");
			App.get().startService(
				new Intent(App.get(), AutoService.class));
			waitRunning(true);
			LogUtils.info("startAutoService...started.");
		}
	}
	
	private static class StopProgressDialog extends AbstractProgressDialog<Activity> {
		@Override
		public void doTask() {
			LogUtils.info("stopAutoService...");
			App.get().stopService(
				new Intent(App.get(), AutoService.class));
			waitRunning(false);
			LogUtils.info("stopAutoService...stopped.");
		}
	}
	
	public static void start() {
		StartProgressDialog dlg = new StartProgressDialog();
		dlg.run();
	}
	
	public static void stop() {
		StopProgressDialog dlg = new StopProgressDialog();
		dlg.run();
	}
	
	public static void waitRunning(boolean waitUntilServiceIsRunning) {
		boolean interrupted = false;
		while ((waitUntilServiceIsRunning ? 
			!isRunning() : isRunning()) && !interrupted) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				interrupted = true;
				LogUtils.info("waitRunning=interrupted");
			}
			LogUtils.info("waitRunning=" + isRunning());
		}
	}
	
	public static boolean isRunning() {
		return AutoManager.isAutoManagerRunning();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// Do not provide binding, so return null.
		return null;
	}

	@Override
	public void onDestroy() {
		AutoManager.stopAutoManager();
		this.stopForeground(true);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//this.setToForeground();
		AutoManager.startAutoManager();
		return START_NOT_STICKY;
	}
}
