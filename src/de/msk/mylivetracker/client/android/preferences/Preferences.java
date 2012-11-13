package de.msk.mylivetracker.client.android.preferences;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.telephony.TelephonyManager;

import com.google.gson.Gson;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.util.MyLiveTrackerUtils;
import de.msk.mylivetracker.client.android.util.VersionUtils;
import de.msk.mylivetracker.client.android.util.VersionUtils.VersionDsc;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.commons.protocol.ProtocolUtils;

/**
 * Preferences.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2012-02-19 preferences version 300 implemented.
 * 000 	2011-08-11 initial. 
 * 
 */
public class Preferences {
	protected VersionDsc versionApp;
	protected boolean firstStartOfApp;
	protected TransferProtocol transferProtocol;
	protected String statusParamsId;
	protected String server;
	protected int port;
	protected String path;
	protected int locTimeTriggerInSeconds;
	protected int locDistanceTriggerInMeter;	
	protected boolean closeConnectionAfterEveryUpload;
	protected boolean finishEveryUploadWithALinefeed;
	protected String lineSeperator;
	protected int locAccuracyRequiredInMeter;
	protected int locDistBtwTwoLocsForDistCalcRequiredInCMtr;
	protected UploadTimeTrigger uplTimeTrigger;
	@Deprecated
	protected int uplTimeTriggerInSeconds;
	protected UploadTriggerLogic uplTriggerLogic;
	protected UploadDistanceTrigger uplDistanceTrigger;
	@Deprecated
	protected int uplDistanceTriggerInMeter;
	protected BufferSize uplPositionBufferSize;
	protected String phoneNumber;
	protected String trackName;
	protected String deviceId;
	protected String username;
	protected String password;	
	protected String seed;
	protected LocalizationMode localizationMode;
	@Deprecated
	protected String locationProvider;
	protected ConfirmLevel confirmLevel;
	protected TrackingOneTouchMode trackingOneTouchMode;
	protected Boolean logging; 
	protected boolean autoModeEnabled;
	protected AutoModeResetTrackMode autoModeResetTrackMode;
	protected boolean autoStartEnabled;
	//protected UploadThreadPriorityLevel uploadThreadPriorityLevel;
	
	public enum ConfirmLevel {
		low("low"), medium("medium"), high("high");
		private String dsc;				
		private ConfirmLevel(String dsc) {
			this.dsc = dsc;
		}
		public String getDsc() {
			return dsc;
		}
		public boolean isLow() {
			return this.ordinal() >= (low.ordinal());
		}
		public boolean isMedium() {
			return this.ordinal() >= (medium.ordinal());
		}
		public boolean isHigh() {
			return this.ordinal() >= (high.ordinal());
		}
	}
	
	public enum BufferSize {
		disabled(0), pos3(3), pos5(5), pos10(10);	
		private Integer size;				
		private BufferSize(Integer size) {
			this.size = size;
		}
		public Integer getSize() {
			return size;
		}
		public boolean isDisabled() {
			return size == 0;
		}
	}
	
	public enum TransferProtocol {
		uploadDisabled("Upload disabled", false, false),
		mltHttpPlain("MLT HTTP (plain)", true, true),
		mltTcpEncrypted("MLT TCP (encrypted)", true, true),
		mltRpcEncrypted("MLT RPC (encrypted)", true, true),
		tk102Emulator("Tk102 Emulator", false, true),
		tk5000Emulator("Tk5000 Emulator", false, true),
		fransonGpsGateHttp("Franson GpsGate HTTP", false, false); // not supported since version 1400
		
		private String dsc;
		private boolean supportsSendMessage;
		private boolean supportsSendEmergencySignal;
		
		private TransferProtocol(String dsc,
			boolean supportsSendMessage,
			boolean supportsSendEmergencySignal) {
			this.dsc = dsc;
			this.supportsSendMessage = supportsSendMessage;
			this.supportsSendEmergencySignal = supportsSendEmergencySignal;
		}
		public String getDsc() {
			return dsc;
		}
		public boolean supportsSendMessage() {
			return this.supportsSendMessage;
		}
		public boolean supportsSendEmergencySignal() {
			return this.supportsSendEmergencySignal;
		}
	};
	
	public enum LocalizationMode {
		gps("GPS", true, false),
		network("Network", false, true),
		gpsAndNetwork("GPS and Network", true, true);
		
		private String dsc;
		private boolean gpsProviderEnabled;
		private boolean networkProviderEnabled;
		
		private LocalizationMode(String dsc,
			boolean gpsProviderEnabled,
			boolean networkProviderEnabled) {
			this.dsc = dsc;
			this.gpsProviderEnabled = gpsProviderEnabled;
			this.networkProviderEnabled = networkProviderEnabled;
		}
		public String getDsc() {
			return dsc;
		}
		public boolean supported() {
			boolean res = true;
			if (this.gpsProviderEnabled) {
				res = res && (MainActivity.get().getLocationManager().getProvider(LocationManager.GPS_PROVIDER) != null);
			}
			if (this.networkProviderEnabled) {
				res = res && (MainActivity.get().getLocationManager().getProvider(LocationManager.NETWORK_PROVIDER) != null);
			}
			return res;
		}
		public boolean neededProvidersEnabled() {
			boolean res = true;
			if (this.gpsProviderEnabled) {
				res = res && MainActivity.get().getLocationManager().isProviderEnabled(LocationManager.GPS_PROVIDER);
			}
			if (this.networkProviderEnabled) {
				res = res && MainActivity.get().getLocationManager().isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			}
			return res;
		}
		public boolean gpsProviderEnabled() {
			return this.gpsProviderEnabled;
		}
		public boolean networkProviderEnabled() {
			return this.networkProviderEnabled;
		}
	};
	
	public enum UploadTimeTrigger {
		Off("off", 0),
		Sec1("1 sec", 1),
		Secs3("3 secs", 3),
		Secs5("5 secs", 5),
		Secs10("10 secs", 10),
		Secs20("20 secs", 20),
		Secs30("30 secs", 30),
		Min1("1 min", 60),
		Min3("3 mins", 180),
		Min5("5 mins", 300),
		Min10("10 mins", 600),
		Min20("20 mins", 1200),
		Min30("30 mins", 1800),
		Hr1("1 hr", 3600);
        
		private String dsc;
		private int secs;
		
		private UploadTimeTrigger(String dsc, int secs) {
			this.dsc = dsc;
			this.secs = secs;
		}
		public String getDsc() {
			return dsc;
		}
		public int getSecs() {
			return secs;
		}
		public static UploadTimeTrigger findSuitable(int secs) {
			if (secs == 0) return Off;
			if (secs <= 1) return Sec1;
			if (secs <= 3) return Secs3;
			if (secs <= 5) return Secs5;
			if (secs <= 10) return Secs10;
			if (secs <= 20) return Secs20;
			if (secs <= 30) return Secs30;
			if (secs <= 60) return Min1;
			if (secs <= 180) return Min3;
			if (secs <= 300) return Min5;
			if (secs <= 600) return Min10;
			if (secs <= 1200) return Min20;
			if (secs <= 1800) return Min30;
			return Hr1;
		}
	};
	
	public enum UploadTriggerLogic {
		OR("OR", false),
		AND("AND", true);
        
		private String dsc;
		private boolean logic;
		
		private UploadTriggerLogic(String dsc, boolean logic) {
			this.dsc = dsc;
			this.logic = logic;
		}
		public String getDsc() {
			return dsc;
		}
		public boolean OR() {
			return logic == false;
		}
		public boolean AND() {
			return logic == true;
		}
	};
	
	public enum UploadDistanceTrigger {
		Off("off", 0),
		Mtr50("50 mtrs", 50),
		Mtr100("100 mtrs", 100),
		Mtr200("200 mtrs", 200),
		Mtr300("300 mtrs", 300),
		Mtr500("500 mtrs", 500),
		Km1("1 km", 1000),
		Km2("2 kms", 2000),
		Km5("5 kms", 5000),
		Km10("10 kms", 10000),
		Km50("50 kms", 50000);
        
		private String dsc;
		private int mtrs;
		
		private UploadDistanceTrigger(String dsc, int mtrs) {
			this.dsc = dsc;
			this.mtrs = mtrs;
		}
		public String getDsc() {
			return dsc;
		}
		public int getMtrs() {
			return mtrs;
		}
		public static UploadDistanceTrigger findSuitable(int mtrs) {
			if (mtrs == 0) return Off;
			if (mtrs <= 50) return Mtr50;
			if (mtrs <= 100) return Mtr100;
			if (mtrs <= 200) return Mtr200;
			if (mtrs <= 300) return Mtr300;
			if (mtrs <= 500) return Mtr500;
			if (mtrs <= 1000) return Km1;
			if (mtrs <= 2000) return Km2;
			if (mtrs <= 5000) return Km5;
			if (mtrs <= 50000) return Km10;
			return Km50;
		}
	};
	
	public enum AutoModeResetTrackMode {
		Never("never", 0),
		NextDay("next day", -1),
		Hour1("1 hour", 1),
		Hours2("2 hours", 2),
		Hours4("4 hours", 4),
		Hours8("8 hours", 8),
		Hours24("24 hours", 24);
        
		private String dsc;
		private int val;
		
		private AutoModeResetTrackMode(String dsc, int val) {
			this.dsc = dsc;
			this.val = val;
		}
		public String getDsc() {
			return dsc;
		}
		public int getVal() {
			return val;
		}
	};

	public enum TrackingOneTouchMode {
		TrackingOnly, 
		TrackingLocalization, 
		TrackingLocalizationHeartrate;
	};

	public static final String DB_NAME = "MyLiveTracker.DB";
	
	//
	// version 1400:
	// o property 'versionApp' added.
	// o transferProtocol 'fransonGpsGateHttp' is not supported anymore.
	// o property 'firstStartOfApp' added.
	// o two new values for UploadTimeTrigger added ('Sec1' and 'Secs3').
	// o property 'statusParamsId' added.
	//
	// version 300:
	// o property 'localizationMode' added and 'locationProvider' removed.
	// o property 'uplTimeTrigger' added and 'uplTimeTriggerInSeconds' removed.
	// o property 'uplDistanceTrigger' added and 'uplDistanceTriggerInMeter' removed.
	// o property 'uplTriggerLogic' added.
	// o property 'autoModeEnabled' added.
	// o property 'autoModeResetTrackMode' added.
	// o property 'autoStartEnabled' added.
	// o property 'trackingOneTouchMode' added.
	//	
	// version 201: 
	// o property 'closeConnectionAfterEveryUpload' added.
	// o property 'finishEveryUploadWithALinefeed' added.
	// o property 'lineSeperator' added.
	// 
	// version 200: started
	// version < 200: reset is needed.
	// 
	private static final int PREFERENCES_VERSION_MIN = 201;
	private static final int PREFERENCES_VERSION_300 = 300;
	private static final int PREFERENCES_VERSION_1400 = 1400;
	private static final int PREFERENCES_VERSION_CURRENT = 1500;
	
	private static final String PREFERENCES_VERSION_VAR = "preferencesVersion";
	private static final String PREFERENCES_VAR = "preferences";
	
	private static Preferences preferences = null;
	
	public static Preferences get() {
		if (preferences == null) {			
			Preferences.load();
		} 
		return preferences;
	}
	
	public static Preferences get(Context context) {
		if (preferences == null) {			
			Preferences.load(context, DB_NAME);
		} 
		return preferences;
	}
	
	public static void reset(Context context) {
		preferences = PreferencesCreator.create(context);
		save();
	}
	
	private static void load() {
		MainActivity mainActivity = MainActivity.get();
		load(mainActivity, DB_NAME);
	}
	
	private static void load(Context context, String name) {
		String infoMessage = null;
		SharedPreferences prefs = context.getSharedPreferences(name, 0);				
		int preferencesVersion = prefs.getInt(PREFERENCES_VERSION_VAR, -1);
		if (preferencesVersion == -1) {
			// first time of using the app.
			Preferences.reset(context);
		} else if (preferencesVersion < PREFERENCES_VERSION_MIN) {
			Preferences.reset(context);
			infoMessage = context.getString(R.string.prefsReset, 
				VersionUtils.get().toString());
		} else {
			String preferencesStr = prefs.getString(PREFERENCES_VAR, null);
			if (!StringUtils.isEmpty(preferencesStr)) {
				try {
					Gson gson = new Gson();
					preferences = gson.fromJson(preferencesStr, Preferences.class);
					boolean doSave = false;
					if (preferencesVersion < PREFERENCES_VERSION_300) {
						// device id is read only.
						preferences.deviceId =  
							((TelephonyManager)MainActivity.get().getSystemService(
								Context.TELEPHONY_SERVICE)).getDeviceId();
						// locationProvider is deprecated --> localizationMode is used instead.
						if (StringUtils.equals(preferences.locationProvider, LocationManager.GPS_PROVIDER)) {
							preferences.localizationMode = LocalizationMode.gps;
						} else if (StringUtils.equals(preferences.locationProvider, LocationManager.NETWORK_PROVIDER)) {
							preferences.localizationMode = LocalizationMode.network;
						}
						// uplTimeTriggerInSeconds is deprecated --> uplTimeTrigger is used instead.
						preferences.uplTimeTrigger = UploadTimeTrigger.findSuitable(preferences.uplTimeTriggerInSeconds);
						// uplTriggerLogic is new --> default value is OR (same behaviour as hardwired before).
						preferences.uplTriggerLogic = UploadTriggerLogic.OR;
						// uplDistanceTriggerInMeter is deprecated --> uplDistanceTrigger is used instead.
						preferences.uplDistanceTrigger = UploadDistanceTrigger.findSuitable(preferences.uplDistanceTriggerInMeter);
						preferences.autoModeEnabled = false;
						preferences.autoModeResetTrackMode = AutoModeResetTrackMode.NextDay;
						preferences.autoStartEnabled = false;
						preferences.trackingOneTouchMode = TrackingOneTouchMode.TrackingOnly;
						doSave = true;
					} 
					if (preferencesVersion < PREFERENCES_VERSION_1400) {
						preferences.firstStartOfApp = true;
						preferences.statusParamsId = null;
						if (preferences.transferProtocol.equals(TransferProtocol.fransonGpsGateHttp)) {
							preferences.transferProtocol = TransferProtocol.uploadDisabled;
						} else if (preferences.transferProtocol.equals(TransferProtocol.mltTcpEncrypted)) {
							preferences.server = MyLiveTrackerUtils.getServerDns();
						}
						doSave = true;
					}
					if (!VersionUtils.isCurrent(context, preferences.versionApp)) {
						preferences.firstStartOfApp = true;
						preferences.versionApp = VersionUtils.get();
					}
					if (doSave) {
						save();
						infoMessage = context.getString(R.string.prefsUpdated, 
							VersionUtils.get().toString());
					}
				} catch (Exception e) {
					Preferences.reset(context);
					infoMessage = context.getString(R.string.prefsReset, 
						VersionUtils.get().toString());
				}
			} else {			
				Preferences.reset(context);			
				infoMessage = context.getString(R.string.prefsReset, 
					VersionUtils.get().toString());
			}
		}
		if (!StringUtils.isEmpty(infoMessage) && MainActivity.exists()) {
			SimpleInfoDialog infoDlg = new SimpleInfoDialog(
				MainActivity.get(), infoMessage);
			infoDlg.show();
		}
	}
	
	public static void save() {
		if (preferences == null) return;				
		SharedPreferences prefs = MainActivity.get().
			getSharedPreferences(Preferences.DB_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		
		preferences.seed = ProtocolUtils.calcSeed(
			preferences.deviceId, 
			preferences.username, 
			preferences.password);
		
		Gson gson = new Gson();			
		editor.putInt(
			PREFERENCES_VERSION_VAR, 
			PREFERENCES_VERSION_CURRENT);
		editor.putString(PREFERENCES_VAR, 
			gson.toJson(preferences));		
		editor.commit();	
	}	

	public static boolean firstStartOfApp() {
		boolean res =
			preferences.firstStartOfApp;
		if (preferences.firstStartOfApp) {
			preferences.firstStartOfApp = false;
			save();
		}
		return res;
	}
	
	public TransferProtocol getTransferProtocol() {
		return transferProtocol;
	}
	public void setTransferProtocol(TransferProtocol transferProtocol) {
		this.transferProtocol = transferProtocol;
	}
	public String getStatusParamsId() {
		return statusParamsId;
	}
	public void setStatusParamsId(String statusParamsId) {
		this.statusParamsId = statusParamsId;
	}

	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}
	/**
	 * @param server the server to set
	 */
	public void setServer(String server) {
		this.server = server;
	}
	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return the locTimeTriggerInSeconds
	 */
	public int getLocTimeTriggerInSeconds() {
		return locTimeTriggerInSeconds;
	}
	/**
	 * @param locTimeTriggerInSeconds the locTimeTriggerInSeconds to set
	 */
	public void setLocTimeTriggerInSeconds(int locTimeTriggerInSeconds) {
		this.locTimeTriggerInSeconds = locTimeTriggerInSeconds;
	}
	/**
	 * @return the locDistanceTriggerInMeter
	 */
	public int getLocDistanceTriggerInMeter() {
		return locDistanceTriggerInMeter;
	}
	/**
	 * @param locDistanceTriggerInMeter the locDistanceTriggerInMeter to set
	 */
	public void setLocDistanceTriggerInMeter(int locDistanceTriggerInMeter) {
		this.locDistanceTriggerInMeter = locDistanceTriggerInMeter;
	}
	/**
	 * @return the closeConnectionAfterEveryUpload
	 */
	public boolean isCloseConnectionAfterEveryUpload() {
		return closeConnectionAfterEveryUpload;
	}
	/**
	 * @param closeConnectionAfterEveryUpload the closeConnectionAfterEveryUpload to set
	 */
	public void setCloseConnectionAfterEveryUpload(
			boolean closeConnectionAfterEveryUpload) {
		this.closeConnectionAfterEveryUpload = closeConnectionAfterEveryUpload;
	}
	/**
	 * @return the finishEveryUploadWithALinefeed
	 */
	public boolean isFinishEveryUploadWithALinefeed() {
		return finishEveryUploadWithALinefeed;
	}
	/**
	 * @param finishEveryUploadWithALinefeed the finishEveryUploadWithALinefeed to set
	 */
	public void setFinishEveryUploadWithALinefeed(
			boolean finishEveryUploadWithALinefeed) {
		this.finishEveryUploadWithALinefeed = finishEveryUploadWithALinefeed;
	}
	/**
	 * @return the lineSeperator
	 */
	public String getLineSeperator() {
		return lineSeperator;
	}
	/**
	 * @param lineSeperator the lineSeperator to set
	 */
	public void setLineSeperator(String lineSeperator) {
		this.lineSeperator = lineSeperator;
	}
	/**
	 * @return the locAccuracyRequiredInMeter
	 */
	public int getLocAccuracyRequiredInMeter() {
		return locAccuracyRequiredInMeter;
	}
	/**
	 * @param locAccuracyRequiredInMeter the locAccuracyRequiredInMeter to set
	 */
	public void setLocAccuracyRequiredInMeter(int locAccuracyRequiredInMeter) {
		this.locAccuracyRequiredInMeter = locAccuracyRequiredInMeter;
	}
	/**
	 * @return the locDistBtwTwoLocsForDistCalcRequiredInCMtr
	 */
	public int getLocDistBtwTwoLocsForDistCalcRequiredInCMtr() {
		return locDistBtwTwoLocsForDistCalcRequiredInCMtr;
	}
	/**
	 * @param locDistBtwTwoLocsForDistCalcRequiredInCMtr the locDistBtwTwoLocsForDistCalcRequiredInCMtr to set
	 */
	public void setLocDistBtwTwoLocsForDistCalcRequiredInCMtr(
			int locDistBtwTwoLocsForDistCalcRequiredInCMtr) {
		this.locDistBtwTwoLocsForDistCalcRequiredInCMtr = locDistBtwTwoLocsForDistCalcRequiredInCMtr;
	}
	
	/**
	 * @return the uplTimeTrigger
	 */
	public UploadTimeTrigger getUplTimeTrigger() {
		return uplTimeTrigger;
	}

	/**
	 * @param uplTimeTrigger the uplTimeTrigger to set
	 */
	public void setUplTimeTrigger(UploadTimeTrigger uplTimeTrigger) {
		this.uplTimeTrigger = uplTimeTrigger;
	}

	/**
	 * @return the uplTriggerLogic
	 */
	public UploadTriggerLogic getUplTriggerLogic() {
		return uplTriggerLogic;
	}

	/**
	 * @param uplTriggerLogic the uplTriggerLogic to set
	 */
	public void setUplTriggerLogic(UploadTriggerLogic uplTriggerLogic) {
		this.uplTriggerLogic = uplTriggerLogic;
	}

	/**
	 * @return the uplDistanceTrigger
	 */
	public UploadDistanceTrigger getUplDistanceTrigger() {
		return uplDistanceTrigger;
	}

	/**
	 * @param uplDistanceTrigger the uplDistanceTrigger to set
	 */
	public void setUplDistanceTrigger(UploadDistanceTrigger uplDistanceTrigger) {
		this.uplDistanceTrigger = uplDistanceTrigger;
	}

	/**
	 * @return the uplPositionBufferSize
	 */
	public BufferSize getUplPositionBufferSize() {
		return uplPositionBufferSize;
	}
	/**
	 * @param uplPositionBufferSize the uplPositionBufferSize to set
	 */
	public void setUplPositionBufferSize(BufferSize uplPositionBufferSize) {
		this.uplPositionBufferSize = uplPositionBufferSize;
	}
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}
	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/**
	 * @return the trackName
	 */
	public String getTrackName() {
		return trackName;
	}
	/**
	 * @param trackName the trackName to set
	 */
	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getSeed() {
		return seed;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the localizationMode
	 */
	public LocalizationMode getLocalizationMode() {
		return localizationMode;
	}

	/**
	 * @param localizationMode the localizationMode to set
	 */
	public void setLocalizationMode(LocalizationMode localizationMode) {
		this.localizationMode = localizationMode;
	}
	/**
	 * @return the confirmLevel
	 */
	public ConfirmLevel getConfirmLevel() {
		return confirmLevel;
	}

	/**
	 * @param confirmLevel the confirmLevel to set
	 */
	public void setConfirmLevel(ConfirmLevel confirmLevel) {
		this.confirmLevel = confirmLevel;
	}

	/**
	 * @return the logging
	 */
	public Boolean getLogging() {
		return logging;
	}
	/**
	 * @param logging the logging to set
	 */
	public void setLogging(Boolean logging) {
		this.logging = logging;
	}

	/**
	 * @return the autoModeEnabled
	 */
	public boolean isAutoModeEnabled() {
		return autoModeEnabled;
	}

	/**
	 * @param autoModeEnabled the autoModeEnabled to set
	 */
	public void setAutoModeEnabled(boolean autoModeEnabled) {
		this.autoModeEnabled = autoModeEnabled;
	}

	public AutoModeResetTrackMode getAutoModeResetTrackMode() {
		return autoModeResetTrackMode;
	}

	public void setAutoModeResetTrackMode(
			AutoModeResetTrackMode autoModeResetTrackMode) {
		this.autoModeResetTrackMode = autoModeResetTrackMode;
	}

	/**
	 * @return the autoStartEnabled
	 */
	public boolean isAutoStartEnabled() {
		return autoStartEnabled;
	}

	/**
	 * @param autoStartEnabled the autoStartEnabled to set
	 */
	public void setAutoStartEnabled(boolean autoStartEnabled) {
		this.autoStartEnabled = autoStartEnabled;
	}

	public TrackingOneTouchMode getTrackingOneTouchMode() {
		return trackingOneTouchMode;
	}

	public void setTrackingOneTouchMode(TrackingOneTouchMode trackingOneTouchMode) {
		this.trackingOneTouchMode = trackingOneTouchMode;
	}
}
