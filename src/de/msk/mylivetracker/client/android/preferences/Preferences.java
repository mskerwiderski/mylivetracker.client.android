package de.msk.mylivetracker.client.android.preferences;

import org.apache.commons.lang.StringUtils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.commons.protocol.ProtocolUtils;

/**
 * Preferences.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class Preferences {
	protected TransferProtocol transferProtocol;
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
	protected int uplTimeTriggerInSeconds;
	protected int uplDistanceTriggerInMeter;
	protected BufferSize uplPositionBufferSize;
	protected String phoneNumber;
	protected String trackName;
	protected String deviceId;
	protected String username;
	protected String password;	
	protected String seed;
	protected String locationProvider;	
	protected ConfirmLevel confirmLevel;
	protected Boolean logging; 
	
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
		fransonGpsGateHttp("Franson GpsGate HTTP", false, false),
		tk102Emulator("Tk102 Emulator", false, true),
		tk5000Emulator("Tk5000 Emulator", false, true);
		
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
	
	public static final String DB_NAME = "MyLiveTracker.DB";
	
	//
	// version < 200: reset is needed.
	// version 200: started
	// version 201: 
	// o property 'closeConnectionAfterEveryUpload' added.
	// o property 'finishEveryUploadWithALinefeed' added.
	// o property 'lineSeperator' added.
	//
	private static final int PREFERENCES_VERSION_MIN = 200;
	private static final int PREFERENCES_VERSION_VAL = 201;
	
	private static final String PREFERENCES_VERSION_VAR = "preferencesVersion";
	private static final String PREFERENCES_VAR = "preferences";
	
	private static Preferences preferences = null;
	
	public static Preferences get() {
		if (preferences == null) {			
			Preferences.load();
		} 
		return preferences;
	}
		
	public static void reset() {
		preferences = PreferencesCreator.create();
		save();
	}
	
	private static void load() {
		MainActivity mainActivity = MainActivity.get();
		SharedPreferences prefs = mainActivity.getSharedPreferences(DB_NAME, 0);				
		int preferencesVersion = prefs.getInt(PREFERENCES_VERSION_VAR, -1);
		if (preferencesVersion < PREFERENCES_VERSION_MIN) {
			Preferences.reset();
		} else {
			String preferencesStr = prefs.getString(PREFERENCES_VAR, null);
			if (!StringUtils.isEmpty(preferencesStr)) {
				try {
					Gson gson = new Gson();
					preferences = gson.fromJson(preferencesStr, Preferences.class);				
				} catch (JsonParseException e) {
					Preferences.reset();
				}
			} else {			
				Preferences.reset();			
			}
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
			PREFERENCES_VERSION_VAL);
		editor.putString(PREFERENCES_VAR, 
			gson.toJson(preferences));		
		editor.commit();	
	}	
	
	public TransferProtocol getTransferProtocol() {
		return transferProtocol;
	}

	public void setTransferProtocol(TransferProtocol transferProtocol) {
		this.transferProtocol = transferProtocol;
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
	 * @return the uplTimeTriggerInSeconds
	 */
	public int getUplTimeTriggerInSeconds() {
		return uplTimeTriggerInSeconds;
	}
	/**
	 * @param uplTimeTriggerInSeconds the uplTimeTriggerInSeconds to set
	 */
	public void setUplTimeTriggerInSeconds(int uplTimeTriggerInSeconds) {
		this.uplTimeTriggerInSeconds = uplTimeTriggerInSeconds;
	}
	/**
	 * @return the uplDistanceTriggerInMeter
	 */
	public int getUplDistanceTriggerInMeter() {
		return uplDistanceTriggerInMeter;
	}
	/**
	 * @param uplDistanceTriggerInMeter the uplDistanceTriggerInMeter to set
	 */
	public void setUplDistanceTriggerInMeter(int uplDistanceTriggerInMeter) {
		this.uplDistanceTriggerInMeter = uplDistanceTriggerInMeter;
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
	 * @return the locationProvider
	 */
	public String getLocationProvider() {
		return locationProvider;
	}
	/**
	 * @param locationProvider the locationProvider to set
	 */
	public void setLocationProvider(String locationProvider) {
		this.locationProvider = locationProvider;
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
}
