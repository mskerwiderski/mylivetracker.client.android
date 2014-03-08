package de.msk.mylivetracker.client.android.preferences;

import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.App;
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

	public static final String PASSWORD_MASK = "********";
	
	private static String getSimpleLine(int cnt) {
		return StringUtils.repeat("-", cnt);
	}
	
	private static String getDoubleLine(int cnt) {
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
			"Dump created=" + (new DateTime()).getAsStr(
				TimeZone.getTimeZone(DateTime.TIME_ZONE_UTC), 
				"'UTC'yyyy-MM-dd'T'HH-mm-ss-SSS'Z'") + LINE_SEP +
			"AppNameComplete=" + App.getAppNameComplete() + LINE_SEP +
			"DeviceId=" + App.getDeviceId() + LINE_SEP +
			"DeviceModel=" + App.getDeviceModel() + LINE_SEP +
			"DeviceLanguage=" + App.getLocale().getDisplayLanguage() + LINE_SEP +
			getDoubleLine(80) + LINE_SEP + LINE_SEP;
		for (PrefsDsc prefsDsc : PrefsRegistry.prefsDscArr) {
			if (prefsDsc.id > 0) {
				dump += getPrefsDumpAsStr(prefsDsc.id, prefsDsc.version,
					PrefsRegistry.get(prefsDsc.prefsClass).getPrefsDump(), false) + 
					LINE_SEP;
			}
		}
		return dump;
	}
	
	public static String getPrefsDumpAsStr(int id, int version, PrefsDump prefsDump, boolean oneLine) {
		if (prefsDump == null) {
			throw new IllegalArgumentException("prefsDump must not be empty!");
		}
		String res = id + ": " + prefsDump.name +
			" (version " + version + ")";
		
		if (oneLine) {
			res += ":";
			if (prefsDump.configPairs == null) {
				res += EMPTY;
			} else {
				for (ConfigPair configPair : prefsDump.configPairs) {
					res += configPair.param+ "=" + configPair.value + ", ";
				}
				res = StringUtils.chop(res);
			}
		} else {
			res += LINE_SEP + getSimpleLine(res.length()) + LINE_SEP;
			if (prefsDump.configPairs == null) {
				res += EMPTY + LINE_SEP;
			} else {
				int idx = 97;
				for (ConfigPair configPair : prefsDump.configPairs) {
					res += (char)idx + ": " + configPair.param + " = " + configPair.value + LINE_SEP;
					idx++;
				}
			}
		}
		return res;
	}
}
