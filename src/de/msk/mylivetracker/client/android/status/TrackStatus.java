package de.msk.mylivetracker.client.android.status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.TimeUtils;

/**
 * classname: TrackStatus
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class TrackStatus implements Serializable {
	
	private static final long serialVersionUID = 1705600083488798901L;
	
	private String trackId = null;
	private String logFileName = null;
	private Float trackDistanceInMtr = 0.0f;
	private Long markerFirstStarted = null;
	private Long markerLastStarted = null;
	private Long markerFirstPositionReceived = null;
	private Long markerLastPositionReceived = null;
	private Long runtimeAfterLastStopInMSecs = 0L;	
	private String antPlusStatus = null;
	private String antPlusHeartrateStatus = null;
	private Float mileageInMtr = 0.0f;		
	private Long lastAutoModeStopSignalReceived = null;
	private Long markerCountdownStarted = null;
	
	private static TrackStatus trackStatus;
		
	private static final String TRACK_STATUS_VERSION_VAR = "trackStatusVersion";
	private static final String TRACK_STATUS_VAR = "trackStatus";	
	
	public TrackStatus deepCopy() {
		TrackStatus trackStatusCloned = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			Object deepCopy = ois.readObject();
			trackStatusCloned = (TrackStatus)deepCopy;
		} catch (Exception e) {
			throw new RuntimeException(e);
	    }
	    return trackStatusCloned;
	}

	public static void saveTrackStatus() {
		if (trackStatus == null) return;				
		SharedPreferences prefs = App.get().
			getSharedPreferences(App.getStatusFileName(false), 0);
		SharedPreferences.Editor editor = prefs.edit();
		TrackStatus temp = trackStatus.deepCopy();
		temp.markAsStopped();
		editor.putLong(
			TRACK_STATUS_VERSION_VAR, 
			serialVersionUID);
		Gson gson = StatusDeSerializer.get();
		editor.putString(TRACK_STATUS_VAR, gson.toJson(temp));		
		AbstractInfo.save(editor, gson, UploadInfo.class, UploadInfo.get());
		AbstractInfo.save(editor, gson, LocationInfo.class, LocationInfo.get());
		AbstractInfo.save(editor, gson, HeartrateInfo.class, HeartrateInfo.get());
		AbstractInfo.save(editor, gson, MessageInfo.class, MessageInfo.get());
		AbstractInfo.save(editor, gson, EmergencySignalInfo.class, EmergencySignalInfo.get());
		AbstractInfo.save(editor, gson, BatteryStateInfo.class, BatteryStateInfo.get());
		AbstractInfo.save(editor, gson, GpsStateInfo.class, GpsStateInfo.get());
		AbstractInfo.save(editor, gson, PhoneStateInfo.class, PhoneStateInfo.get());
		AbstractInfo.save(editor, gson, PositionBufferInfo.class, PositionBufferInfo.get());
		editor.commit();			
	}
	
	private static void loadTrackStatus() {
		if (trackStatus != null) return;
		try {
			SharedPreferences prefs = App.get().
				getSharedPreferences(App.getStatusFileName(false), 0);		
			long trackVersion = prefs.getLong(TRACK_STATUS_VERSION_VAR, -1);
			if (trackVersion != serialVersionUID) {
				TrackStatus.reset();
			} else {
				String trackStatusStr = prefs.getString(TRACK_STATUS_VAR, null);
				if (!StringUtils.isEmpty(trackStatusStr)) {
					try {
						Gson gson = StatusDeSerializer.get();
						trackStatus = gson.fromJson(trackStatusStr, TrackStatus.class);
						UploadInfo.set(AbstractInfo.load(prefs, gson, UploadInfo.class));
						LocationInfo.set(AbstractInfo.load(prefs, gson, LocationInfo.class));
						HeartrateInfo.set(AbstractInfo.load(prefs, gson, HeartrateInfo.class));
						MessageInfo.set(AbstractInfo.load(prefs, gson, MessageInfo.class));
						EmergencySignalInfo.set(AbstractInfo.load(prefs, gson, EmergencySignalInfo.class));
						BatteryStateInfo.set(AbstractInfo.load(prefs, gson, BatteryStateInfo.class));
						GpsStateInfo.set(AbstractInfo.load(prefs, gson, GpsStateInfo.class));
						PhoneStateInfo.set(AbstractInfo.load(prefs, gson, PhoneStateInfo.class));
						PositionBufferInfo.set(AbstractInfo.load(prefs, gson, PositionBufferInfo.class));
					} catch (JsonParseException e) {
						LogUtils.info(TrackStatus.class, "loadTrackStatus failed: " + e.toString());
						TrackStatus.reset();
					}
				} else {			
					LogUtils.info(TrackStatus.class, "loadTrackStatus failed: no track status found");
					TrackStatus.reset();			
				}
			}
		} catch (Exception e) {
			LogUtils.info(TrackStatus.class, "loadTrackStatus failed: " + e.toString());
			TrackStatus.reset();
		}
	}
	
	public static TrackStatus get() {
		if (trackStatus == null) {
			TrackStatus.loadTrackStatus();
		}
		return trackStatus;
	}
	
	public static void resetMileage() {
		trackStatus.mileageInMtr = 0.0f;
		saveTrackStatus();
	}
	
	private static boolean inResettingState = false;
	
	public static boolean isInResettingState() {
		return inResettingState;
	}
	
	public static void reset() {
		inResettingState = true;
		String lastAntPlusStatus = (trackStatus != null) ? trackStatus.antPlusStatus : null;
		float mileageInMtr = (trackStatus != null) ? trackStatus.mileageInMtr : 0.0f;
		float trackDistanceInMtr = 0.0f;
		if ((trackStatus != null) && !StringUtils.isEmpty(trackStatus.logFileName)) {
			App.get().deleteFile(trackStatus.logFileName);
		}
		trackStatus = new TrackStatus();
		trackStatus.antPlusStatus = lastAntPlusStatus;
		trackStatus.mileageInMtr = mileageInMtr;
		trackStatus.trackDistanceInMtr = trackDistanceInMtr;
		trackStatus.trackId = UUID.randomUUID().toString();
		trackStatus.logFileName = "track_" + trackStatus.trackId + ".log";
		PositionBufferInfo.reset();
		PhoneStateInfo.reset();
		BatteryStateInfo.reset();
		LocationInfo.reset();
		GpsStateInfo.reset();				
		HeartrateInfo.reset();
		EmergencySignalInfo.reset();
		MessageInfo.reset();
		UploadInfo.reset();
		saveTrackStatus();
		inResettingState = false;
	}		

	public String getTrackId() {
		return trackId;
	}
		
	public String getLogFileName() {
		return logFileName;
	}

	public boolean trackIsRunning() {
		return this.markerLastStarted != null;
	}
	
	public boolean countdownIsActive() {
		return this.trackIsRunning() && 
			(getCountdownLeftInSecs() > 0);
	}
	
	public int getCountdownLeftInSecs() {
		int secs = 0;
		if (TrackingModePrefs.isStandard() && 
			(this.markerCountdownStarted != null)) {
			long msecs = 
				PrefsRegistry.get(TrackingModePrefs.class).
				getCountdownInSecs() * 1000L - 
				(TimeUtils.getElapsedTimeInMSecs() - 
				this.markerCountdownStarted);
			if (msecs > 0) {
				secs = Math.round(msecs / 1000);
			}
		}
		return secs;
	}
	
	public void markAsStarted() {
		long curr = TimeUtils.getElapsedTimeInMSecs();
		if (!this.trackIsRunning()) {
			this.markerLastStarted = 
				curr + 
				PrefsRegistry.get(TrackingModePrefs.class).
				getCountdownInSecs() * 1000L;
			if (this.markerFirstStarted == null) {
				this.markerFirstStarted = 
					this.markerLastStarted;
			}
		}
		this.markerCountdownStarted = curr;
	}
	
	public void markAsStopped() {
		if (this.trackIsRunning()) {
			long curr = TimeUtils.getElapsedTimeInMSecs();
			// check if countdown was canceled.
			if (this.markerLastStarted < curr) {
				this.runtimeAfterLastStopInMSecs +=
					curr - this.markerLastStarted;
			}
			this.markerLastStarted = null;
		}		
	}

	public Long getMarkerFirstPositionReceived() {
		return markerFirstPositionReceived;
	}

	public Long getMarkerLastPositionReceived() {
		return markerLastPositionReceived;
	}

	public void updateMarkersFirstAndLastPositionReceived(LocationInfo locationInfo) {
		if (locationInfo == null) {
			throw new IllegalArgumentException("locationInfo must not be null!");
		}
		if (this.markerFirstPositionReceived == null) {
			this.markerFirstPositionReceived = 
				locationInfo.getTimestamp().getTime();
		}
		this.markerLastPositionReceived = 
			locationInfo.getTimestamp().getTime();
	}

	public Long getStartedInMSecs() {
		return markerFirstStarted;
	}

	public Long getLastStartedInMSecs() {
		return this.markerLastStarted;
	}
	
	public Long getRuntimeInMSecs(boolean pausesIncluded) {
		Long runtimeInMSecs = 0L;
		if (!pausesIncluded) {
			runtimeInMSecs = 
				this.runtimeAfterLastStopInMSecs;
			if (this.trackIsRunning()) {
				runtimeInMSecs += 
					TimeUtils.getElapsedTimeInMSecs() -
					this.markerLastStarted;
			}
		} else {
			if ((this.markerFirstStarted != null) && 
				(this.markerFirstStarted > 0)) {
				runtimeInMSecs = 
					TimeUtils.getElapsedTimeInMSecs() -
					this.markerFirstStarted;
			} 
		}
		return runtimeInMSecs;
	}

	public String getRuntimeAsPrettyStr(boolean pausesIncluded) {
		long msecs = this.getRuntimeInMSecs(pausesIncluded);
		long secs = msecs / 1000L;
		long mins = secs / 60L;
		long hours = mins / 60L;
		long restOfSecs = secs - (mins * 60L);
		long restOfMins = mins - (hours * 60L);
		return 
			StringUtils.leftPad(String.valueOf(hours), 2, '0') + ":" +
			StringUtils.leftPad(String.valueOf(restOfMins), 2, '0') + ":" +
			StringUtils.leftPad(String.valueOf(restOfSecs), 2, '0');
	}
	
	public String getAntPlusStatus() {
		return antPlusStatus;
	}

	public void setAntPlusStatus(String antPlusStatus) {
		this.antPlusStatus = antPlusStatus;
	}

	public String getAntPlusHeartrateStatus() {
		return antPlusHeartrateStatus;
	}

	public void setAntPlusHeartrateStatus(String antPlusHeartrateStatus) {
		this.antPlusHeartrateStatus = antPlusHeartrateStatus;
	}

	public Float getMileageInMtr() {
		return mileageInMtr;
	}
	
	public Float getTrackDistanceInMtr() {
		return trackDistanceInMtr;
	}

	public void setTrackDistanceInMtr(Float trackDistanceInMtr) {
		this.trackDistanceInMtr = trackDistanceInMtr;
	}

	public void setMileageInMtr(Float mileageInMtr) {
		this.mileageInMtr = mileageInMtr;
	}

	public Long getLastAutoModeStopSignalReceived() {
		return lastAutoModeStopSignalReceived;
	}

	public void updateLastAutoModeStopSignalReceived() {
		this.lastAutoModeStopSignalReceived = 
			TimeUtils.getElapsedTimeInMSecs();
	}
}
