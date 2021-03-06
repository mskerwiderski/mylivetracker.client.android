package de.msk.mylivetracker.client.android.upload.protocol;

import java.util.Date;

import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;

/**
 * classname: IProtocol
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public interface IProtocol {
	public String createDataStrForDataTransfer(
		Date lastInfoTimestamp, 
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo, 
		LocationInfo locationInfo, GpsStateInfo gpsStateInfo,  
		HeartrateInfo heartrateInfo, EmergencySignalInfo emergencySignalInfo, 
		MessageInfo messageInfo, String username, String password);
}
