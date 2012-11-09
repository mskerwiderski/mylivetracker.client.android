package de.msk.mylivetracker.client.android.util;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.App;

import android.util.Log;


/**
 * LogUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history 
 * 000	2012-11-03 initial.
 * 
 */
public class LogUtils {
	private static final String LOG_TAG_GLOBAL = "MLT";
	
	public static void info(String logStr) {
		if (!VersionUtils.isTest(App.getCtx())) return;
		Log.i(LOG_TAG_GLOBAL, logStr);
	}

	public static void info(Class<?> clazz, String logStr) {
		if (!VersionUtils.isTest(App.getCtx())) return;
		String className = "unknown";
		if ((clazz != null) && !StringUtils.isEmpty(clazz.getSimpleName())) {
			className = clazz.getSimpleName();
		}
		String info =  className + ": " + logStr;
		Log.i(LOG_TAG_GLOBAL, info);
	}
}