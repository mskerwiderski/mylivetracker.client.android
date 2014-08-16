package de.msk.mylivetracker.client.android.mainview.updater;

import org.apache.commons.lang3.StringUtils;

import android.content.res.Resources;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefs;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.status.UploadInfo;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;
import de.msk.mylivetracker.client.android.util.ConnectivityUtils;
import de.msk.mylivetracker.client.android.util.FormatUtils.Unit;
import de.msk.mylivetracker.client.android.util.LocationManagerUtils;
import de.msk.mylivetracker.client.android.util.TimeUtils;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: MainViewUpdater
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class MainViewUpdater extends AViewUpdater {
		
	private static String getHeartrateCurrentStr(HeartrateInfo heartrateInfo) {
		if (heartrateInfo == null) return UpdaterUtils.getNoValue();
		return UpdaterUtils.getLongStr(heartrateInfo.getHrInBpm(), Unit.BeatsPerMinute);
	}		
	
	private static String getUploaderResultCode(UploadInfo uploadInfo) {
		if (uploadInfo == null) return UpdaterUtils.getNoValue();
		return UpdaterUtils.getStr(uploadInfo.getResultCode());
	}
	
	private static String getUploaderCountUploaded(UploadInfo uploadInfo) {
		if (uploadInfo == null) return UpdaterUtils.getNoValue();
		return 
			UpdaterUtils.getIntStr(uploadInfo.getCountUploaded()) + 
			"p (" + uploadInfo.getLastUsedLocationProvider() + ")";
	}
	
	public static String getLocationAccuracyStr(LocationInfo locationInfo) {
		if (locationInfo == null) return UpdaterUtils.getNoValue();
		if (!locationInfo.hasValidAccuracy()) return UpdaterUtils.getNoValue(); 
		return 
			UpdaterUtils.getFltStr(locationInfo.getAccuracyInMtr(), 0, Unit.Meter) + " (" +
			LocationInfo.getProviderAbbr(locationInfo) + ")";
	}
	
	private enum ColorState {
		IndicatorOk(R.color.colorIndicatorOk), 
		IndicatorOff(R.color.colorIndicatorOff), 
		IndicatorNotOk(R.color.colorIndicatorNotOk),
		ChronometerOff(R.color.colorChronometerOff),
		ChronometerCountdown(R.color.colorChronometerCountdown),
		ChronometerRunning(R.color.colorChronometerRunning);
		
		private int color;
		private ColorState(int color) {
			this.color = color;
		}
		public int getColor() {
			return color;
		}
	}
	
	public static void setTextAndBackgroundColor(View tv, String text, ColorState state) {
		MainActivity mainActivity = MainActivity.get();					
		Resources res = mainActivity.getResources();
		if (text != null) {
			((TextView)tv).setText(text);
		}
		if (state != null) {
			tv.setBackgroundColor(res.getColor(state.getColor()));
		}
	}
	
	@Override
	public void updateView() {
		TrackingModePrefs trackingModePrefs = PrefsRegistry.get(TrackingModePrefs.class);
		OtherPrefs otherPrefs = PrefsRegistry.get(OtherPrefs.class);
		TrackStatus status = TrackStatus.get();
		
		MainActivity mainActivity = MainActivity.get();					
		Resources res = mainActivity.getResources();
		
		LocationInfo locationInfo = LocationInfo.get();
		HeartrateInfo heartrateInfo = HeartrateInfo.get();
		UploadInfo uploadInfo = UploadInfo.get();

		// auto start indicator
		TextView tvAutoStartIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_AutoStartIndicator);
		setTextAndBackgroundColor(tvAutoStartIndicator, 
			(otherPrefs.isAutoStartApp() ? 
				res.getText(R.string.tvOn).toString() : 
				res.getText(R.string.tvOff).toString()), 
			otherPrefs.isAutoStartApp() ? 
				ColorState.IndicatorOk : ColorState.IndicatorOff);
				
		// tracking mode indicator
		TextView tvAutoModeIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_TrackingModeIndicator);
		setTextAndBackgroundColor(tvAutoModeIndicator,
			App.getCtx().getResources().getStringArray(R.array.trackingModeAbbr)
				[trackingModePrefs.getTrackingMode().ordinal()],	
			trackingModePrefs.getTrackingMode().equals(TrackingMode.Auto) ? 
				ColorState.IndicatorOk : ColorState.IndicatorOff);
		
		// gps indicator			
		TextView tvLocalizationIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_LocalizationIndicator);
		boolean gpsOn = LocationManagerUtils.gpsProviderEnabled();
		boolean nwOn = LocationManagerUtils.networkProviderEnabled();
		String locIndStr = UpdaterUtils.getNoValue();
		if (gpsOn && !nwOn) {
			locIndStr = "GPS";
		} else if (!gpsOn && nwOn) {
			locIndStr = "Nw";
		} else if (gpsOn && nwOn) {
			locIndStr = "GPS/Nw";
		}
		setTextAndBackgroundColor(tvLocalizationIndicator, locIndStr, 
			!(!gpsOn && !nwOn) ? 
				ColorState.IndicatorOk : 
				ColorState.IndicatorNotOk);
		
		// wireless lan indicator			
		TextView tvWirelessLanIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_WirelessLanIndicator);
		boolean wifiEnabled = ConnectivityUtils.isWifiEnabled();
		setTextAndBackgroundColor(tvWirelessLanIndicator,
			(wifiEnabled ? 
				res.getText(R.string.tvOn).toString() : 
				res.getText(R.string.tvOff).toString()),
				wifiEnabled ? 
					ColorState.IndicatorOk : 
					ColorState.IndicatorOff);
		
		// mobile network indicator			
		TextView tvMobileNetworkIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_MobileNetworkIndicator);
		PhoneStateInfo phoneStateInfo = PhoneStateInfo.get();
		String mobNwStr = (phoneStateInfo != null ? 
			phoneStateInfo.getNetworkType(UpdaterUtils.getNoValue()) : UpdaterUtils.getNoValue());
		setTextAndBackgroundColor(tvMobileNetworkIndicator, mobNwStr, 
			ConnectivityUtils.isDataConnectionAvailable() ? 
				ColorState.IndicatorOk : 
				ColorState.IndicatorNotOk);
		
		// toggle buttons
		((ToggleButton)mainActivity.findViewById(R.id.btMain_StartStopTrack)).
			setChecked(AppControl.trackIsRunning());
		((ToggleButton)mainActivity.findViewById(R.id.btMain_LocationListenerOnOff)).
			setChecked(AppControl.localizationIsRunning());
		if (AppControl.antPlusDetectionIsAvailable()) {
			((ToggleButton)mainActivity.findViewById(R.id.btMain_ConnectDisconnectAnt)).
				setChecked(AppControl.antPlusDetectionIsRunning());
		}
		
		// chronometer
		View tvRuntime = mainActivity.findViewById(R.id.tvMain_Runtime);
		if (!status.trackIsRunning()) {
			setTextAndBackgroundColor(tvRuntime, 
				TimeUtils.getTrackTimeFormatted(status.getRuntimeInMSecs(false)),
				ColorState.ChronometerOff);
		} else {
			if (!status.countdownIsActive()) {
				setTextAndBackgroundColor(tvRuntime, 
					TimeUtils.getTrackTimeFormatted(status.getRuntimeInMSecs(false)),
					ColorState.ChronometerRunning);
			} else {
				setTextAndBackgroundColor(tvRuntime, 
					String.valueOf(status.getCountdownLeftInSecs()), 
					ColorState.ChronometerCountdown);
			}
		}
		
		// track distance
		TextView tvDistance = UpdaterUtils.tv(mainActivity, R.id.tvMain_Distance);
		Float trackDistanceInMtr = status.getTrackDistanceInMtr();
		if (trackDistanceInMtr == null) {
			tvDistance.setText(UpdaterUtils.getNoValue());
		} else {									
			tvDistance.setText(UpdaterUtils.getFltStr(
				trackDistanceInMtr / 1000f, 2, Unit.Kilometer));		
		}
		
		// heartrate
		TextView tvHeartrate = UpdaterUtils.tv(mainActivity, R.id.tvMain_Heartrate);								
		if (AntPlusManager.get().hasSensorListeners() &&
			(heartrateInfo != null) && heartrateInfo.isUpToDate()) {
			tvHeartrate.setBackgroundColor(res.getColor(R.color.colorAntPlListValueValid));
			tvHeartrate.setText(getHeartrateCurrentStr(heartrateInfo));
		} else if (AntPlusManager.get().hasSensorListeners() &&
			StringUtils.equals(status.getAntPlusHeartrateStatus(), 
				mainActivity.getText(R.string.antPlus_ConnStateConnected).toString())) {
			tvHeartrate.setBackgroundColor(res.getColor(R.color.colorAntPlListConnected));
			tvHeartrate.setText(status.getAntPlusHeartrateStatus());
		} else if (AntPlusManager.get().hasSensorListeners() &&
			StringUtils.equals(status.getAntPlusHeartrateStatus(), 
				mainActivity.getText(R.string.antPlus_ConnStateConnecting).toString())) {
			tvHeartrate.setBackgroundColor(res.getColor(R.color.colorAntPlListConnecting));
			tvHeartrate.setText(status.getAntPlusHeartrateStatus());
		} else {
			tvHeartrate.setBackgroundColor(res.getColor(R.color.colorAntPlListOff));
			tvHeartrate.setText(getHeartrateCurrentStr(heartrateInfo));
		}
		
		// location
		TextView tvLocation = UpdaterUtils.tv(mainActivity, R.id.tvMain_Location);
		if (!PrefsRegistry.get(LocalizationPrefs.class).
			getLocalizationMode().neededProvidersEnabled()) {
			tvLocation.setBackgroundColor(res.getColor(R.color.colorLocListNoPosition));
			tvLocation.setText(R.string.locListener_ProviderNotAvailable);
		} else {
			boolean localizationActive = 
				AbstractService.isServiceRunning(LocalizationService.class);
			if (localizationActive && 
				(locationInfo != null) &&  locationInfo.isUpToDate() && locationInfo.isAccurate()) {
				tvLocation.setBackgroundColor(res.getColor(R.color.colorLocListPosFoundAcc));
				tvLocation.setText(getLocationAccuracyStr(locationInfo));
			} else if (localizationActive && 
				(locationInfo != null) && locationInfo.isUpToDate()) {
				tvLocation.setBackgroundColor(res.getColor(R.color.colorLocListPosFoundNotAcc));
				tvLocation.setText(getLocationAccuracyStr(locationInfo));
			} else if (localizationActive) {
				tvLocation.setBackgroundColor(res.getColor(R.color.colorLocListNoPosition));
				tvLocation.setText(R.string.locListener_Listening);
			} else {
				tvLocation.setBackgroundColor(res.getColor(R.color.colorLocListOff));
				tvLocation.setText(getLocationAccuracyStr(locationInfo));				
			}
		}
		
		// uploader
		TextView tvUploader = UpdaterUtils.tv(mainActivity, R.id.tvMain_Uploader);
		if (TrackStatus.get().trackIsRunning() &&
			(uploadInfo != null) && uploadInfo.isSuccess()) {
			tvUploader.setBackgroundColor(res.getColor(R.color.colorUploaderLastUplSuccessful));
			tvUploader.setText(getUploaderCountUploaded(uploadInfo));
		} else if (TrackStatus.get().trackIsRunning() &&
			(uploadInfo != null) && !uploadInfo.isSuccess()) {
			tvUploader.setBackgroundColor(res.getColor(R.color.colorUploaderLastUplFailed));
			tvUploader.setText(getUploaderResultCode(uploadInfo));
		} else if (TrackStatus.get().trackIsRunning() && (uploadInfo == null)) {
			tvUploader.setBackgroundColor(res.getColor(R.color.colorUploaderNoUploadDone));
			tvUploader.setText(getUploaderCountUploaded(uploadInfo));
		} else  {
			tvUploader.setBackgroundColor(res.getColor(R.color.colorUploaderOff));
			tvUploader.setText(getUploaderCountUploaded(uploadInfo));
		}
	}	
}
