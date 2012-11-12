package de.msk.mylivetracker.client.android.util;

import org.apache.commons.lang.StringUtils;

/**
 * MyLiveTrackerUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-11-02 initial.
 * 
 */
public class MyLiveTrackerUtils {
	
	private enum Param {
		Realm,
		ServerDnsName,
		ServerPortHttp,
		ServerPortTcp,
		ServerPathHttp,
		PortalRpcUrl,
		PortalStatusUrlPrefix
	}
	
	private static final String[] PARAMS_PRODUCTION = new String[] {
		"SKERWIDERSKI",
		"portal.mylivetracker.de",
		"80",
		"51395",
		"upl_mlt.sec",
		"http://portal.mylivetracker.de/rpc.json",
		"http://portal.mylivetracker.de/track_as_map.sec?pid="
	};
	
	private static final String[] PARAMS_LOCALE = new String[] {
		"SKERWIDERSKI",
		"skerwiderski.homedns.org",
		"8080",
		"51395",
		"mylivetracker.server/upl_mlt.sec",
		"http://skerwiderski.homedns.org:8080/mylivetracker.server/rpc.json",
		"http://skerwiderski.homedns.org:8080/mylivetracker.server/track_as_map.sec?pid="
	};
	
	private static String getValue(Param param) {
		return VersionUtils.get().isTest() ? 
			PARAMS_LOCALE[param.ordinal()] : 
			PARAMS_PRODUCTION[param.ordinal()];
	}
	public static String getRealm() {
		return getValue(Param.Realm);
	}
	public static String getServerDns() {
		return getValue(Param.ServerDnsName);
	}
	public static int getServerPortHttp() {
		return Integer.valueOf(getValue(Param.ServerPortHttp));
	}
	public static int getServerPortTcp() {
		return Integer.valueOf(getValue(Param.ServerPortTcp));
	}
	public static String getServerPathHttp() {
		return getValue(Param.ServerPathHttp);
	}
	public static String getPortalRpcUrl() {
		return getValue(Param.PortalRpcUrl);
	}	
	public static String getPortalStatusUrl(String statusParamsId) {
		if (StringUtils.isEmpty(statusParamsId)) return null;
		return getValue(Param.PortalStatusUrlPrefix) + statusParamsId;
	}
}
