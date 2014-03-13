package de.msk.mylivetracker.client.android.upload.protocol.gator.pt350;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

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
import de.msk.mylivetracker.client.android.util.FormatUtils;
import de.msk.mylivetracker.commons.util.gps.LatLonUtils;
import de.msk.mylivetracker.commons.util.gps.LatLonUtils.PosType;
import de.msk.mylivetracker.commons.util.gps.LatLonUtils.Wgs84Dsc;

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

	// #EMI15digits#username#statusdigit#password#datatype#datacapacity#base station information#longitude,E,latitude,N,speed,direction#date#time##
	//
	// e.g.:
	// #353818051525580#13610001016#1#0000#AUT#1#36012#00648.9669,W,5311.2871,N,055.00,000#070114#074248##
	//
	// o Imei Numebr 15 digits 
	// o Username is an 11 digit number assigned on the server side e.g. 13610001004 unique ID of the tracker
	// o Status Digit is always 1, it is 0 for when it cannot locate.
	// o Password is a 4 digit pin for the device, default is 0000 probably not needed in any way with this so may leave at 0000
	// o datatype, there are 3 possibilities, AUT proper correct location details, SOS sos alert, LPD Low power alert
	// o Data capacity is number of locations being transmitted. i'd imagine easiest would be to just do one location at a time, so this would be 1
	// o Base station is the cell tower identifier number
	// o latitude and longitude is a little complicated, but i think they are the same as the TK102 gps. 
	//   this lat/long 53.26932,-6.512361 would be converted like this. 
	//   0.26932*60.15920 so  53.26932  will be convert to be 5316.15920 
	//   0.512361*600.741660  -6.512361 will be -630.741660
	// o Speed in mph (nnn.nn)
	// o direction  in degrees (nnn)
	// o date in format DDMMYY
	// o Time in format HHMMSS
	
	private static final String DATA_DELIMITER = "#";
	private static final String DATA_ID_IMEI = "$imei";
	private static final String DATA_ID_USERNAME = "$username";
	private static final String DATA_ID_LOCATION_STATUS = "$locationStatus";
	private static final String DATA_ID_PASSWORD = "$password";
	private static final String DATA_ID_DEVICE_STATUS = "$deviceStatus";
	private static final String DATA_ID_CNT_LOCATION_DATA = "$cntLocationData";
	private static final String DATA_ID_CELL_ID = "$cellId";
	private static final String DATA_ID_LOCATION_DATA = "$locationData";
	private static final String DATA_ID_DATE = "$date";
	private static final String DATA_ID_TIME = "$time";

	private static final String DATA_DEVICE_STATUS_OK = "AUT";
	private static final String DATA_DEVICE_STATUS_SOS = "SOS";
	private static final String DATA_DEVICE_STATUS_BATTERY_LOW = "LPD";
	
	private static final String DATA_STR_TEMPLATE =
		DATA_DELIMITER + DATA_ID_IMEI + 
		DATA_DELIMITER + DATA_ID_USERNAME +
		DATA_DELIMITER + DATA_ID_LOCATION_STATUS +
		DATA_DELIMITER + DATA_ID_PASSWORD + 
		DATA_DELIMITER + DATA_ID_DEVICE_STATUS +
		DATA_DELIMITER + DATA_ID_CNT_LOCATION_DATA +
		DATA_DELIMITER + DATA_ID_CELL_ID + 
		DATA_DELIMITER + DATA_ID_LOCATION_DATA +
		DATA_DELIMITER + DATA_ID_DATE +
		DATA_DELIMITER + DATA_ID_TIME +
		DATA_DELIMITER + DATA_DELIMITER;
		
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, 
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		String dataStr = DATA_STR_TEMPLATE;
		dataStr = StringUtils.replace(dataStr, DATA_ID_IMEI, accountPrefs.getDeviceId());
		dataStr = StringUtils.replace(dataStr, DATA_ID_USERNAME, accountPrefs.getUsername());
		dataStr = StringUtils.replace(dataStr, DATA_ID_PASSWORD, accountPrefs.getPassword());
		String locationStatus = "0";
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			locationStatus = "1";
		}
		dataStr = StringUtils.replace(dataStr, DATA_ID_LOCATION_STATUS, locationStatus);
		if ((emergencySignalInfo != null) && emergencySignalInfo.isActivated()) {
			dataStr = StringUtils.replace(dataStr, DATA_ID_DEVICE_STATUS, DATA_DEVICE_STATUS_SOS);
		} else if ((batteryStateInfo != null) && batteryStateInfo.isBatteryLow()) {
			dataStr = StringUtils.replace(dataStr, DATA_ID_DEVICE_STATUS, DATA_DEVICE_STATUS_BATTERY_LOW);
		} else {
			dataStr = StringUtils.replace(dataStr, DATA_ID_DEVICE_STATUS, DATA_DEVICE_STATUS_OK);
		}
		dataStr = StringUtils.replace(dataStr, DATA_ID_CNT_LOCATION_DATA, "1");
		if ((phoneStateInfo != null) && phoneStateInfo.hasCellId()) {
			dataStr = StringUtils.replace(dataStr, DATA_ID_CELL_ID, phoneStateInfo.getCellId());
		} else {
			dataStr = StringUtils.replace(dataStr, DATA_ID_CELL_ID, "");
		}
		SimpleDateFormat sdfDate = new SimpleDateFormat("ddMMyy", Locale.ENGLISH);
		SimpleDateFormat sdfTime = new SimpleDateFormat("HHmmss", Locale.ENGLISH);
		dataStr = StringUtils.replace(dataStr, DATA_ID_DATE, sdfDate.format(lastInfoTimestamp));
		dataStr = StringUtils.replace(dataStr, DATA_ID_TIME, sdfTime.format(lastInfoTimestamp));
		String locationData = "";
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			Wgs84Dsc wgs84Lon = LatLonUtils.decToWgs84(locationInfo.getLongitude(), PosType.Longitude);
			locationData += wgs84Lon.toNmea0183String();
			Wgs84Dsc wgs84Lat = LatLonUtils.decToWgs84(locationInfo.getLatitude(), PosType.Latitude);
			locationData += "," + wgs84Lat.toNmea0183String();
			if (locationInfo.hasValidSpeed()) {
				String speed = FormatUtils.getDoubleAsSimpleStr(
					locationInfo.getSpeedInMtrPerSecs() * 3.6f / 1.852f, 2);
				if (speed.length() < 6) {
					speed = StringUtils.leftPad(speed, 6, '0');
				} else if (speed.length() > 6) {
					speed = StringUtils.right(speed, 6);
				}
				locationData += "," + speed;
				
			} else {
				locationData += ",000.00";
			}
			if (locationInfo.hasValidBearing()) {
				locationData += "," + FormatUtils.getDoubleAsSimpleStr(
					locationInfo.getBearingInDegree(), 2);
			} else {
				locationData += ",000";
			}
		}
		dataStr = StringUtils.replace(dataStr, DATA_ID_LOCATION_DATA, locationData);
		return dataStr;
	}

}
