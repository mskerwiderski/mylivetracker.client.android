package de.msk.mylivetracker.client.android.other;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;

/**
 * classname: OtherPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
	private boolean adaptButtonsForOneTouchMode;
	private boolean antPlusEnabledIfAvailable;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.confirmLevel = ConfirmLevel.high;
		this.trackingOneTouchMode = TrackingOneTouchMode.TrackingLocalization;
		this.adaptButtonsForOneTouchMode = false;
		this.antPlusEnabledIfAvailable = true;
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
	public boolean isAdaptButtonsForOneTouchMode() {
		return adaptButtonsForOneTouchMode;
	}
	public void setAdaptButtonsForOneTouchMode(boolean adaptButtonsForOneTouchMode) {
		this.adaptButtonsForOneTouchMode = adaptButtonsForOneTouchMode;
	}
	public boolean isAntPlusEnabledIfAvailable() {
		return antPlusEnabledIfAvailable;
	}
	public void setAntPlusEnabledIfAvailable(boolean antPlusEnabledIfAvailable) {
		this.antPlusEnabledIfAvailable = antPlusEnabledIfAvailable;
	}
	@Override
	public String toString() {
		return "OtherPrefs [confirmLevel=" + confirmLevel
			+ ", trackingOneTouchMode=" + trackingOneTouchMode
			+ ", adaptButtonsForOneTouchMode="
			+ adaptButtonsForOneTouchMode + ", antPlusEnabledIfAvailable="
			+ antPlusEnabledIfAvailable + "]";
	}
}
