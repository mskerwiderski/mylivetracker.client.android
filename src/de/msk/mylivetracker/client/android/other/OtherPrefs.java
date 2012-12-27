package de.msk.mylivetracker.client.android.other;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;

/**
 * OtherPrefs.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2012-12-25 	initial.
 * 
 */
public class OtherPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 7732687590338383772L;

	public static final int VERSION = 1;
	
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
	
	
	public enum TrackingOneTouchMode {
		TrackingOnly, 
		TrackingLocalization, 
		TrackingLocalizationHeartrate;
	};
	
	private ConfirmLevel confirmLevel;
	private TrackingOneTouchMode trackingOneTouchMode;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.confirmLevel = ConfirmLevel.medium;
		this.trackingOneTouchMode = TrackingOneTouchMode.TrackingOnly;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public ConfirmLevel getConfirmLevel() {
		return confirmLevel;
	}
	public void setConfirmLevel(ConfirmLevel confirmLevel) {
		this.confirmLevel = confirmLevel;
	}
	public TrackingOneTouchMode getTrackingOneTouchMode() {
		return trackingOneTouchMode;
	}
	public void setTrackingOneTouchMode(TrackingOneTouchMode trackingOneTouchMode) {
		this.trackingOneTouchMode = trackingOneTouchMode;
	}
	
	@Override
	public String toString() {
		return "OtherPrefs [confirmLevel=" + confirmLevel
				+ ", trackingOneTouchMode=" + trackingOneTouchMode + "]";
	}
}