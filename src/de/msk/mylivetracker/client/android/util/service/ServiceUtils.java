package de.msk.mylivetracker.client.android.util.service;

import java.util.HashMap;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.automode.AutoService;
import de.msk.mylivetracker.client.android.upload.UploadService;

/**
 * ServiceUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 initial 2012-11-11
 * 
 */
public class ServiceUtils {

	private static Map<ServiceName, Boolean> serviceStatus = 
		new HashMap<ServiceName, Boolean>();
			
	public enum ServiceName {
		AutoService(AutoService.class, 100), UploadService(UploadService.class, 110);
		private Class<? extends AbstractService> serviceClass;
		private int id;
		ServiceName(Class<? extends AbstractService> serviceClass, int id) {
			this.serviceClass = serviceClass;
			this.id = id;
			serviceStatus.put(this, Boolean.FALSE);
		}
		public Class<? extends AbstractService> getServiceClass() {
			return serviceClass;
		}
		public int getId() {
			return id;
		}
	}
	
	public static void startService(ServiceName serviceName) {
		if (serviceStatus.get(serviceName)) {
			// service already started.
		} else {
			App.getCtx().startService(
				new Intent(App.getCtx(), serviceName.getClass()));
			waitStartStopServiceDone(serviceName, true);
			serviceStatus.put(serviceName, Boolean.TRUE);
		}
	}
	
	public static void stopService(ServiceName serviceName) {
		if (!serviceStatus.get(serviceName)) {
			// service already stopped.
		} else {
			App.getCtx().stopService(
				new Intent(App.getCtx(), serviceName.getClass()));
			waitStartStopServiceDone(serviceName, false);
			serviceStatus.put(serviceName, Boolean.FALSE);
		}
	}
	
	private static boolean isServiceRunning(ServiceName serviceName) {
		boolean res = false;
	    ActivityManager manager = (ActivityManager) App.getCtx().getSystemService(Context.ACTIVITY_SERVICE);
	    Class<? extends AbstractService> serviceClass = serviceName.getServiceClass();
	    for (RunningServiceInfo runningService : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(runningService.service.getClassName())) {
	            res = true;
	        }
	    }
	    return res;
	}
	
	private static void waitStartStopServiceDone(
		ServiceName serviceName, boolean running) {
		boolean interrupted = false;
		while ((running ? 
			!isServiceRunning(serviceName) : 
			isServiceRunning(serviceName)) && 
			!interrupted) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}
	}
}
