package de.msk.mylivetracker.client.android.upload.protocol.xexun.tk102;

import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.emergency.EmergencyPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: ProtocolEncoder
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class ProtocolEncoder implements IProtocol {
	
	private static final String SEPERATOR = ",";	
		
	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, 
		GpsStateInfo gpsStateInfo, HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		String dataStr = "";
		DateTime dateTime = new DateTime(lastInfoTimestamp.getTime());
		String dateTimeStr = dateTime.getAsStr(TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), "yyMMddHHmmss");
		dataStr += dateTimeStr + SEPERATOR;
		dataStr += (StringUtils.isEmpty(accountPrefs.getPhoneNumber()) ? "" : accountPrefs.getPhoneNumber()) + SEPERATOR;
		dataStr += LocationInfo.getLocationAsGprmcRecord(locationInfo) + SEPERATOR;
		boolean locValid = false;
		String altitude = "";
		if ((locationInfo != null) && locationInfo.hasValidLatLon()) {
			locValid = locationInfo.isAccurate();
			if (locationInfo.hasValidAltitude()) {
				altitude = String.valueOf( 
					Math.round(locationInfo.getAltitudeInMtr() * 100d) / 100d);
			}
		}
		dataStr += (locValid ? "F" : "L") + SEPERATOR;
		if ((emergencySignalInfo != null) && emergencySignalInfo.isActivated()) {
			dataStr += EmergencyPrefs.getEmergencyMessageText() + SEPERATOR;
		} else if ((batteryStateInfo != null) && batteryStateInfo.isBatteryLow()) {
			dataStr += "low battery" + SEPERATOR;
		} else if (messageInfo != null) {
			dataStr += messageInfo.getMessage() + SEPERATOR;
		}
		dataStr += "imei:" + accountPrefs.getDeviceId() + SEPERATOR;
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
