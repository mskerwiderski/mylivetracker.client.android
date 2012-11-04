package de.msk.mylivetracker.client.android.upload.protocol.xexun.tk102;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import android.telephony.ServiceState;
import android.telephony.gsm.GsmCellLocation;
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

		SimpleDateFormat datetimef = new SimpleDateFormat("yyMMddHHmmss");
		dataStr += datetimef.format(lastInfoTimestamp) + SEPERATOR;		
		dataStr += (StringUtils.isEmpty(prefs.getPhoneNumber()) ? "" : prefs.getPhoneNumber()) + SEPERATOR;
		String nmeaGprmc = null;
		if (nmeaInfo != null) {
			// disable nmea sentences from os.
			//nmeaGprmc = nmeaInfo.getGprmc();
		}
		if (!StringUtils.isEmpty(nmeaGprmc)) {
			dataStr += StringUtils.substring(nmeaGprmc, 1) + SEPERATOR;
		} else if ((locationInfo != null) && (locationInfo.getLocation() != null)) {
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
			String mcc = "";
			String mnc = "";
			ServiceState serviceState = phoneStateInfo.getServiceState();
			if (serviceState != null) {
				String nwOpCode = serviceState.getOperatorNumeric();				
				if (!StringUtils.isEmpty(nwOpCode) && 
					((StringUtils.length(nwOpCode) == 5) || (StringUtils.length(nwOpCode) == 6))) {
					mcc = StringUtils.left(nwOpCode, 3);
					mnc = StringUtils.substring(nwOpCode, 3);
				}							
			}
			dataStr += mcc + SEPERATOR;
			dataStr += mnc + SEPERATOR;
			String lac = "";
			String cid = "";
			GsmCellLocation gsmCellLocation = phoneStateInfo.getGsmCellLocation();
			if (gsmCellLocation != null) {				
				if (gsmCellLocation.getCid() != -1) {
					cid = Integer.toHexString(gsmCellLocation.getCid());
					cid = StringUtils.upperCase(cid);
					cid = StringUtils.leftPad(cid, 4, '0');
				}
				if (gsmCellLocation.getLac() != -1) {
					lac = Integer.toHexString(gsmCellLocation.getLac());
					lac = StringUtils.upperCase(lac);
					lac = StringUtils.leftPad(lac, 4, '0');
				}
			}
			dataStr += lac + SEPERATOR;
			dataStr += cid;
		}
		
		return dataStr;
	}

}
