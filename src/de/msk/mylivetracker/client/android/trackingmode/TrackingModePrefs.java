package de.msk.mylivetracker.client.android.trackingmode;

import java.io.Serializable;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;

/**
 * classname: TrackingModePrefs
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-01-25	origin.
 * 
 */
public class TrackingModePrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = -283740449679486205L;
	
	public static final int VERSION = 1;
	
	public enum TrackingMode {
		Auto,
		Checkpoint,
		Standard;
	}
	
	private TrackingMode trackingMode;
	
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
	
	// only for trackingmode checkpoint.
	private long maxCheckpointPeriodInSecs;
	private String checkpointMessage;
	
	// only for trackingmode auto.
	private AutoModeResetTrackMode autoModeResetTrackMode;
	private boolean autoStartEnabled;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.trackingMode = TrackingMode.Standard;
		this.maxCheckpointPeriodInSecs = 180;
		this.checkpointMessage = null;
		this.autoModeResetTrackMode = AutoModeResetTrackMode.NextDay;
		this.autoStartEnabled = false;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	public static boolean isCheckpoint() {
		return PrefsRegistry.get(TrackingModePrefs.class).
			getTrackingMode().equals(TrackingMode.Checkpoint);
	}
	public static boolean hasCheckpointMessage() {
		return !StringUtils.isEmpty(PrefsRegistry.get(TrackingModePrefs.class).
			getCheckpointMessage());
	}
	public TrackingMode getTrackingMode() {
		return trackingMode;
	}
	public void setTrackingMode(TrackingMode trackingMode) {
		this.trackingMode = trackingMode;
	}
	public long getMaxCheckpointPeriodInSecs() {
		return maxCheckpointPeriodInSecs;
	}
	public void setMaxCheckpointPeriodInSecs(long maxCheckpointPeriodInSecs) {
		this.maxCheckpointPeriodInSecs = maxCheckpointPeriodInSecs;
	}
	public String getCheckpointMessage() {
		return checkpointMessage;
	}
	public void setCheckpointMessage(String checkpointMessage) {
		this.checkpointMessage = checkpointMessage;
	}
	public AutoModeResetTrackMode getAutoModeResetTrackMode() {
		return autoModeResetTrackMode;
	}
	public void setAutoModeResetTrackMode(
			AutoModeResetTrackMode autoModeResetTrackMode) {
		this.autoModeResetTrackMode = autoModeResetTrackMode;
	}
	public boolean isAutoStartEnabled() {
		return autoStartEnabled;
	}
	public void setAutoStartEnabled(boolean autoStartEnabled) {
		this.autoStartEnabled = autoStartEnabled;
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("TrackingModePrefs", 
			new ConfigPair[] {
				new ConfigPair("trackingMode", this.trackingMode.name()),
				new ConfigPair("maxCheckpointPeriodInSecs", 
					String.valueOf(this.maxCheckpointPeriodInSecs)),
				new ConfigPair("checkpointMessage", this.checkpointMessage),
				new ConfigPair("autoStartEnabled", 
					BooleanUtils.toStringTrueFalse(this.autoStartEnabled)),
				new ConfigPair("autoModeResetTrackMode", this.autoModeResetTrackMode.name()),
		});
	}
	@Override
	public String toString() {
		return "TrackingModePrefs [trackingMode=" + trackingMode
			+ ", maxCheckpointPeriodInSecs=" + maxCheckpointPeriodInSecs
			+ ", checkpointMessage=" + checkpointMessage
			+ ", autoModeResetTrackMode=" + autoModeResetTrackMode
			+ ", autoStartEnabled=" + autoStartEnabled + "]";
	}
}
