package de.msk.mylivetracker.client.android.localization;

import java.io.Serializable;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;
import de.msk.mylivetracker.client.android.util.LocationManagerUtils;

/**
 * classname: LocalizationPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class LocalizationPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = 1329293448673994415L;

	public static final int VERSION = 1;
	
	public enum LocalizationMode {
		gps("GPS", true, false),
		network("Network", false, true),
		gpsAndNetwork("GPS and Network", true, true),
		none("None", false, false);
		
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
				res = res && LocationManagerUtils.gpsProviderSupported();
			}
			if (this.networkProviderEnabled) {
				res = res && LocationManagerUtils.networkProviderSupported();
			}
			return res;
		}
		public boolean neededProvidersEnabled() {
			boolean res = true;
			if (this.gpsProviderEnabled) {
				res = res && LocationManagerUtils.gpsProviderEnabled();
			}
			if (this.networkProviderEnabled) {
				res = res && LocationManagerUtils.networkProviderEnabled();
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
	
	private LocalizationMode localizationMode;
	private int timeTriggerInSeconds;
	private int distanceTriggerInMeter;	
	private int accuracyRequiredInMeter;
	private int distBtwTwoLocsForDistCalcRequiredInCMtr;
	private long maxWaitingPeriodForGpsFixInMSecs;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	
	@Override
	public boolean checkIfValid() {
		return this.localizationMode.supported();
	}

	@Override
	public void initWithDefaults() {
		boolean gpsProviderSupported = LocationManagerUtils.gpsProviderSupported();
		boolean networkProviderSupported = LocationManagerUtils.networkProviderSupported();
		if (gpsProviderSupported && networkProviderSupported) {
			this.localizationMode = LocalizationMode.gpsAndNetwork;
		} else if (gpsProviderSupported) {
			this.localizationMode = LocalizationMode.gps;
		} else if (networkProviderSupported) {
			this.localizationMode = LocalizationMode.network;
		} else {
			this.localizationMode = LocalizationMode.none;
		}
		this.timeTriggerInSeconds = 0;
		this.distanceTriggerInMeter = 0;
		this.accuracyRequiredInMeter = 150;
		this.distBtwTwoLocsForDistCalcRequiredInCMtr = 1650;
		this.maxWaitingPeriodForGpsFixInMSecs = 180000;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}

	public LocalizationMode getLocalizationMode() {
		return localizationMode;
	}
	public void setLocalizationMode(LocalizationMode localizationMode) {
		this.localizationMode = localizationMode;
	}
	public int getTimeTriggerInSeconds() {
		return timeTriggerInSeconds;
	}
	public void setTimeTriggerInSeconds(int timeTriggerInSeconds) {
		this.timeTriggerInSeconds = timeTriggerInSeconds;
	}
	public int getDistanceTriggerInMeter() {
		return distanceTriggerInMeter;
	}
	public void setDistanceTriggerInMeter(int distanceTriggerInMeter) {
		this.distanceTriggerInMeter = distanceTriggerInMeter;
	}
	public int getAccuracyRequiredInMeter() {
		return accuracyRequiredInMeter;
	}
	public void setAccuracyRequiredInMeter(int accuracyRequiredInMeter) {
		this.accuracyRequiredInMeter = accuracyRequiredInMeter;
	}
	public int getDistBtwTwoLocsForDistCalcRequiredInCMtr() {
		return distBtwTwoLocsForDistCalcRequiredInCMtr;
	}
	public void setDistBtwTwoLocsForDistCalcRequiredInCMtr(
			int distBtwTwoLocsForDistCalcRequiredInCMtr) {
		this.distBtwTwoLocsForDistCalcRequiredInCMtr = distBtwTwoLocsForDistCalcRequiredInCMtr;
	}
	public long getMaxWaitingPeriodForGpsFixInMSecs() {
		return maxWaitingPeriodForGpsFixInMSecs;
	}
	public void setMaxWaitingPeriodForGpsFixInMSecs(
		long maxWaitingPeriodForGpsFixInMSecs) {
		this.maxWaitingPeriodForGpsFixInMSecs = maxWaitingPeriodForGpsFixInMSecs;
	}
	@Override
	public String getShortName() {
		return "localization";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("LocalizationPrefs", 
			new ConfigPair[] {
				new ConfigPair("localizationMode", this.localizationMode.name()),
				new ConfigPair("timeTriggerInSeconds", String.valueOf(this.timeTriggerInSeconds)),
				new ConfigPair("distanceTriggerInMeter", String.valueOf(this.distanceTriggerInMeter)),
				new ConfigPair("accuracyRequiredInMeter", String.valueOf(this.accuracyRequiredInMeter)),
				new ConfigPair("distBtwTwoLocsForDistCalcRequiredInCMtr", 
					String.valueOf(this.distBtwTwoLocsForDistCalcRequiredInCMtr)),
				new ConfigPair("maxWaitingPeriodForGpsFixInMSecs", 
					String.valueOf(this.maxWaitingPeriodForGpsFixInMSecs)),
		});
	}
	@Override
	public String toString() {
		return "LocalizationPrefs [localizationMode=" + localizationMode
			+ ", timeTriggerInSeconds=" + timeTriggerInSeconds
			+ ", distanceTriggerInMeter=" + distanceTriggerInMeter
			+ ", accuracyRequiredInMeter=" + accuracyRequiredInMeter
			+ ", distBtwTwoLocsForDistCalcRequiredInCMtr="
			+ distBtwTwoLocsForDistCalcRequiredInCMtr
			+ ", maxWaitingPeriodForGpsFixInMSecs="
			+ maxWaitingPeriodForGpsFixInMSecs + "]";
	}
}
