package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.auto.AutoService;
import de.msk.mylivetracker.client.android.battery.BatteryReceiver;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.mainview.updater.ViewUpdateService;
import de.msk.mylivetracker.client.android.phonestate.PhoneStateReceiver;
import de.msk.mylivetracker.client.android.upload.UploadService;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: SystemInfo
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.7.0
 * 
 * history:
 * 000	2014-09-21	origin.
 * 
 */
public class SystemInfo extends AbstractInfo implements Serializable {

	private static final long serialVersionUID = -2795275278561077175L;

	private static SystemInfo systemInfo = null;
	
	public static void update() {
		if (TrackStatus.isInResettingState()) return;
		systemInfo = SystemInfo.createNewSystemInfo();
	}
	public static SystemInfo get() {
		return systemInfo;
	}
	public static void reset() {
		systemInfo = null;
	}
	public static void set(SystemInfo systemInfo) {
		SystemInfo.systemInfo = systemInfo;
	}
	
	private boolean viewUpdateServiceRunning;
	private boolean uploadServiceRunning;
	private boolean autoServiceRunning;
	private boolean localizationServiceRunning;
	private boolean batteryReceiverRegistered;
	private boolean phoneStateReceiverRegistered;
	
	private SystemInfo() {
	}
	
	public static SystemInfo createNewSystemInfo() {
		SystemInfo systemInfo = new SystemInfo();
		systemInfo.viewUpdateServiceRunning =
			AbstractService.isServiceRunning(ViewUpdateService.class);
		systemInfo.uploadServiceRunning =
			AbstractService.isServiceRunning(UploadService.class);
		systemInfo.autoServiceRunning =
			AbstractService.isServiceRunning(AutoService.class);
		systemInfo.localizationServiceRunning =
			AbstractService.isServiceRunning(LocalizationService.class);
		systemInfo.batteryReceiverRegistered =
			BatteryReceiver.isRegistered();
		systemInfo.phoneStateReceiverRegistered =
			PhoneStateReceiver.isRegistered();
		return systemInfo; 
	}
	
	@Override
	public String toString() {
		return "SystemInfo [viewUpdateServiceRunning="
			+ viewUpdateServiceRunning + ", uploadServiceRunning="
			+ uploadServiceRunning + ", autoServiceRunning="
			+ autoServiceRunning + ", localizationServiceRunning="
			+ localizationServiceRunning + ", batteryReceiverRegistered="
			+ batteryReceiverRegistered + ", phoneStateReceiverRegistered="
			+ phoneStateReceiverRegistered + "]";
	}

	public boolean isViewUpdateServiceRunning() {
		return viewUpdateServiceRunning;
	}
	public boolean isUploadServiceRunning() {
		return uploadServiceRunning;
	}
	public boolean isAutoServiceRunning() {
		return autoServiceRunning;
	}
	public boolean isLocalizationServiceRunning() {
		return localizationServiceRunning;
	}
	public boolean isBatteryReceiverRegistered() {
		return batteryReceiverRegistered;
	}
	public boolean isPhoneStateReceiverRegistered() {
		return phoneStateReceiverRegistered;
	}
}
