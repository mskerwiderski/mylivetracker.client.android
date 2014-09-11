package de.msk.mylivetracker.client.android.trackingmode;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

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
	
	public enum CountdownInSecs {
		Off("off", 0),
		Secs5("5 seconds", 5),
		Secs10("10 seconds", 10),
		Secs15("15 seconds", 15),
		Secs20("20 seconds", 20),
		Secs30("30 seconds", 30),
		Secs45("45 seconds", 45),
		Min1("1 minute", 60);
		
		private String dsc;
		private int val;
		
		private CountdownInSecs(String dsc, int val) {
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
	
	// only for trackingmode standard.
	private CountdownInSecs countdownInSecs;
	
	// only for trackingmode checkpoint.
	private long maxCheckpointPeriodInSecs;
	private String checkpointMessage;
	
	// only for trackingmode auto.
	private boolean startAfterReboot;
	private boolean runOnlyIfBattFullOrCharging;
	private AutoModeResetTrackMode autoModeResetTrackMode;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.trackingMode = TrackingMode.Standard;
		this.countdownInSecs = CountdownInSecs.Off;
		this.maxCheckpointPeriodInSecs = 180;
		this.checkpointMessage = null;
		this.startAfterReboot = false;
		this.runOnlyIfBattFullOrCharging = true;
		this.autoModeResetTrackMode = AutoModeResetTrackMode.NextDay;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	public static boolean isStandard() {
		return PrefsRegistry.get(TrackingModePrefs.class).
			getTrackingMode().equals(TrackingMode.Standard);
	}	
	public static boolean isAuto() {
		return PrefsRegistry.get(TrackingModePrefs.class).
			getTrackingMode().equals(TrackingMode.Auto);
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
	public CountdownInSecs getCountdownInSecs() {
		return countdownInSecs;
	}
	public void setCountdownInSecs(CountdownInSecs countdownInSecs) {
		this.countdownInSecs = countdownInSecs;
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
	public boolean isStartAfterReboot() {
		return startAfterReboot;
	}
	public void setStartAfterReboot(boolean startAfterReboot) {
		this.startAfterReboot = startAfterReboot;
	}
	public boolean isRunOnlyIfBattFullOrCharging() {
		return runOnlyIfBattFullOrCharging;
	}
	public void setRunOnlyIfBattFullOrCharging(boolean runOnlyIfBattFullOrCharging) {
		this.runOnlyIfBattFullOrCharging = runOnlyIfBattFullOrCharging;
	}
	public AutoModeResetTrackMode getAutoModeResetTrackMode() {
		return autoModeResetTrackMode;
	}
	public void setAutoModeResetTrackMode(
			AutoModeResetTrackMode autoModeResetTrackMode) {
		this.autoModeResetTrackMode = autoModeResetTrackMode;
	}	
	@Override
	public String getShortName() {
		return "trackingmode";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("TrackingModePrefs", 
			new ConfigPair[] {
				new ConfigPair("trackingMode", this.trackingMode.name()),
				new ConfigPair("maxCheckpointPeriodInSecs", 
					String.valueOf(this.maxCheckpointPeriodInSecs)),
				new ConfigPair("checkpointMessage", this.checkpointMessage),
				new ConfigPair("startAfterReboot",
					String.valueOf(this.startAfterReboot)),
				new ConfigPair("runOnlyIfBattFullOrCharging",
					String.valueOf(this.runOnlyIfBattFullOrCharging)),
				new ConfigPair("autoModeResetTrackMode", this.autoModeResetTrackMode.name()),
		});
	}
	@Override
	public String toString() {
		return "TrackingModePrefs [trackingMode=" + trackingMode
			+ ", countdownInSecs=" + countdownInSecs
			+ ", maxCheckpointPeriodInSecs=" + maxCheckpointPeriodInSecs
			+ ", checkpointMessage=" + checkpointMessage
			+ ", startAfterReboot=" + startAfterReboot
			+ ", runOnlyIfBattFullOrCharging="
			+ runOnlyIfBattFullOrCharging + ", autoModeResetTrackMode="
			+ autoModeResetTrackMode + "]";
	}
}
