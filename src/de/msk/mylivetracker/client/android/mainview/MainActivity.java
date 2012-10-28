package de.msk.mylivetracker.client.android.mainview;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.wahoofitness.api.WFAntException;
import com.wahoofitness.api.WFAntNotSupportedException;
import com.wahoofitness.api.WFAntServiceNotInstalledException;
import com.wahoofitness.api.WFDisplaySettings;
import com.wahoofitness.api.WFHardwareConnector;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.automode.AutoManager;
import de.msk.mylivetracker.client.android.listener.AntPlusHeartrateListener;
import de.msk.mylivetracker.client.android.listener.AntPlusListener;
import de.msk.mylivetracker.client.android.listener.AntPlusManager;
import de.msk.mylivetracker.client.android.listener.GpsStateListener;
import de.msk.mylivetracker.client.android.listener.LocationListener;
import de.msk.mylivetracker.client.android.listener.NmeaListener;
import de.msk.mylivetracker.client.android.listener.PhoneStateListener;
import de.msk.mylivetracker.client.android.mainview.updater.MainDetailsViewUpdater;
import de.msk.mylivetracker.client.android.mainview.updater.MainViewUpdater;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.receiver.BatteryReceiver;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadService;

/**
 * MainActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2012-02-21
 * 		o property 'localizationMode' implemented (gps, network, gpsAndNetwork).
 * 		o listener to autoModeIndicator added.
 *      o isDataConnectionAvailable implemented.
 *      o 'onBackPressed' implemented.
 *      o 'exit' implemented.
 *      o 'exitHandler' implemented.
 * 000 	2011-08-11 initial.
 * 
 */
public class MainActivity extends AbstractMainActivity {
    
	private static MainActivity mainActivity = null;	
	
	public static MainActivity get() {
		return mainActivity;
	}
	
	public static void exit() {
		if (mainActivity != null) {
			mainActivity.onDestroy();
			MainActivity.logInfo("Exit: main window closed.");
		}
		MainActivity.logInfo("Exit: exit app.");
		System.exit(0);	
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.main);
        mainActivity = this;         
                
        this.setTitle(R.string.tiMain);
        
        TrackStatus.loadTrackStatus();
        
        AutoManager.get();
        
		Context context = this.getApplicationContext();
		AntPlusManager antPlusListener = AntPlusManager.get();
		
		String statusStr = null;
		// check for ANT hardware support.
        if (isAntPlusSupported()) {
	        try {
	        	boolean bResumed = false;
	        	// attempt to retrieve the previously suspended WFHardwareConnector instance.
	        	//
	        	// see the onRetainNonConfigurationInstance method.
	        	this.antPlusHwConnector = (WFHardwareConnector)
	        		this.getLastNonConfigurationInstance();
	        	if (this.antPlusHwConnector != null) {
	        		// attempt to resume the WFHardwareConnector instance.
	        		if (!(bResumed = this.antPlusHwConnector.resume(antPlusListener))) {
	        			// if the WFHardwareConnector instance failed to resume,
	        			// it must be re-initialized.
	        			this.antPlusHwConnector.connectAnt();
	        		}
	        	}
	        	// if there is no suspended WFHardwareConnector instance,
	        	// configure the singleton instance.
	        	else {
			         // get the hardware connector singleton instance.
	        		this.antPlusHwConnector = WFHardwareConnector.getInstance(
			        	this, antPlusListener);
	        		this.antPlusHwConnector.connectAnt();
	        	}
		        // restore connection state only if the previous
		        // WFHardwareConnector instance was not resumed.
		        if (!bResumed) {
			        // the connection state is cached in the state
			        // bundle (onSaveInstanceState).  this is used to
			        // restore previous connections.  if the Bundle
			        // is null, no connections are configured.
		        	this.antPlusHwConnector.restoreInstanceState(savedInstanceState);
		        }
		        // configure the display settings.
		        //
		        // this demonstrates how to use the display
		        // settings.  if this step is skipped, the
		        // default settings will be used.
		        WFDisplaySettings settings = this.antPlusHwConnector.getDisplaySettings();
		        settings.staleDataTimeout = 5.0f;          // seconds, default = 5
		        settings.staleDataString = "--";           // string to display when data is stale, default = "--"
		        settings.useMetricUnits = true;            // display metric units, default = false
		        settings.bikeWheelCircumference = 2.07f;   // meters, default = 2.07
		        settings.bikeCoastingTimeout = 3.0f;       // seconds, default = 3    
		        this.antPlusHwConnector.setDisplaySettings(settings);
	        } catch (WFAntNotSupportedException nse) {
	        	// ANT hardware not supported.
	        	statusStr = this.getString(R.string.antPlus_NotSupported);
	        } catch (WFAntServiceNotInstalledException nie) {

				Toast installNotification = Toast.makeText(context, 
					this.getString(R.string.antPlus_InstallInfo),
					Toast.LENGTH_LONG);
				installNotification.show();

				// open the Market Place app, search for the ANT Radio service.
				this.antPlusHwConnector.destroy();
				this.antPlusHwConnector = null;
				WFHardwareConnector.installAntService(context);

				// close this app.
				this.finish();
	        } catch (WFAntException e) {
	        	statusStr = this.getString(R.string.antPlus_InitError);
			} catch (Exception e) {
				statusStr = this.getString(R.string.antPlus_InitError);
			}
        } else {
        	// ANT hardware not supported.
        	statusStr = this.getString(R.string.antPlus_NotSupported);
        }  
        if (!StringUtils.isEmpty(statusStr)) {
        	TrackStatus.get().setAntPlusStatus(statusStr);
        }                      
        
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
		
		TextView tvLocation = UpdaterUtils.tv(mainActivity, R.id.tvMain_Location);
		tvLocation.setOnClickListener(
			new OnClickButtonLocalizationListener());
		
		TextView tvUploader = UpdaterUtils.tv(mainActivity, R.id.tvMain_Uploader);
		tvUploader.setOnClickListener(
			new OnClickButtonNetworkListener());
    }	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AutoManager.shutdown();
		UploadService.stop();									
		MainActivity.get().stopLocationListener();
		MainActivity.get().stopAntPlusHeartrateListener();
		MainActivity.get().stopBatteryReceiver();
		MainActivity.get().stopPhoneStateListener();
		Chronometer chronometer = MainActivity.get().getUiChronometer();
		chronometer.stop();			
		chronometer.setBase(SystemClock.elapsedRealtime());
		TrackStatus.saveTrackStatus();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {		
		super.onPause();
		this.getUiChronometer().stop();
		TrackStatus.saveTrackStatus();		
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
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
        
        Button btConnectDisconnectAnt = this.getUiBtConnectDisconnectAnt();
		if (!this.isAntPlusSupported()) {
        	btConnectDisconnectAnt.setClickable(false);
        } else {
        	btConnectDisconnectAnt.setClickable(true);
        }		
		
		this.getUiBtStartStop().setChecked(TrackStatus.get().trackIsRunning());
		this.getUiBtLocationListenerOnOff().setChecked(LocationListener.get().isActive());
		this.getUiBtConnectDisconnectAnt().setChecked(AntPlusManager.get().hasSensorListeners());
			
		this.updateView();				
	}
	
	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.mainview.AbstractMainActivity#onSwitchToView(boolean)
	 */
	@Override
	public void onSwitchToView(boolean next) {
		startActivity(new Intent(this, MainDetailsActivity.class));	
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, MainDetailsActivity.class));
	}

	public static Handler exitHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (MainDetailsActivity.isActive()) {
				MainActivity.logInfo("Exit: close details window.");
				MainDetailsActivity.close();
				try { Thread.sleep(1000); } catch(Exception e) {};
			}
			MainActivity.logInfo("Exit: exit main window.");
			MainActivity.exit();
	    }
	};
	
	private ConnectivityManager connectivityManager = null;
	private TelephonyManager telephonyManager = null;	
	private AntPlusManager antPlusManager = null;
	
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
	
	private WFHardwareConnector antPlusHwConnector;
	public boolean isAntPlusSupported() {
		return WFHardwareConnector.hasAntSupport(
			this.getApplicationContext());
	}
	public WFHardwareConnector getAntPlusHwConnector() {
		if (!isAntPlusSupported()) {
			return null;
		}
		return this.antPlusHwConnector;
	}
	public AntPlusManager getAntPlusManager() {
		if (this.antPlusManager == null) {
			this.antPlusManager = AntPlusManager.get();
			this.antPlusManager.setListener(AntPlusListener.get());
		}
		return this.antPlusManager;
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

	public boolean localizationEnabled() {
		return Preferences.get().getLocalizationMode().neededProvidersEnabled();
	}
	
	public void startLocationListener() {
		Preferences preferences = Preferences.get();
		this.getLocationManager().
			addGpsStatusListener(GpsStateListener.get());
		this.getLocationManager().
			addNmeaListener(NmeaListener.get());
		if (preferences.getLocalizationMode().gpsProviderEnabled()) {
			this.getLocationManager().requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 
				preferences.getLocTimeTriggerInSeconds() * 1000, 
				preferences.getLocDistanceTriggerInMeter(), 
				LocationListener.get());
		}
		if (preferences.getLocalizationMode().networkProviderEnabled()) {
			this.getLocationManager().requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 
				preferences.getLocTimeTriggerInSeconds() * 1000, 
				preferences.getLocDistanceTriggerInMeter(), 
				LocationListener.get());
		}
		LocationListener.get().setActive(true);		
	}
	
	public void startAntPlusHeartrateListener() {
		this.getAntPlusManager().requestSensorUpdates(
			AntPlusHeartrateListener.get());
	}
	
	public void stopAntPlusHeartrateListener() {
		this.getAntPlusManager().removeUpdates(
			AntPlusHeartrateListener.get());
	}
	
	public void updateView() {
		if (MainDetailsActivity.get() == null) {
			this.runOnUiThread(new MainViewUpdater());
		} else {
			this.runOnUiThread(new MainDetailsViewUpdater());
		}
	}
		
	private static final String LOG_TAG_GLOBAL = "MLT";
	
	public static void logInfo(String logStr) {
		Log.i(LOG_TAG_GLOBAL, logStr);
	}

	public static void logInfo(Class<?> clazz, String logStr) {
		String className = "unknown";
		if ((clazz != null) && !StringUtils.isEmpty(clazz.getSimpleName())) {
			className = clazz.getSimpleName();
		}
		String info =  className + ": " + logStr;
		Log.i(LOG_TAG_GLOBAL, info);
	}
	
	public static class VersionDsc {
		private int code;
		private String name;
		public VersionDsc(int code, String name) {
			this.code = code;
			this.name = name;
		}
		public int getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		@Override
		public String toString() {
			return "v" + this.name;
		}		
	}
	public static VersionDsc getVersion() {
		MainActivity mainActivity = MainActivity.get();
		VersionDsc versionDsc = new VersionDsc(1, "INVALID");
		try {
			versionDsc = new VersionDsc(
				mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionCode,	
				mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			logInfo("version info not found: " + e.getMessage());
		}
		return versionDsc;
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
	
	private String myLiveTrackerRealm = "SKERWIDERSKI";
	private String myLiveTrackerRpcServiceUrl = "http://mylivetracker.de/rpc.json";
	
	public String getMyLiveTrackerRealm() {
		return myLiveTrackerRealm;
	}
	public String getMyLiveTrackerRpcServiceUrl() {
		return myLiveTrackerRpcServiceUrl;
	}	
}