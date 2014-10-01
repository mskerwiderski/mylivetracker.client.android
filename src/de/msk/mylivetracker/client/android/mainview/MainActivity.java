package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.auto.AutoService;
import de.msk.mylivetracker.client.android.battery.BatteryReceiver;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.mainview.updater.MainViewUpdater;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils;
import de.msk.mylivetracker.client.android.mainview.updater.ViewUpdateService;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.phonestate.PhoneStateReceiver;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry.InitResult;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;
import de.msk.mylivetracker.client.android.util.LocationManagerUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractInfoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: MainActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 001	2014-01-24	checkpoint mode implemented.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class MainActivity extends AbstractMainActivity {

	private static MainActivity mainActivity = null;	
	
	public static MainActivity get() {
		return mainActivity;
	}

	public static boolean exists() {
		return mainActivity != null;
	}
	
	@Override
	protected boolean isPrefsActivity() {
		return false;
	}
	
	private static class StartInfoDialog extends AbstractInfoDialog {

		private StartInfoDialog(Context ctx, int message, Object[] args) {
			super(ctx, ctx.getString(message, args));
		}

		public static void show(Context ctx, int message, Object... args) {
			StartInfoDialog dlg = new StartInfoDialog(ctx, message, args);
			dlg.show();
		}

		@Override
		public void onOk() {
			Activity activity = MainActivity.get();
			if (App.getInitPrefsResult().equals(InitResult.PrefsImportedFromV144)) {
				SimpleInfoDialog.show(activity, R.string.prefsImportedFromV144);
			} else if (App.getInitPrefsResult().equals(InitResult.PrefsUpdatedFromV150)) {
				SimpleInfoDialog.show(activity, R.string.prefsUpdated);
			} else if (App.getInitPrefsResult().equals(InitResult.PrefsUpdatedFromV160)) {
				SimpleInfoDialog.show(activity, R.string.prefsUpdated);
			} else if (App.getInitPrefsResult().equals(InitResult.PrefsCreated)) {
				SimpleInfoDialog.show(activity, R.string.prefsCreated);
			} else if (App.getInitPrefsResult().equals(InitResult.PrefsUpdated)) {
				SimpleInfoDialog.show(activity, R.string.prefsUpdated);
			}
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        mainActivity = this;         
        this.setTitle(R.string.tiMain);
        
        PhoneStateReceiver.register();
        BatteryReceiver.register();
        AbstractService.startService(ViewUpdateService.class);
        if (TrackingModePrefs.isAuto()) {
        	AbstractService.startService(AutoService.class);
        }
        
        this.onResume();
        
        ToggleButton btMain_StartStopTrack = (ToggleButton)
			findViewById(R.id.btMain_StartStopTrack);
    	btMain_StartStopTrack.setOnClickListener(
			new OnClickButtonStartStopListener());
    	
    	ToggleButton btMain_LocationListenerOnOff = (ToggleButton)
			findViewById(R.id.btMain_LocationListenerOnOff);
    	btMain_LocationListenerOnOff.setOnClickListener(
			new OnClickButtonLocationListenerOnOffListener());
    	
    	Button btMain_ResetTrack = (Button)
			findViewById(R.id.btMain_ResetTrack);
		btMain_ResetTrack.setOnClickListener(
			new OnClickButtonResetListener());
		
		ToggleButton btMain_ConnectDisconnectAnt = (ToggleButton)
			findViewById(R.id.btMain_ConnectDisconnectAnt);
		btMain_ConnectDisconnectAnt.setOnClickListener(
			new OnClickButtonAntPlusListener(
				btMain_ConnectDisconnectAnt));
		
		checkButtons(
			btMain_LocationListenerOnOff,
			btMain_ConnectDisconnectAnt);
		
		((Button)findViewById(R.id.btMain_SendSos)).setOnClickListener(
			new OnClickButtonSosListener(this));
		((Button)findViewById(R.id.btMain_SendMessage)).setOnClickListener(
			new OnClickButtonMessageListener(this));
		
		TextView tvAutoStartIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_AutoStartIndicator);
		tvAutoStartIndicator.setOnClickListener(
			new OnClickButtonTrackingModeIndicatorListener());
		
		TextView tvHeadAutoStartIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_HeadAutoStartIndicator);
		tvHeadAutoStartIndicator.setOnClickListener(
			new OnClickButtonTrackingModeIndicatorListener());
		
		TextView tvTrackingModeIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_TrackingModeIndicator);
		tvTrackingModeIndicator.setOnClickListener(
			new OnClickButtonTrackingModeIndicatorListener());
		
		TextView tvHeadTrackingModeIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_HeadTrackingModeIndicator);
		tvHeadTrackingModeIndicator.setOnClickListener(
			new OnClickButtonTrackingModeIndicatorListener());
		
		TextView tvLocalizationIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_LocalizationIndicator);
		tvLocalizationIndicator.setOnClickListener(
			new OnClickButtonLocalizationIndicatorListener());
		
		TextView tvHeadLocalizationIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_HeadLocalizationIndicator);
		tvHeadLocalizationIndicator.setOnClickListener(
			new OnClickButtonLocalizationIndicatorListener());
		
		TextView tvWirelessLanIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_WirelessLanIndicator);
		tvWirelessLanIndicator.setOnClickListener(
			new OnClickButtonWirelessLanIndicatorListener());
		
		TextView tvHeadWirelessLanIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_HeadWirelessLanIndicator);
		tvHeadWirelessLanIndicator.setOnClickListener(
			new OnClickButtonWirelessLanIndicatorListener());
		
		TextView tvMobileNetworkIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_MobileNetworkIndicator);
		tvMobileNetworkIndicator.setOnClickListener(
			new OnClickButtonMobileNetworkIndicatorListener());
		
		TextView tvHeadMobileNetworkIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_HeadMobileNetworkIndicator);
		tvHeadMobileNetworkIndicator.setOnClickListener(
			new OnClickButtonMobileNetworkIndicatorListener());
		
		TextView tvHeartrate = UpdaterUtils.tv(mainActivity, R.id.tvMain_Heartrate);
		tvHeartrate.setOnClickListener(
			new OnClickButtonHeartrateListener());
		
		TextView tvLocation = UpdaterUtils.tv(mainActivity, R.id.tvMain_Location);
		tvLocation.setOnClickListener(
			new OnClickButtonLocalizationListener());
		
		TextView tvUploader = UpdaterUtils.tv(mainActivity, R.id.tvMain_Uploader);
		tvUploader.setOnClickListener(
			new OnClickButtonNetworkListener());
		
		if (App.wasStartedForTheFirstTime()) {
			StartInfoDialog.show(this, 
				R.string.welcomeMessage, App.getAppNameComplete());
		}
		
		if (!LocationManagerUtils.gpsProviderSupported() &&
			!LocationManagerUtils.networkProviderSupported()) {
			SimpleInfoDialog.show(this, 
				R.string.locationProvidersNotSupported, 
				App.getAppName());
		}
    }	
	
	public static void destroy() {
		MainActivity mainActivity = MainActivity.get();
		if (mainActivity != null) {
			mainActivity.onDestroy();
		}
	}
	
	@Override
	protected void onDestroy() {
		TrackStatus.saveTrackStatus();
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {		
		super.onPause();
		TrackStatus.saveTrackStatus();		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AntPlusHardware.checkConnection();
		
        ToggleButton btMain_StartStopTrack = (ToggleButton)
			findViewById(R.id.btMain_StartStopTrack);
    	ToggleButton btMain_LocationListenerOnOff = (ToggleButton)
			findViewById(R.id.btMain_LocationListenerOnOff);
		ToggleButton btMain_ConnectDisconnectAnt = (ToggleButton)
			findViewById(R.id.btMain_ConnectDisconnectAnt);
		
		checkButtons(
			btMain_LocationListenerOnOff,
			btMain_ConnectDisconnectAnt);	

		btMain_StartStopTrack.setChecked(TrackStatus.get().trackIsRunning());
		btMain_LocationListenerOnOff.setChecked(
			AbstractService.isServiceRunning(LocalizationService.class));
		btMain_ConnectDisconnectAnt.setChecked(AntPlusManager.get().hasSensorListeners());
		
    	if (PrefsRegistry.get(TrackingModePrefs.class).getTrackingMode().equals(TrackingMode.Checkpoint)) {
    		btMain_StartStopTrack.setText(R.string.btMain_Checkpoint);
    		btMain_StartStopTrack.setTextOn(App.getResStr(R.string.btMain_Checkpoint));
    		btMain_StartStopTrack.setTextOff(App.getResStr(R.string.btMain_Checkpoint));
    	} else {
    		btMain_StartStopTrack.setText(R.string.btMain_Tracking);
    		btMain_StartStopTrack.setTextOn(App.getResStr(R.string.btMain_Tracking));
    		btMain_StartStopTrack.setTextOff(App.getResStr(R.string.btMain_Tracking));
    	}
	}
	
	private void checkButtons(
		ToggleButton btMain_LocationListenerOnOff,
		ToggleButton btMain_ConnectDisconnectAnt) {
		if (AntPlusHardware.initialized() && 
			PrefsRegistry.get(OtherPrefs.class).isAntPlusEnabledIfAvailable()) {
			if (PrefsRegistry.get(OtherPrefs.class).getTrackingOneTouchMode().
				equals(OtherPrefs.TrackingOneTouchMode.TrackingLocalizationHeartrate) &&
				PrefsRegistry.get(OtherPrefs.class).isAdaptButtonsForOneTouchMode()) {
				// ANT+ supported and enabled by user
				// one touch mode includes heartrate control
				// adaption enabled by user
				// --> hide ANT+ button.
				btMain_ConnectDisconnectAnt.setVisibility(View.GONE);
			} else {
				// ANT+ supported and enabled by user --> show ANT+ button.
				btMain_ConnectDisconnectAnt.setVisibility(View.VISIBLE);
			}
		} else {
			// ANT+ not supported or/and disabled by user --> hide ANT+ button.
			btMain_ConnectDisconnectAnt.setVisibility(View.GONE);
		}
		LinearLayout llHeartrate = (LinearLayout)this.findViewById(R.id.llMain_Heartrate);
		if (AntPlusHardware.initialized() && 
			PrefsRegistry.get(OtherPrefs.class).isAntPlusEnabledIfAvailable()) {
			// ANT+ supported and enabled by user --> show heartrate info.
			llHeartrate.setVisibility(View.VISIBLE);
		} else {
			// ANT+ not supported or/and disabled by user --> hide heartrate info.
			llHeartrate.setVisibility(View.GONE);
		}
		if (!PrefsRegistry.get(OtherPrefs.class).getTrackingOneTouchMode().
			equals(OtherPrefs.TrackingOneTouchMode.TrackingOnly) &&
			PrefsRegistry.get(OtherPrefs.class).isAdaptButtonsForOneTouchMode()) {
			btMain_LocationListenerOnOff.setVisibility(View.GONE);
		} else {
			btMain_LocationListenerOnOff.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onSwitchToView(boolean next) {
		startActivity(new Intent(this, MainDetailsActivity.class));	
	}
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainDetailsActivity.class));
	}
	
	@Override
	public Class<? extends Runnable> getViewUpdater() {
		return MainViewUpdater.class;
	}
}