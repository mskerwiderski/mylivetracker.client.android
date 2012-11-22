package de.msk.mylivetracker.client.android.upload.protocol.xexun.tk102;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.NmeaInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * ProtocolEncoder.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 001 2011-11-20 create own nmea sentence, if nmea listener does not provide it. 
 * 000 2011-08-11 initial.
 * 
 */
public class ProtocolEncoder implements IProtocol {
	
	private static final String SEPERATOR = ",";	
		
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
		Preferences prefs = Preferences.get();		
		String dataStr = "";
		DateTime dateTime = new DateTime(lastInfoTimestamp.getTime());
		String dateTimeStr = dateTime.getAsStr(TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), "yyMMddHHmmss");
		dataStr += dateTimeStr + SEPERATOR;
		dataStr += (StringUtils.isEmpty(prefs.getPhoneNumber()) ? "" : prefs.getPhoneNumber()) + SEPERATOR;
		if ((locationInfo != null) && (locationInfo.getLocation() != null)) {
			dataStr += LocationInfo.getLocationAsGprmcRecord(locationInfo) + SEPERATOR;
		} else {
			dataStr += StringUtils.substring(NmeaInfo.GPRMC_EMPTY_SENTENCE, 1) + SEPERATOR;
		}
		boolean locValid = false;
		String altitude = "";
		if ((locationInfo != null) && (locationInfo.getLocation() != null)) {
			locValid = locationInfo.isAccurate();
			altitude = String.valueOf( 
				Math.round(locationInfo.getLocation().getAltitude() * 100d) / 100d);
		}
		dataStr += (locValid ? "F" : "L") + SEPERATOR;
		if ((emergencySignalInfo != null) && emergencySignalInfo.isActivated()) {
			dataStr += "help me" + SEPERATOR;
		} else if ((batteryStateInfo != null) && batteryStateInfo.isBatteryLow()) {
			dataStr += "low battery" + SEPERATOR;
		}		
		dataStr += "imei:" + prefs.getDeviceId() + SEPERATOR;
		dataStr += ((gpsStateInfo != null) ? 
			StringUtils.leftPad(gpsStateInfo.getCountSatellites().toString(), 2, '0') : "0") + 
			SEPERATOR;
		dataStr += altitude + SEPERATOR;
		String batteryState = "";
		if (batteryStateInfo != null) {
			batteryState = (batteryStateInfo.isBatteryLow() ? "L" : "F");
			batteryState += ":" + batteryStateInfo.getVoltage() + "V";
		}
		dataStr += batteryState + SEPERATOR;
		dataStr += BooleanUtils.toString(
			batteryStateInfo.getState().equals(BatteryStateInfo.State.Charging), 
			"1", "0") + SEPERATOR;
		dataStr += StringUtils.leftPad(String.valueOf(dataStr.length()), 3, '0') + 
			SEPERATOR;
		if (phoneStateInfo != null) {
			dataStr += "" + SEPERATOR; // GSM ID
			String mcc = phoneStateInfo.getMobileCountryCode("");
			String mnc = phoneStateInfo.getMobileNetworkCode("");
			dataStr += mcc + SEPERATOR;
			dataStr += mnc + SEPERATOR;
			String lac = phoneStateInfo.getLocalAreaCodeAsHex("");
			String cid = phoneStateInfo.getCellIdAsHex("");
			dataStr += lac + SEPERATOR;
			dataStr += cid;
		}
		
		return dataStr;
	}

}
