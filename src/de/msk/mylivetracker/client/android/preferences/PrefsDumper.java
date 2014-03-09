package de.msk.mylivetracker.client.android.preferences;

import java.util.TimeZone;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry.PrefsDsc;
import de.msk.mylivetracker.commons.util.datetime.DateTime;

/**
 * classname: PrefsDumper
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-08	origin.
 * 
 */
public class PrefsDumper {
	public static final String LINE_SEP = "\n";
	public static final String EMPTY = "<empty>";
	public static final String NOT_SET = "<not set>";
	public static final String QUOTE = "\"";
	public static final String PASSWORD_MASK = "********";
	
	protected static String getSimpleLine(int cnt) {
		return StringUtils.repeat("-", cnt);
	}
	
	protected static String getDoubleLine(int cnt) {
		return StringUtils.repeat("=", cnt);
	}

	public static class PrefsDump {
		protected String name;
		protected ConfigPair[] configPairs;
		public PrefsDump(String name,
			ConfigPair[] configPairs) {
			this.name = name;
			this.configPairs = configPairs;
		}
	}
	
	public static class ConfigPair {
		protected  String param;
		protected  String value;
		public ConfigPair(String param, String value) {
			this.param = param;
			this.value = value;
		}
	}
	
	public static String getDump() {
		String dump = 
			getDoubleLine(80) + LINE_SEP +
			"Dump created = " + QUOTE + (new DateTime()).getAsStr(
				TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
				"'UTC'yyyy-MM-dd'T'HH-mm-ss-SSS'Z'") + QUOTE + LINE_SEP +
			"PrefsVersion = " + QUOTE + MainPrefs.VERSION + QUOTE + LINE_SEP +
			"AppNameComplete = " + QUOTE + App.getAppNameComplete() + QUOTE + LINE_SEP +
			"DeviceId = " + QUOTE + App.getDeviceId() + QUOTE + LINE_SEP +
			"DeviceModel = " + QUOTE + App.getDeviceModel() + QUOTE + LINE_SEP +
			"DeviceLanguage = " + QUOTE + App.getLocale().getDisplayLanguage() + QUOTE + LINE_SEP +
			"smsSupported = " + QUOTE + 
				BooleanUtils.toStringTrueFalse(App.smsSupported()) + QUOTE + LINE_SEP +
			"AntPlusHardwareInitialized = " + QUOTE + 
				BooleanUtils.toStringTrueFalse(AntPlusHardware.initialized()) + QUOTE + LINE_SEP +
			"AndroidVersion = " + QUOTE + App.getAndroidVersion() + QUOTE + LINE_SEP +
			getDoubleLine(80) + LINE_SEP + LINE_SEP;
		for (PrefsDsc prefsDsc : PrefsRegistry.prefsDscArr) {
			if (prefsDsc.id > 0) {
				dump += 
					PrefsRegistry.get(prefsDsc.prefsClass).getPrefsDumpAsStr(false) + 
					LINE_SEP;
			}
		}
		return dump;
	}
}
