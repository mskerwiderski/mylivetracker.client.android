package de.msk.mylivetracker.client.android.mainview.updater;

import org.apache.commons.lang.StringUtils;

import android.content.res.Resources;
import android.location.Location;
import android.widget.TextView;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.listener.LocationListener;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils.Unit;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.status.UploadInfo;

/**
 * MainViewUpdater.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2012-02-04 
 *     	o additional localizationMode implemented (gpsAndNetwork).
 *     	o lastUsedLocationProvider added.
 * 000 	2011-08-11 initial.
 * 
 */
public class MainViewUpdater implements Runnable {
		
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
		return UpdaterUtils.getIntStr(uploadInfo.getCountUploaded()) + 
			" " + uploadInfo.getLastUsedLocationProvider();
	}
	
	public static String getLocationAccuracyStr(LocationInfo locationInfo) {
		if (locationInfo == null) return UpdaterUtils.getNoValue();
		Location location = locationInfo.getLocation();
		if ((location == null) || (!location.hasAccuracy())) return UpdaterUtils.getNoValue(); 
		return UpdaterUtils.getFltStr(location.getAccuracy(), 0, Unit.Meter);
	}
	
	private enum IndicatorState {
		Ok(R.color.colorIndicatorOk), 
		Off(R.color.colorIndicatorOff), 
		NotOk(R.color.colorIndicatorNotOk);
		private int color;
		private IndicatorState(int color) {
			this.color = color;
		}
		public int getColor() {
			return color;
		}
	}
	
	public static void setIndicatorTextAndColors(TextView tv, String text, IndicatorState state) {
		MainActivity mainActivity = MainActivity.get();					
		Resources res = mainActivity.getResources();
		tv.setText(text);
		tv.setBackgroundColor(
			res.getColor(state.getColor()));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Preferences preferences = Preferences.get();
		TrackStatus status = TrackStatus.get();
		
		MainActivity mainActivity = MainActivity.get();					
		Resources res = mainActivity.getResources();
		
		LocationInfo locationInfo = LocationInfo.get();
		HeartrateInfo heartrateInfo = HeartrateInfo.get();
		UploadInfo uploadInfo = UploadInfo.get();
		
		// auto mode indicator
		TextView tvAutoModeIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_AutoModeIndicator);
		setIndicatorTextAndColors(tvAutoModeIndicator, 
			(preferences.isAutoModeEnabled() ? 
				res.getText(R.string.tvOn).toString() : 
				res.getText(R.string.tvOff).toString()), 
			preferences.isAutoModeEnabled() ? IndicatorState.Ok : IndicatorState.Off);
		
		// gps indicator			
		TextView tvLocalizationIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_LocalizationIndicator);
		boolean gpsOn = MainActivity.isLocalizationByGpsEnabled();
		boolean nwOn = MainActivity.isLocalizationByNetworkEnabled();
		String locIndStr = UpdaterUtils.getNoValue();
		if (gpsOn && !nwOn) {
			locIndStr = "GPS";
		} else if (!gpsOn && nwOn) {
			locIndStr = "Nw";
		} else if (gpsOn && nwOn) {
			locIndStr = "GPS/Nw";
		}
		setIndicatorTextAndColors(tvLocalizationIndicator, locIndStr, 
			!(!gpsOn && !nwOn) ? IndicatorState.Ok : IndicatorState.NotOk);
		
		// wireless lan indicator			
		TextView tvWirelessLanIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_WirelessLanIndicator);
		setIndicatorTextAndColors(tvWirelessLanIndicator,
			(MainActivity.isWifiEnabled() ? 
				res.getText(R.string.tvOn).toString() : 
				res.getText(R.string.tvOff).toString()),
				MainActivity.isWifiEnabled() ? IndicatorState.Ok : IndicatorState.Off);
		
		// mobile network indicator			
		TextView tvMobileNetworkIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_MobileNetworkIndicator);
		PhoneStateInfo phoneStateInfo = PhoneStateInfo.get();
		String mobNwStr = (phoneStateInfo != null ? 
			phoneStateInfo.getNetworkTypeAsStr(UpdaterUtils.getNoValue()) : UpdaterUtils.getNoValue());
		setIndicatorTextAndColors(tvMobileNetworkIndicator, mobNwStr, 
			MainActivity.get().isDataConnectionAvailable() ? IndicatorState.Ok : IndicatorState.NotOk);
		
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
		if (mainActivity.getAntPlusManager().hasSensorListeners() &&
			(heartrateInfo != null) && heartrateInfo.isUpToDate()) {
			tvHeartrate.setBackgroundColor(res.getColor(R.color.colorAntPlListValueValid));
			tvHeartrate.setText(getHeartrateCurrentStr(heartrateInfo));
		} else if (mainActivity.getAntPlusManager().hasSensorListeners() &&
			StringUtils.equals(status.getAntPlusHeartrateStatus(), 
				mainActivity.getText(R.string.antPlus_ConnStateConnected).toString())) {
			tvHeartrate.setBackgroundColor(res.getColor(R.color.colorAntPlListConnected));
			tvHeartrate.setText(status.getAntPlusHeartrateStatus());
		} else if (mainActivity.getAntPlusManager().hasSensorListeners() &&
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
		if (!mainActivity.localizationEnabled()) {
			tvLocation.setBackgroundColor(res.getColor(R.color.colorLocListNoPosition));
			tvLocation.setText(R.string.locListener_ProviderNotAvailable);
		} else {
			if (LocationListener.get().isActive() && 
				(locationInfo != null) &&  locationInfo.isUpToDate() && locationInfo.isAccurate()) {
				tvLocation.setBackgroundColor(res.getColor(R.color.colorLocListPosFoundAcc));
				tvLocation.setText(getLocationAccuracyStr(locationInfo));
			} else if (LocationListener.get().isActive() && 
				(locationInfo != null) && locationInfo.isUpToDate()) {
				tvLocation.setBackgroundColor(res.getColor(R.color.colorLocListPosFoundNotAcc));
				tvLocation.setText(getLocationAccuracyStr(locationInfo));
			} else if (LocationListener.get().isActive()) {
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
		
		MainActivity.logInfo("main view updated.");
	}	
}
