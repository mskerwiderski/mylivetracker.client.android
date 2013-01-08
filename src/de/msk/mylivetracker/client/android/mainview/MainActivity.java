package de.msk.mylivetracker.client.android.mainview;

import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.antplus.AntPlusHeartrateListener;
import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.auto.AutoService;
import de.msk.mylivetracker.client.android.listener.PhoneStateListener;
import de.msk.mylivetracker.client.android.localization.LocationListener;
import de.msk.mylivetracker.client.android.mainview.updater.MainDetailsViewUpdater;
import de.msk.mylivetracker.client.android.mainview.updater.MainViewUpdater;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry.InitResult;
import de.msk.mylivetracker.client.android.status.BatteryReceiver;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadService;
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
	
	public static void exit() {
		if (mainActivity != null) {
			mainActivity.onDestroy();
		}
		System.exit(0);	
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        mainActivity = this;         
        this.setTitle(R.string.tiMain);
        TrackStatus.loadTrackStatus();
        
        AntPlusHardware.init(this.getLastNonConfigurationInstance(), savedInstanceState);

        this.onResume();
        
        this.startPhoneStateListener();
        this.startBatteryReceiver();
                
    	this.getUiBtStartStop().setOnClickListener(
			new OnClickButtonStartStopListener());
    	this.getUiBtLocationListenerOnOff().setOnClickListener(
			new OnClickButtonLocationListenerOnOffListener());
		this.getUiBtReset().setOnClickListener(
			new OnClickButtonResetListener());
		this.getUiBtConnectDisconnectAnt().setOnClickListener(
			new OnClickButtonAntPlusListener());
		
		checkButtons();
		
		((Button)findViewById(R.id.btMain_SendSos)).setOnClickListener(
			new OnClickButtonSosListener(this));
		((Button)findViewById(R.id.btMain_SendMessage)).setOnClickListener(
			new OnClickButtonMessageListener(this));
		
		TextView tvAutoStartIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_AutoStartIndicator);
		tvAutoStartIndicator.setOnClickListener(
			new OnClickButtonAutoIndicatorListener());
		
		TextView tvHeadAutoStartIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_HeadAutoStartIndicator);
		tvHeadAutoStartIndicator.setOnClickListener(
			new OnClickButtonAutoIndicatorListener());
		
		TextView tvAutoModeIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_AutoModeIndicator);
		tvAutoModeIndicator.setOnClickListener(
			new OnClickButtonAutoIndicatorListener());
		
		TextView tvHeadAutoModeIndicator = UpdaterUtils.tv(mainActivity, R.id.tvMain_HeadAutoModeIndicator);
		tvHeadAutoModeIndicator.setOnClickListener(
			new OnClickButtonAutoIndicatorListener());
		
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
		
		if (App.getInitPrefsResult().equals(InitResult.PrefsImportedFromV144)) {
			SimpleInfoDialog.show(this, R.string.prefsImportedFromV144);
		} else if (App.getInitPrefsResult().equals(InitResult.PrefsCreated)) {
			SimpleInfoDialog.show(this, R.string.prefsCreated);
		} else if (App.getInitPrefsResult().equals(InitResult.PrefsUpdated)) {
			SimpleInfoDialog.show(this, R.string.prefsUpdated);
		}
		
		if (App.wasStartedForTheFirstTime()) {
			SimpleInfoDialog.show(this, 
				R.string.welcomeMessage, App.getAppNameComplete());
		}
		
		AbstractService.startService(AutoService.class);
    }	
	
	@Override
	protected void onDestroy() {
		AbstractService.stopService(AutoService.class);		
		AbstractService.stopService(UploadService.class);
		LocationListener.stop();
		MainActivity.get().stopAntPlusHeartrateListener();
		MainActivity.get().stopBatteryReceiver();
		MainActivity.get().stopPhoneStateListener();
		Chronometer chronometer = MainActivity.get().getUiChronometer();
		chronometer.stop();			
		chronometer.setBase(SystemClock.elapsedRealtime());
		TrackStatus.saveTrackStatus();
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {		
		super.onPause();
		this.getUiChronometer().stop();
		TrackStatus.saveTrackStatus();		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		TrackStatus.loadTrackStatus();
		TrackStatus trackStatus = TrackStatus.get();
		
		boolean isRunning = trackStatus.trackIsRunning();
		
        Chronometer chronometer = (Chronometer)this.findViewById(R.id.tvMain_Runtime);
       	chronometer.setBase(SystemClock.elapsedRealtime() -
			trackStatus.getRuntimeInMSecs(false));
        if (isRunning) {        
    		chronometer.start();
        }		
        
        checkButtons();
        
		this.getUiBtStartStop().setChecked(TrackStatus.get().trackIsRunning());
		this.getUiBtLocationListenerOnOff().setChecked(LocationListener.isActive());
		this.getUiBtConnectDisconnectAnt().setChecked(AntPlusManager.get().hasSensorListeners());
			
		this.updateView();				
	}
	
	private void checkButtons() {
		if (AntPlusHardware.initialized() && 
			PrefsRegistry.get(OtherPrefs.class).isAntPlusEnabledIfAvailable()) {
			if (PrefsRegistry.get(OtherPrefs.class).getTrackingOneTouchMode().
				equals(OtherPrefs.TrackingOneTouchMode.TrackingLocalizationHeartrate) &&
				PrefsRegistry.get(OtherPrefs.class).isAdaptButtonsForOneTouchMode()) {
				// ANT+ supported and enabled by user
				// one touch mode includes heartrate control
				// adaption enabled by user
				// --> hide ANT+ button.
				this.getUiBtConnectDisconnectAnt().setVisibility(View.GONE);
			} else {
				// ANT+ supported and enabled by user --> show ANT+ button.
				this.getUiBtConnectDisconnectAnt().setVisibility(View.VISIBLE);
			}
		} else {
			// ANT+ not supported or/and disabled by user --> hide ANT+ button.
			this.getUiBtConnectDisconnectAnt().setVisibility(View.GONE);
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
		if (PrefsRegistry.get(OtherPrefs.class).getTrackingOneTouchMode().
			equals(OtherPrefs.TrackingOneTouchMode.TrackingOnly)) {
			// one touch mode does not include localization control
			// --> show localization button.
			this.getUiBtLocationListenerOnOff().setVisibility(View.VISIBLE);
		} else if  (PrefsRegistry.get(OtherPrefs.class).isAdaptButtonsForOneTouchMode()) {
			// one touch mode includes localization control
			// adaption enabled by user
			// --> hide localization button.
			this.getUiBtLocationListenerOnOff().setVisibility(View.GONE);
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
	
	private ConnectivityManager connectivityManager = null;
	private TelephonyManager telephonyManager = null;	
	
	public boolean isDataConnectionActive() {
		boolean res = false;
		ConnectivityManager connectivityManager = getConnectivityManager();
		if (connectivityManager != null) {
			NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
			if ((nwInfo != null) && nwInfo.isConnected()) {
				res = true;
			}
		}
		return res;
	}
	
	public boolean isDataConnectionAvailable() {
		boolean res = false;
		ConnectivityManager connectivityManager = getConnectivityManager();
		if (connectivityManager != null) {
			NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
			if ((nwInfo != null) && nwInfo.isAvailable()) {
				res = true;
			}
		}
		return res;
	}
	
	public ConnectivityManager getConnectivityManager() {
		if (this.connectivityManager == null) {
			this.connectivityManager = (ConnectivityManager)
				this.getApplicationContext().getSystemService(
					Context.CONNECTIVITY_SERVICE);
		}
		return this.connectivityManager;
	}
	
	public TelephonyManager getTelephonyManager() {
		if (this.telephonyManager == null) {
			this.telephonyManager = (TelephonyManager)
				this.getApplicationContext().getSystemService(
					Context.TELEPHONY_SERVICE);
		}
		return this.telephonyManager;
	}
	
	private Boolean isPhoneTypeGsm = null;
	public boolean isPhoneTypeGsm() {
		if (isPhoneTypeGsm != null) {
			return isPhoneTypeGsm;
		}
		int phoneType = getTelephonyManager().getPhoneType();
		isPhoneTypeGsm = (phoneType == TelephonyManager.PHONE_TYPE_GSM);
		return isPhoneTypeGsm();
	}
	
	public void startBatteryReceiver() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        this.registerReceiver(BatteryReceiver.get(), filter);
        BatteryReceiver.get().setActive(true);
	}
	
	public void stopBatteryReceiver() {
		if (BatteryReceiver.get().isActive()) {
			this.unregisterReceiver(BatteryReceiver.get());
			BatteryReceiver.get().setActive(false);
		}
	}
	
	public void startPhoneStateListener() {
		this.getTelephonyManager().listen(
			PhoneStateListener.get(), 
				PhoneStateListener.LISTEN_SERVICE_STATE |
				PhoneStateListener.LISTEN_CELL_LOCATION |
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
	}
	
	public void stopPhoneStateListener() {
		this.getTelephonyManager().listen(
			PhoneStateListener.get(), PhoneStateListener.LISTEN_NONE);
	}

	public void startAntPlusHeartrateListener() {
		AntPlusManager.get().requestSensorUpdates(
			AntPlusHeartrateListener.get());
	}
	
	public void stopAntPlusHeartrateListener() {
		AntPlusManager.get().removeUpdates(
			AntPlusHeartrateListener.get());
	}
	
	public void updateView() {
		if (MainDetailsActivity.get() == null) {
			this.runOnUiThread(new MainViewUpdater());
		} else {
			this.runOnUiThread(new MainDetailsViewUpdater());
		}
	}
		
	public static Locale getLocale() {
		return MainActivity.get().getResources().getConfiguration().locale;
	}
	
	public Chronometer getUiChronometer() {
		return (Chronometer)this.findViewById(R.id.tvMain_Runtime);
	}
	public ToggleButton getUiBtStartStop() {
		return (ToggleButton)findViewById(R.id.btMain_StartStopTrack);
	}
	public ToggleButton getUiBtLocationListenerOnOff() {
		return (ToggleButton)findViewById(R.id.btMain_LocationListenerOnOff);
	}
	public Button getUiBtReset() {
		return (Button)findViewById(R.id.btMain_ResetTrack);
	}
	public ToggleButton getUiBtConnectDisconnectAnt() {
		return (ToggleButton)findViewById(R.id.btMain_ConnectDisconnectAnt);
	}	
}