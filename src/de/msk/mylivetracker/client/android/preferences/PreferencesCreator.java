package de.msk.mylivetracker.client.android.preferences;

import android.content.Context;
import android.location.LocationManager;
import android.telephony.TelephonyManager;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences.AutoModeResetTrackMode;
import de.msk.mylivetracker.client.android.preferences.Preferences.BufferSize;
import de.msk.mylivetracker.client.android.preferences.Preferences.ConfirmLevel;
import de.msk.mylivetracker.client.android.preferences.Preferences.LocalizationMode;
import de.msk.mylivetracker.client.android.preferences.Preferences.TrackingOneTouchMode;
import de.msk.mylivetracker.client.android.preferences.Preferences.TransferProtocol;
import de.msk.mylivetracker.client.android.preferences.Preferences.UploadDistanceTrigger;
import de.msk.mylivetracker.client.android.preferences.Preferences.UploadTimeTrigger;
import de.msk.mylivetracker.client.android.preferences.Preferences.UploadTriggerLogic;
import de.msk.mylivetracker.client.android.pro.R;

/**
 * PreferencesCreator.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2012-02-04 preferences version 300 implemented.
 * 000 	2011-08-11 initial.
 */
public class PreferencesCreator {

	public static Preferences create(Context context) {
		return createDefault(context);
	}
	
	private static Preferences createDefault(Context context) {
		Preferences prefs = new Preferences();
		prefs.versionApp = App.getVersionDsc();
		prefs.firstStartOfApp = true;
		prefs.pinCode = null;
		prefs.pinCodeQueryEnabled = false;
		prefs.transferProtocol = TransferProtocol.uploadDisabled;
		prefs.server = "";
		prefs.port = 80;
		prefs.path = "";
		prefs.trackName = MainActivity.get().getResources().getString(R.string.txPrefs_Def_TrackName);		
		if (MainActivity.get().getLocationManager().getProvider(LocationManager.GPS_PROVIDER) == null) {
			prefs.localizationMode = LocalizationMode.network;
		} else {
			prefs.localizationMode = LocalizationMode.gpsAndNetwork;
		}
		prefs.locTimeTriggerInSeconds = 0;
		prefs.locDistanceTriggerInMeter = 0;	
		prefs.closeConnectionAfterEveryUpload = false;
		prefs.finishEveryUploadWithALinefeed = false;
		prefs.lineSeperator = "\r\n";
		prefs.locAccuracyRequiredInMeter = 150;
		prefs.locDistBtwTwoLocsForDistCalcRequiredInCMtr = 1650;
		prefs.uplTimeTrigger = UploadTimeTrigger.Secs10;
		prefs.uplTriggerLogic = UploadTriggerLogic.OR;
		prefs.uplDistanceTrigger = UploadDistanceTrigger.Off;
		prefs.uplPositionBufferSize = BufferSize.disabled;
		prefs.phoneNumber = "";
		prefs.deviceId = 
			((TelephonyManager)MainActivity.get().getSystemService(
			Context.TELEPHONY_SERVICE)).getDeviceId();
		prefs.username = "";
		prefs.password = "";	
		prefs.seed = null;
		prefs.confirmLevel = ConfirmLevel.medium;
		prefs.autoModeEnabled = false;
		prefs.autoModeResetTrackMode = AutoModeResetTrackMode.NextDay;
		prefs.autoStartEnabled = false;
		prefs.trackingOneTouchMode = TrackingOneTouchMode.TrackingOnly;
		prefs.logging = false;
		prefs.remoteAccessEnabled = false;
		prefs.remoteAccessPassword = "";
		prefs.remoteAccessUseReceiver = false;
		prefs.remoteAccessReceiver = "";
		prefs.httpProtocolParams = HttpProtocolParams.create();
		return prefs;
	}
}
