package de.msk.mylivetracker.client.android.preferences;

import android.content.Context;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences.BufferSize;
import de.msk.mylivetracker.client.android.preferences.Preferences.ConfirmLevel;
import de.msk.mylivetracker.client.android.preferences.Preferences.TransferProtocol;

/**
 * PreferencesCreator.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-11-08
 * 
 */
public class PreferencesCreator {

	public static Preferences create() {
		return createDefault();
	}
	
	private static Preferences createDefault() {
		Preferences prefs = new Preferences();
		prefs.transferProtocol = TransferProtocol.mltTcpEncrypted;
		prefs.server = MainActivity.get().getResources().getString(R.string.txPrefs_Def_MyLiveTracker_Server);
		prefs.port = Integer.valueOf(MainActivity.get().getResources().getString(R.string.txPrefs_Def_MyLiveTracker_PortTcp));
		prefs.path = MainActivity.get().getResources().getString(R.string.txPrefs_Def_MyLiveTracker_PathTcp);
		prefs.trackName = MainActivity.get().getResources().getString(R.string.txPrefs_Def_TrackName);		
		if (MainActivity.get().getLocationManager().getProvider(LocationManager.GPS_PROVIDER) == null) {
			prefs.locationProvider = LocationManager.NETWORK_PROVIDER;
		} else {
			prefs.locationProvider = LocationManager.GPS_PROVIDER;
		}
		prefs.locTimeTriggerInSeconds = 0;
		prefs.locDistanceTriggerInMeter = 0;	
		prefs.closeConnectionAfterEveryUpload = false;
		prefs.finishEveryUploadWithALinefeed = false;
		prefs.lineSeperator = "\r\n";
		prefs.locAccuracyRequiredInMeter = 100;
		prefs.locDistBtwTwoLocsForDistCalcRequiredInCMtr = 1650;
		prefs.uplTimeTriggerInSeconds = 10;
		prefs.uplDistanceTriggerInMeter = 0;
		prefs.uplPositionBufferSize = BufferSize.disabled;
		prefs.phoneNumber = "";
		prefs.deviceId = 
			((TelephonyManager)MainActivity.get().getSystemService(
			Context.TELEPHONY_SERVICE)).getDeviceId();
		prefs.username = "";
		prefs.password = "";	
		prefs.seed = null;
		prefs.confirmLevel = ConfirmLevel.medium;
		prefs.logging = false;
		return prefs;
	}
	
//	private static Preferences createSeltmann() {
//		Preferences prefs = new Preferences();
//		prefs.transferProtocol = TransferProtocol.tk102Emulator;
//		prefs.server = "87.139.224.116";
//		prefs.port = 31272;
//		prefs.path = "";
//		prefs.trackName = "not used";		
//		if (MainActivity.get().getLocationManager().getProvider(LocationManager.GPS_PROVIDER) == null) {
//			prefs.locationProvider = LocationManager.NETWORK_PROVIDER;
//		} else {
//			prefs.locationProvider = LocationManager.GPS_PROVIDER;
//		}
//		prefs.locTimeTriggerInSeconds = 0;
//		prefs.locDistanceTriggerInMeter = 0;	
//		prefs.closeConnectionAfterEveryUpload = true;
//		prefs.finishEveryUploadWithALinefeed = true;
//		prefs.lineSeperator = "\r\n";
//		prefs.locAccuracyRequiredInMeter = 0;
//		prefs.locDistBtwTwoLocsForDistCalcRequiredInCMtr = 0;
//		prefs.uplTimeTriggerInSeconds = 10;
//		prefs.uplDistanceTriggerInMeter = 0;
//		prefs.uplPositionBufferSize = BufferSize.disabled;
//		prefs.phoneNumber = "not used";
//		prefs.deviceId = 
//			((TelephonyManager)MainActivity.get().getSystemService(
//			Context.TELEPHONY_SERVICE)).getDeviceId();
//		prefs.username = "";
//		prefs.password = "";	
//		prefs.seed = null;
//		prefs.confirmLevel = ConfirmLevel.medium;
//		prefs.logging = false;
//		return prefs;
//	}
}
