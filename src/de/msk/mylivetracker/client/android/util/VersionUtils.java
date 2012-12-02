package de.msk.mylivetracker.client.android.util;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.app.AbstractApp;

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
		boolean alpha;
		boolean beta;
		boolean test;
		boolean local;
		public VersionDsc() {
		}
		public VersionDsc(int code, String name) {
			this.code = code;
			this.name = name;
			this.alpha = StringUtils.containsIgnoreCase(this.name, "alpha"); 
			this.beta = StringUtils.containsIgnoreCase(this.name, "beta");
			this.test = StringUtils.containsIgnoreCase(this.name, "test");
			this.local = StringUtils.containsIgnoreCase(this.name, "local");
			LogUtils.always(this.toString());
		}
		public int getCode() {
			return code;
		}
		public String getName() {
			return name;
		}
		public boolean isAlpha() {
			return alpha;
		}
		public boolean isBeta() {
			return beta;
		}
		public boolean isTest() {
			return test;
		}
		public boolean isLocal() {
			return local;
		}
		public String getVersionStr() {
			String versionStr = "v" + this.name; 
			if (AbstractApp.isPro()) {
				versionStr += " (PRO)";
			}
			return versionStr; 
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("VersionDsc [code=").append(code).append(", name=")
				.append(name).append(", alpha=").append(alpha)
				.append(", beta=").append(beta).append(", test=")
				.append(test).append(", local=").append(local).append("]");
			return builder.toString();
		}
	}
	
	private static VersionDsc versionDsc = null;
	
	public static boolean isAlpha() {
		VersionDsc versionDsc = get();
		return versionDsc.isAlpha();
	}
	
	public static boolean isBeta() {
		VersionDsc versionDsc = get();
		return versionDsc.isBeta();
	}
	
	public static boolean isTest() {
		VersionDsc versionDsc = get();
		return versionDsc.isTest();
	}
	
	public static boolean isLocal() {
		VersionDsc versionDsc = get();
		return versionDsc.isLocal();
	}
	
	public static boolean isCurrent(Context context, VersionDsc versionDsc) {
		if (versionDsc == null) return false;
		return (get().getCode() == versionDsc.getCode());
	}
	
	public static VersionDsc get() {
		if (versionDsc != null) return versionDsc;
		try {
			Context context = AbstractApp.getCtx();
			versionDsc = new VersionDsc(
				context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode,	
				context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
		return versionDsc;
	}	
}
