package de.msk.mylivetracker.client.android.upload.protocol.incutex.tk5000;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * ProtocolEncoder.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class ProtocolEncoder implements IProtocol {

	// http://www.auto-wacht.de/fileadmin/user_upload/Downloadliste/Bedienungsanleitung_TK5000_SMS_PC-Tool.pdf
	private static final String EVENT_ID_TRACKING_POSITIONS = "2";
	private static final String EVENT_ID_BATTERY_LOW = "40";
	private static final String EVENT_ID_EMERGENCY = "4";
	
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, 
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		Preferences prefs = Preferences.get();		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");		
		String dataStr = 
			prefs.getDeviceId() + "," +
			sdf.format(lastInfoTimestamp) + ",";
		if ((locationInfo != null) && locationInfo.latLonValid()) {
			dataStr += 
				locationInfo.getLongitude() + "," +
				locationInfo.getLatitude() + "," +
				((locationInfo.getSpeed() != null) ? locationInfo.getSpeed() : "0.0") + "," +
				((locationInfo.getBearing() != null) ? locationInfo.getBearing() : "0.0") + "," +
				((locationInfo.getAltitude() != null) ? locationInfo.getAltitude() : "0.0") + ",";
			
			if (gpsStateInfo != null) {
				dataStr += gpsStateInfo.getCountSatellites();
			} else {
				dataStr += "0";
			}
			
			dataStr += ",";
		} else {
			dataStr += "0,0,0,0,0,0,";
		}
		
		if (emergencySignalInfo != null) {
			dataStr += EVENT_ID_EMERGENCY;
		} else {
			if ((batteryStateInfo != null) && (batteryStateInfo.isBatteryLow())) {
				dataStr += EVENT_ID_BATTERY_LOW;
			} else {
				dataStr += EVENT_ID_TRACKING_POSITIONS;
			}
		}
		return dataStr;
	}

}
