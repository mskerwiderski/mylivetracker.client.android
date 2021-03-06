package de.msk.mylivetracker.client.android.other;

import java.io.Serializable;

import org.apache.commons.lang3.BooleanUtils;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: OtherPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 001	2012-03-12	option 'autoStartApp' added.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OtherPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 7732687590338383772L;

	public static final int VERSION = 2;
	
	public enum ConfirmLevel {
		low("low"), 
		medium("medium"), 
		high("high"), 
		max("maximum");
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
		public boolean isMax() {
			return this.ordinal() >= (max.ordinal());
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
	private boolean useGoogleUrlShortener;
	
	@Deprecated
	private boolean autoStartApp;
	
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
		this.useGoogleUrlShortener = true;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public static boolean useGoogleUrlShortener() {
		return PrefsRegistry.get(OtherPrefs.class).isUseGoogleUrlShortener();
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
	public boolean isUseGoogleUrlShortener() {
		return useGoogleUrlShortener;
	}
	public void setUseGoogleUrlShortener(boolean useGoogleUrlShortener) {
		this.useGoogleUrlShortener = useGoogleUrlShortener;
	}
	@Deprecated
	public boolean isAutoStartApp() {
		return autoStartApp;
	}
	@Deprecated
	public void setAutoStartApp(boolean autoStartApp) {
		this.autoStartApp = autoStartApp;
	}
	@Override
	public String getShortName() {
		return "other";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("OtherPrefs", 
			new ConfigPair[] {
				new ConfigPair("confirmLevel", this.confirmLevel.name()),
				new ConfigPair("trackingOneTouchMode", this.trackingOneTouchMode.name()),
				new ConfigPair("adaptButtonsForOneTouchMode", 
					BooleanUtils.toStringYesNo(this.adaptButtonsForOneTouchMode)),
				new ConfigPair("antPlusEnabledIfAvailable", 
					BooleanUtils.toStringYesNo(this.antPlusEnabledIfAvailable)),
				new ConfigPair("useGoogleUrlShortener", 
					BooleanUtils.toStringYesNo(this.useGoogleUrlShortener)),	
		});
	}
	@Override
	public String toString() {
		return "OtherPrefs [confirmLevel=" + confirmLevel
			+ ", trackingOneTouchMode=" + trackingOneTouchMode
			+ ", adaptButtonsForOneTouchMode="
			+ adaptButtonsForOneTouchMode + ", antPlusEnabledIfAvailable="
			+ antPlusEnabledIfAvailable + ", useGoogleUrlShortener="
			+ useGoogleUrlShortener + "]";
	}
}
