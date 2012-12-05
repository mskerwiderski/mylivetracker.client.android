package de.msk.mylivetracker.client.android;

import org.apache.commons.lang.StringUtils;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * App.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 2012-11-04 initial.
 * 
 */
public abstract class App extends Application {
	private static App app = null;
	private static Context context = null;
	private static VersionDsc versionDsc = null;
	private static String appName = null;
	private static String appNameComplete = null;
	private static String versionStr = null;
	private static String dbName = null;
	
	public enum VersionStage {
		Release("R"),
		Beta("B"),
		Alpha("A");
		private String abbr;
		private VersionStage(String abbr) {
			this.abbr = abbr;
		}
		public String getAbbr() {
			return this.abbr;
		}
		public static VersionStage getVersionStage(String versionName) {
			if (StringUtils.isEmpty(versionName)) {
				throw new IllegalArgumentException("versionName must not be empty");
			}
			VersionStage res = null;
			VersionStage[] versionStages = VersionStage.values();
			for (int i=0; (res == null) && (i < versionStages.length); i++) {
				if (StringUtils.containsIgnoreCase(
					versionName, versionStages[i].abbr)) {
					res = versionStages[i];
				}
			}
			return res;
		}		
	}

	public enum VersionType {
		Production("P"),
		Integration("I"),
		Testing("T");
		private String abbr;
		private VersionType(String abbr) {
			this.abbr = abbr;
		}
		public String getAbbr() {
			return this.abbr;
		}
		public static VersionType getVersionType(String versionName) {
			if (StringUtils.isEmpty(versionName)) {
				throw new IllegalArgumentException("versionName must not be empty");
			}
			VersionType res = null;
			VersionType[] versionTypes = VersionType.values();
			for (int i=0; (res == null) && (i < versionTypes.length); i++) {
				if (StringUtils.containsIgnoreCase(
					versionName, versionTypes[i].abbr)) {
					res = versionTypes[i];
				}
			}
			return res;
		}
	}
	
	public static class VersionDsc {
		private int code;
		private String name;
		private VersionType versionType;
		private VersionStage versionStage;
		
		private VersionDsc() {
		}
		private VersionDsc(int code, String name) {
			this.code = code;
			this.name = name;
			this.versionType = VersionType.getVersionType(this.name);
			this.versionStage = VersionStage.getVersionStage(this.name);
		}
		public static int getCode() {
			VersionDsc.get();
			return versionDsc.code;
		}
		public static String getName() {
			VersionDsc.get();
			return versionDsc.name;
		}
		public static String getVersionStr() {
			VersionDsc.get();
			return versionStr; 
		}
		public static VersionDsc get() {
			if (versionDsc == null) {
				try {
					versionDsc = new VersionDsc(
						context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode,	
						context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
					versionStr = "v" + versionDsc.name;
					if (App.isPro()) {
						versionStr += " (PRO)";
					}
					appName = App.getCtx().getString(R.string.app_name);
					appNameComplete = appName + " " + versionStr;
					dbName = (App.isPro() ? appName + "PRO" : appName) + ".DB"; 
					LogUtils.always("APP: " + appNameComplete);
					LogUtils.always("DB: " + dbName);
				} catch (NameNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
			return versionDsc;
		}
		public static boolean isCurrent(VersionDsc foundVersionDsc) {
			VersionDsc.get();
			if (foundVersionDsc == null) return false;
			return (VersionDsc.getCode() == foundVersionDsc.code);
		}
		public static boolean isRelease() {
			VersionDsc.get();
			return (versionDsc.versionStage.equals(VersionStage.Release));
		}
	}
	
	public static class ConfigDsc {
		private enum ConfigParam {
			Realm,
			ServerDnsName,
			ServerPortHttp,
			ServerPortTcp,
			ServerPathHttp,
			PortalRpcUrl
		}
		
		private static final String[] CONFIG_PARAMS_PRODUCTION = new String[] {
			"SKERWIDERSKI",
			"portal.mylivetracker.de",
			"80",
			"51395",
			"upl_mlt.sec",
			"http://portal.mylivetracker.de/rpc.json",
		};
		
		private static final String[] CONFIG_PARAMS_INTEGRATION = new String[] {
			"SKERWIDERSKI",
			"80.190.245.43",
			"80",
			"63395",
			"upl_mlt.sec",
			"http://test.mylivetracker.de/rpc.json",
		};
		
		private static final String[] CONFIG_PARAMS_TEST = new String[] {
			"SKERWIDERSKI",
			"skerwiderski.homedns.org",
			"8080",
			"51395",
			"mylivetracker.server/upl_mlt.sec",
			"http://skerwiderski.homedns.org:8080/mylivetracker.server/rpc.json",
		};
		
		private static String[][] CONFIG_PARAMS_TABLE = new String[][] {
			CONFIG_PARAMS_PRODUCTION,
			CONFIG_PARAMS_INTEGRATION,
			CONFIG_PARAMS_TEST
		};
		
		private static String getValue(ConfigParam param) {
			int configParamsIdx = versionDsc.versionType.ordinal();
			int paramIdx = param.ordinal();
			String value = CONFIG_PARAMS_TABLE[configParamsIdx][paramIdx]; 
			return value;
		}
		
		public static String getRealm() {
			return getValue(ConfigParam.Realm);
		}
		public static String getServerDns() {
			return getValue(ConfigParam.ServerDnsName);
		}
		public static int getServerPortHttp() {
			return Integer.valueOf(getValue(ConfigParam.ServerPortHttp));
		}
		public static int getServerPortTcp() {
			return Integer.valueOf(getValue(ConfigParam.ServerPortTcp));
		}
		public static String getServerPathHttp() {
			return getValue(ConfigParam.ServerPathHttp);
		}
		public static String getPortalRpcUrl() {
			return getValue(ConfigParam.PortalRpcUrl);
		}	
	}
	
	@Override
    public void onCreate(){
        super.onCreate();
        app = this;
        context = getApplicationContext();
        VersionDsc.get();
    }
    @Override
	public void onTerminate() {
    	versionDsc = null;
		context = null;
		app = null;
		super.onTerminate();
	}
    public static App get() {
        return app;
    }
	public static Context getCtx() {
        return context;
    }
	public static VersionDsc getVersionDsc() {
        return versionDsc;
    }
	public static boolean isPro() {
		return app.isProAux();
	}
	public static String getAppName() {
		return appName;
	}
	public static String getAppNameComplete() {
		return appNameComplete;
	}
	public static String getDbName() {
		return dbName;
	}
	protected abstract boolean isProAux();
	
	/*
	 * other util stuff.
	 */
	
	public static String getDeviceId() {
		return ((TelephonyManager)App.get().getSystemService(
			Context.TELEPHONY_SERVICE)).getDeviceId();
	}
}
