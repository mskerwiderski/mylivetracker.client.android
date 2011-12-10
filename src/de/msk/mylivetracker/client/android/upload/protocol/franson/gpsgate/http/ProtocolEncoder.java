package de.msk.mylivetracker.client.android.upload.protocol.franson.gpsgate.http;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import android.location.Location;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.NmeaInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.upload.protocol.HttpProtocolUtils;
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

	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.upload.protocol.IProtocol#createDataStrForDataTransfer(java.util.Date, de.msk.mylivetracker.client.android.status.PhoneStateInfo, de.msk.mylivetracker.client.android.status.BatteryStateInfo, de.msk.mylivetracker.client.android.status.LocationInfo, de.msk.mylivetracker.client.android.status.NmeaInfo, de.msk.mylivetracker.client.android.status.GpsStateInfo, de.msk.mylivetracker.client.android.status.HeartrateInfo, de.msk.mylivetracker.client.android.status.EmergencySignalInfo, de.msk.mylivetracker.client.android.status.MessageInfo, java.lang.String, java.lang.String)
	 */
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, NmeaInfo nmeaInfo,
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		Preferences preferences = Preferences.get();
				
		String paramsStr = null;
		if ((locationInfo != null) && (locationInfo.getLocation() != null)) {
			Location location = locationInfo.getLocation();
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "latitude", 
				String.valueOf(location.getLatitude()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "longitude",
				String.valueOf(location.getLongitude()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "altitude", 
				String.valueOf(location.getAltitude()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "speed",
				String.valueOf(location.getSpeed()));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "heading", 
				String.valueOf(location.getBearing()));
		}
		
		if (lastInfoTimestamp != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd':'HHmmss.SSS");
			String dateTimeStr = sdf.format(lastInfoTimestamp);
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "date", 
				StringUtils.substringBefore(dateTimeStr, ":"));
			paramsStr = HttpProtocolUtils.addParam(paramsStr, "time",
				StringUtils.substringAfter(dateTimeStr, ":"));
		}
		
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "username", 
			preferences.getUsername());
		paramsStr = HttpProtocolUtils.addParam(paramsStr, "pw", 
			encode(preferences.getPassword()));
		
		return paramsStr;
	}

	private static String encode(String plainPassword) {
		StringBuilder sb = null;
		if (plainPassword != null) {
			sb = new StringBuilder();
			int len = plainPassword.length();
			for (int idx=len-1; idx>=0; idx--) {
				char c = plainPassword.charAt(idx);
				if (c >= '0' && c <= '9') {
					sb.append((char)(9 - (c - '0') + '0'));
				} else if (c >= 'a' && c <= 'z') {
					sb.append((char)(('z' - 'a') - (c - 'a') + 'A'));
				} else if (c >= 'A' && c <= 'Z') {
					sb.append((char)(('Z' - 'A') - (c - 'A') + 'a'));
				}
			}
		}
		return (sb != null) ? sb.toString() : null;
	}
}
