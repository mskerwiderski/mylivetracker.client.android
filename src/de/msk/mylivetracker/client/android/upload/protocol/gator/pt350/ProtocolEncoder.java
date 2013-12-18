package de.msk.mylivetracker.client.android.upload.protocol.gator.pt350;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;

/**
 * classname: ProtocolEncoder
 * 
 * @author michael skerwiderski, (c)2013
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2013-12-18	origin.
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
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);		
		String dataStr = 
			accountPrefs.getDeviceId() + "," +
			sdf.format(lastInfoTimestamp) + ",";
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			dataStr += 
				locationInfo.getLongitude() + "," +
				locationInfo.getLatitude() + "," +
				(locationInfo.hasValidSpeed() ? locationInfo.getSpeedInMtrPerSecs() : "0.0") + "," +
				(locationInfo.hasValidBearing() ? locationInfo.getBearingInDegree() : "0.0") + "," +
				(locationInfo.hasValidAltitude() ? locationInfo.getAltitudeInMtr() : "0.0") + ",";
			
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
