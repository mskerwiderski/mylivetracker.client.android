package de.msk.mylivetracker.client.android.localization;

import java.io.Serializable;

import android.content.Context;
import android.location.LocationManager;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.preferences.APrefs;
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
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		LocationManager locationManager = (LocationManager)
			App.get().getSystemService(Context.LOCATION_SERVICE);
		if ((locationManager == null) || 
			(locationManager.getProvider(LocationManager.GPS_PROVIDER) == null)) {
			this.localizationMode = LocalizationMode.network;
		} else {
			this.localizationMode = LocalizationMode.gpsAndNetwork;
		}
		this.timeTriggerInSeconds = 0;
		this.distanceTriggerInMeter = 0;
		this.accuracyRequiredInMeter = 150;
		this.distBtwTwoLocsForDistCalcRequiredInCMtr = 1650;
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

	@Override
	public String toString() {
		return "LocalizationPrefs [localizationMode=" + localizationMode
			+ ", timeTriggerInSeconds=" + timeTriggerInSeconds
			+ ", distanceTriggerInMeter=" + distanceTriggerInMeter
			+ ", accuracyRequiredInMeter=" + accuracyRequiredInMeter
			+ ", distBtwTwoLocsForDistCalcRequiredInCMtr="
			+ distBtwTwoLocsForDistCalcRequiredInCMtr + "]";
	}
}
