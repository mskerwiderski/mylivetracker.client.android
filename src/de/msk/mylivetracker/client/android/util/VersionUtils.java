package de.msk.mylivetracker.client.android.util;

import org.apache.commons.lang.StringUtils;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * VersionUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history 
 * 000	2012-11-03 initial.
 * 
 */
public class VersionUtils {

	public static class VersionDsc {
		private int code;
		private String name;
		boolean test;
		public VersionDsc() {
		}
		public VersionDsc(int code, String name) {
			this.code = code;
			this.name = name;
			this.test = 
				StringUtils.contains(this.name, "test") ||
				StringUtils.contains(this.name, "beta");
		}
		public int getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		public boolean isTest() {
			return test;
		}
		@Override
		public String toString() {
			return "v" + this.name;
		}		
	}
	
	private static VersionDsc versionDsc = null;
	
	public static boolean isTest(Context context) {
		VersionDsc versionDsc = get(context);
		return versionDsc.isTest();
	}
	
	public static boolean isCurrent(Context context, VersionDsc versionDsc) {
		if (versionDsc == null) return false;
		return (get(context).getCode() == versionDsc.getCode());
	}
	
	public static VersionDsc get(Context context) {
		if (versionDsc != null) return versionDsc;
		try {
			versionDsc = new VersionDsc(
				context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode,	
				context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
		return versionDsc;
	}	
}
