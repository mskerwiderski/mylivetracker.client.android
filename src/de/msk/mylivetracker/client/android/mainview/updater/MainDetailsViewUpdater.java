package de.msk.mylivetracker.client.android.mainview.updater;

import android.app.Activity;
import android.widget.TextView;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainDetailsActivity;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils.Unit;
import de.msk.mylivetracker.client.android.status.BatteryStateInfo;
import de.msk.mylivetracker.client.android.status.EmergencySignalInfo;
import de.msk.mylivetracker.client.android.status.GpsStateInfo;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.LocationInfo;
import de.msk.mylivetracker.client.android.status.MessageInfo;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.status.PositionBuffer;
import de.msk.mylivetracker.client.android.status.UploadInfo;

/**
 * MainDetailsViewUpdater.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-28 initial.
 * 
 */
public class MainDetailsViewUpdater implements Runnable {

	private static void updateTvUploader(Activity act, UploadInfo uploadInfo) {
		TextView tvLastResultCode = UpdaterUtils.tv(act, R.id.tvMainDetails_UploaderLastResultCode);
		TextView tvCountUploaded =  UpdaterUtils.tv(act, R.id.tvMainDetails_UploaderCountUploaded);
		TextView tvAverageUploadTime =  UpdaterUtils.tv(act, R.id.tvMainDetails_UploaderAverageUploadTime);
		TextView tvPositionsInBuffer =  UpdaterUtils.tv(act, R.id.tvMainDetails_UploaderPositionsInBuffer);
		if (uploadInfo == null) {
			tvLastResultCode.setText(UpdaterUtils.getNoValue());
			tvCountUploaded.setText(UpdaterUtils.getNoValue());
			tvAverageUploadTime.setText(UpdaterUtils.getNoValue());
			tvPositionsInBuffer.setText(UpdaterUtils.getNoValue());
		} else {
			tvLastResultCode.setText(UpdaterUtils.getStr(uploadInfo.getResultCode()));
			tvCountUploaded.setText(UpdaterUtils.getIntStr(uploadInfo.getCountUploaded()));
			if (uploadInfo.getAvgUploadTimeInMSecs() == null) {
				tvAverageUploadTime.setText(UpdaterUtils.getNoValue());
			} else {
				tvAverageUploadTime.setText(UpdaterUtils.getDblStr(
					uploadInfo.getAvgUploadTimeInMSecs() / 1000d, 2, Unit.Seconds));
			}
			if (!PositionBuffer.isEnabled()) {
				tvPositionsInBuffer.setText(UpdaterUtils.getNoValue());
			} else {
				tvPositionsInBuffer.setText(UpdaterUtils.getIntStr(
					PositionBuffer.get().size()));
			}
		}		
	}
	
	private static void updateTvBattery(Activity act, BatteryStateInfo batteryStateInfo) {
		TextView tvStatus = UpdaterUtils.tv(act, R.id.tvMainDetails_BatteryStatus);
		TextView tvCharged = UpdaterUtils.tv(act, R.id.tvMainDetails_BatteryCharged);
		TextView tvPower = UpdaterUtils.tv(act, R.id.tvMainDetails_BatteryPower);
		TextView tvTemperature = UpdaterUtils.tv(act, R.id.tvMainDetails_BatteryTemperature);
		if (batteryStateInfo == null) {
			tvStatus.setText(UpdaterUtils.getNoValue());
			tvCharged.setText(UpdaterUtils.getNoValue());
			tvPower.setText(UpdaterUtils.getNoValue());
			tvTemperature.setText(UpdaterUtils.getNoValue());
		} else {
			if (batteryStateInfo.getState() == null) {
				tvStatus.setText(UpdaterUtils.getNoValue());
			} else {
				tvStatus.setText(UpdaterUtils.getStr(batteryStateInfo.getState().getAbbr()));
			}
			tvCharged.setText(UpdaterUtils.getIntStr(
				batteryStateInfo.getPercent(), Unit.Percent));
			tvPower.setText(UpdaterUtils.getDblStr(
				batteryStateInfo.getVoltage(), 2, Unit.Volt));
			tvTemperature.setText(UpdaterUtils.getIntStr(
				batteryStateInfo.getDegrees(), Unit.DegreeCelsius));
		}		
	}
	
	private static void updateTvMobileNetwork(Activity act, PhoneStateInfo phoneStateInfo) {
		TextView tvMCC = UpdaterUtils.tv(act, R.id.tvMainDetails_MobileNetworkMCC);
		TextView tvMNC = UpdaterUtils.tv(act, R.id.tvMainDetails_MobileNetworkMNC);
		TextView tvLAC = UpdaterUtils.tv(act, R.id.tvMainDetails_MobileNetworkLAC);
		TextView tvCellId = UpdaterUtils.tv(act, R.id.tvMainDetails_MobileNetworkCellId);
		if (phoneStateInfo == null) {
			tvMCC.setText(UpdaterUtils.getNoValue());
			tvMNC.setText(UpdaterUtils.getNoValue());
			tvLAC.setText(UpdaterUtils.getNoValue());
			tvCellId.setText(UpdaterUtils.getNoValue());
		} else {
			tvMCC.setText(UpdaterUtils.getStr(phoneStateInfo.getMCC()));
			tvMNC.setText(UpdaterUtils.getStr(phoneStateInfo.getMNC()));
			tvLAC.setText(UpdaterUtils.getIntStr(phoneStateInfo.getLAC()));
			tvCellId.setText(UpdaterUtils.getIntStr(phoneStateInfo.getCellId()));
		}	    
	}
	
	private static void updateTvLocation(Activity act, GpsStateInfo gpsStateInfo, LocationInfo locationInfo) {
		TextView tvSatCount = UpdaterUtils.tv(act, R.id.tvMainDetails_LocationSatCount);
		TextView tvAccuracy = UpdaterUtils.tv(act, R.id.tvMainDetails_LocationAccuracy);
		TextView tvLatitude = UpdaterUtils.tv(act, R.id.tvMainDetails_LocationLatitude);
		TextView tvLongitude = UpdaterUtils.tv(act, R.id.tvMainDetails_LocationLongitude);
		if (locationInfo == null) {
			tvSatCount.setText(UpdaterUtils.getNoValue());
			tvAccuracy.setText(UpdaterUtils.getNoValue());
			tvLatitude.setText(UpdaterUtils.getNoValue());
			tvLongitude.setText(UpdaterUtils.getNoValue());
		} else {
			if (gpsStateInfo == null) {
				tvSatCount.setText(UpdaterUtils.getNoValue());
			} else {
				tvSatCount.setText(UpdaterUtils.getIntStr(gpsStateInfo.getCountSatellites()));
			}
			if (locationInfo.getLocation() == null) {
				tvAccuracy.setText(UpdaterUtils.getNoValue());
				tvLatitude.setText(UpdaterUtils.getNoValue());
				tvLongitude.setText(UpdaterUtils.getNoValue());
			} else {
			    if (!locationInfo.getLocation().hasAccuracy()) {
			    	tvAccuracy.setText(UpdaterUtils.getNoValue());
			    } else {
			    	tvAccuracy.setText(UpdaterUtils.getFltStr(
			    		locationInfo.getLocation().getAccuracy(), 0, Unit.Meter));
			    }
				tvLatitude.setText(UpdaterUtils.getDblStr(
					locationInfo.getLocation().getLatitude(), 4, null));
				tvLongitude.setText(UpdaterUtils.getDblStr(
					locationInfo.getLocation().getLongitude(), 4, null));
			}
		}
		
	}
	
	private static void updateTvHeartrate(Activity act, HeartrateInfo heartrateInfo) {
		TextView tvCurrent = UpdaterUtils.tv(act, R.id.tvMainDetails_HeartrateCurrent);
		TextView tvMinimum = UpdaterUtils.tv(act, R.id.tvMainDetails_HeartrateMinimum);
		TextView tvAverage = UpdaterUtils.tv(act, R.id.tvMainDetails_HeartrateAverage);
		TextView tvMaximum = UpdaterUtils.tv(act, R.id.tvMainDetails_HeartrateMaximum);
		
		if (heartrateInfo == null) {
			tvCurrent.setText(UpdaterUtils.getNoValue());
			tvMinimum.setText(UpdaterUtils.getNoValue());
			tvAverage.setText(UpdaterUtils.getNoValue());
			tvMaximum.setText(UpdaterUtils.getNoValue());
		} else {
			tvCurrent.setText(UpdaterUtils.getLongStr(heartrateInfo.getHrInBpm()));
			tvMinimum.setText(UpdaterUtils.getLongStr(heartrateInfo.getHrMinInBpm()));
			tvAverage.setText(UpdaterUtils.getLongStr(heartrateInfo.getHrAvgInBpm()));
			tvMaximum.setText(UpdaterUtils.getLongStr(heartrateInfo.getHrMaxInBpm()));
		}
	}
	
	private static void updateTvMessage(Activity act, MessageInfo messageInfo) {
		TextView tvTimestamp = UpdaterUtils.tv(act, R.id.tvMainDetails_MessageTimestamp);
		TextView tvText = UpdaterUtils.tv(act, R.id.tvMainDetails_MessageText);
		
		if (messageInfo == null) {
			tvTimestamp.setText(UpdaterUtils.getNoValue());
			tvText.setText(UpdaterUtils.getNoValue());
		} else {
			tvTimestamp.setText(UpdaterUtils.getTimestampStr(messageInfo.getTimestamp()));
			tvText.setText(UpdaterUtils.getStr(messageInfo.getMessage()));
		}	
	}
	
	private static void updateTvEmergency(Activity act, EmergencySignalInfo emergencySignalInfo) {
		TextView tvTimestamp = UpdaterUtils.tv(act, R.id.tvMainDetails_EmergencyTimestamp);
		
		if (emergencySignalInfo == null) {
			tvTimestamp.setText(UpdaterUtils.getNoValue());
		} else {
			tvTimestamp.setText(UpdaterUtils.getTimestampStr(emergencySignalInfo.getTimestamp()));
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		Activity act = MainDetailsActivity.get();
		updateTvUploader(act, UploadInfo.get());
		updateTvBattery(act, BatteryStateInfo.get());
		updateTvMobileNetwork(act, PhoneStateInfo.get());
		updateTvLocation(act, GpsStateInfo.get(), LocationInfo.get());
		updateTvHeartrate(act, HeartrateInfo.get());
		updateTvMessage(act, MessageInfo.get());
		updateTvEmergency(act, EmergencySignalInfo.get());		
	}
}
