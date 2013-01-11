package de.msk.mylivetracker.client.android.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import de.msk.mylivetracker.client.android.App;

/**
 * classname: ConnectivityUtils
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-10	revised for v1.5.x.
 * 
 */
public class ConnectivityUtils {
	
	public static boolean isDataConnectionActive() {
		boolean res = false;
		ConnectivityManager connectivityManager = getConnectivityManager();
		if (connectivityManager != null) {
			NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
			if ((nwInfo != null) && nwInfo.isConnected()) {
				res = true; 
			}
		}
		return res;
	}
	
	public static boolean isDataConnectionAvailable() {
		boolean res = false;
		ConnectivityManager connectivityManager = getConnectivityManager();
		if (connectivityManager != null) {
			NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
			if ((nwInfo != null) && nwInfo.isAvailable()) {
				res = true;
			}
		}
		return res;
	}
	
	public static boolean isWifiEnabled() {
		WifiManager wifiManager = getWifiManager();
		return (wifiManager != null) && 
			getWifiManager().isWifiEnabled();
	}
	
	public static WifiManager getWifiManager() {
		return (WifiManager)
			App.getCtx().getSystemService(
				Context.WIFI_SERVICE);
	}
	
	public static ConnectivityManager getConnectivityManager() {
		return (ConnectivityManager)
			App.getCtx().getSystemService(
				Context.CONNECTIVITY_SERVICE);
	}
}
