package de.msk.mylivetracker.client.android.util;

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
		public VersionDsc() {
		}
		public VersionDsc(int code, String name) {
			this.code = code;
			this.name = name;
		}
		public int getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		@Override
		public String toString() {
			return "v" + this.name;
		}		
	}
	
	public static boolean isCurrent(Context context, VersionDsc versionDsc) {
		if (versionDsc == null) return false;
		return (get(context).getCode() == versionDsc.getCode());
	}
	
	public static VersionDsc get(Context context) {
		VersionDsc versionDsc = new VersionDsc(1, "INVALID");
		try {
			versionDsc = new VersionDsc(
				context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode,	
				context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			LogUtils.info("version info not found: " + e.getMessage());
		}
		return versionDsc;
	}	
}
