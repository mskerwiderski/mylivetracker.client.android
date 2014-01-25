package de.msk.mylivetracker.client.android.trackingmode;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;

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
	
	private AutoModeResetTrackMode autoModeResetTrackMode;
	private boolean autoStartEnabled;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.trackingMode = TrackingMode.Standard;
		this.autoModeResetTrackMode = AutoModeResetTrackMode.NextDay;
		this.autoStartEnabled = false;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public TrackingMode getTrackingMode() {
		return trackingMode;
	}
	public void setTrackingMode(TrackingMode trackingMode) {
		this.trackingMode = trackingMode;
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
	public String toString() {
		return "TrackingModePrefs [trackingMode=" + trackingMode
				+ ", autoModeResetTrackMode=" + autoModeResetTrackMode
				+ ", autoStartEnabled=" + autoStartEnabled + "]";
	}
}
