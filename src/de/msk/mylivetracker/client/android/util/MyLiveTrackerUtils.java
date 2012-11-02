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
	private static final String REALM = "SKERWIDERSKI";
	
	// production.
	private static final String SERVER_DNS = "portal.mylivetracker.de";
	private static final int SERVER_PORT_HTTP = 80;
	private static final int SERVER_PORT_TCP = 51395;
	private static final String SERVER_PATH_HTTP = "upl_mlt.sec";
	private static final String PORTAL_URL = "http://" + SERVER_DNS + ":" + SERVER_PORT_HTTP + "/";
	private static final String PORTAL_RPC_URL = PORTAL_URL + "rpc.json";
	private static final String PORTAL_STATUS_URL_PREFIX = PORTAL_URL + "track_as_map.sec?pid=";
	
	// test
//	private static final String SERVER_DNS = "skerwiderski.homedns.org";
//	private static final int SERVER_PORT_HTTP = 8080;
//	private static final int SERVER_PORT_TCP = 51395;
//	private static final String SERVER_PATH_HTTP = "mylivetracker.server/upl_mlt.sec";
//	private static final String PORTAL_URL = "http://" + SERVER_DNS + ":" + SERVER_PORT_HTTP + "/";
//	private static final String PORTAL_RPC_URL = PORTAL_URL + "mylivetracker.server/rpc.json";
//	private static final String PORTAL_STATUS_URL_PREFIX = PORTAL_URL + "mylivetracker.server/track_as_map.sec?pid=";
	
	public static String getRealm() {
		return REALM;
	}
	public static String getServerDns() {
		return SERVER_DNS;
	}
	public static int getServerPortHttp() {
		return SERVER_PORT_HTTP;
	}
	public static int getServerPortTcp() {
		return SERVER_PORT_TCP;
	}
	public static String getServerPathHttp() {
		return SERVER_PATH_HTTP;
	}
	public static String getPortalRpcUrl() {
		return PORTAL_RPC_URL;
	}	
	public static String getPortalStatusUrl(String statusParamsId) {
		if (StringUtils.isEmpty(statusParamsId)) return null;
		return PORTAL_STATUS_URL_PREFIX + statusParamsId;
	}
}
