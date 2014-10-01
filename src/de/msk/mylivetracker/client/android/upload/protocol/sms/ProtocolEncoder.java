package de.msk.mylivetracker.client.android.upload.protocol.sms;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.emergency.EmergencyPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: ProtocolEncoder
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.7.0
 * 
 * history:
 * 000	2014-09-21	origin.
 * 
 */
public class ProtocolEncoder implements IProtocol {

	@Override
	public String createDataStrForDataTransfer(Date lastInfoTimestamp,
		PhoneStateInfo phoneStateInfo, BatteryStateInfo batteryStateInfo,
		LocationInfo locationInfo, GpsStateInfo gpsStateInfo,
		HeartrateInfo heartrateInfo,
		EmergencySignalInfo emergencySignalInfo, MessageInfo messageInfo,
		String username, String password) {
		LogUtils.infoMethodIn(ProtocolEncoder.class,
			"createDataStrForDataTransfer", locationInfo);
		AccountPrefs accountPrefs = PrefsRegistry.get(AccountPrefs.class);
		String dataStr = App.getAppNameAbbr() + ":"; 
		dataStr += ResponseCreator.addParamValue(
			"", "imei", accountPrefs.getDeviceId());
		dataStr = ResponseCreator.addTimestampValue(dataStr, lastInfoTimestamp);
		dataStr = ResponseCreator.addLatLonValue(dataStr, locationInfo);
		dataStr = ResponseCreator.addParamValue(dataStr, "acc", 
			BooleanUtils.toStringYesNo(locationInfo.isAccurate()));
		if ((emergencySignalInfo != null) && !emergencySignalInfo.isActivated()) {
			dataStr = ResponseCreator.addParamValue(
				dataStr, "sos", PrefsRegistry.get(EmergencyPrefs.class).getMessageText());
		}
		if ((messageInfo != null) && !StringUtils.isEmpty(messageInfo.getMessage())) {
			dataStr = ResponseCreator.addParamValue(
				dataStr, "msg", messageInfo.getMessage());
		}
		dataStr = ResponseCreator.addGoogleLatLonUrl(dataStr, locationInfo);
		LogUtils.infoMethodOut(ProtocolEncoder.class,
			"createDataStrForDataTransfer", dataStr);
		return dataStr;
	}
}
