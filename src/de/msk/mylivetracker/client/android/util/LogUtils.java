package de.msk.mylivetracker.client.android.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;
import de.msk.mylivetracker.client.android.App.VersionDsc;
import de.msk.mylivetracker.client.android.LaunchAppBroadcast;
import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.auto.AutoServiceThread;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.upload.UploadServiceThread;
import de.msk.mylivetracker.client.android.upload.protocol.sms.ProtocolEncoder;

/**
 * classname: LogUtils
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class LogUtils {
	private static final String LOG_TAG_GLOBAL = "MLT";

	private static Map<Class<?>, Boolean> classes = new HashMap<Class<?>, Boolean>();
	
	static {
//		classes.put(RemoteCmdMobNwCell.class, Boolean.TRUE);
//		classes.put(BatteryReceiver.class, Boolean.TRUE);
		classes.put(AppControl.class, Boolean.TRUE);
		classes.put(ProtocolEncoder.class, Boolean.TRUE);
//		classes.put(HttpProtocolParams.class, Boolean.TRUE);
//		classes.put(PhoneStateReceiver.class, Boolean.TRUE);
//		classes.put(PhoneStateListener.class, Boolean.TRUE);
//		classes.put(BatteryReceiver.class, Boolean.TRUE);
//		classes.put(AppControlReceiver.class, Boolean.TRUE);
		classes.put(AbstractActivity.class, Boolean.TRUE);
//		classes.put(ARemoteCmdReceiver.class, Boolean.TRUE);
//		classes.put(ARemoteMessageCmdReceiver.class, Boolean.TRUE);
//		classes.put(RemoteSmsCmdReceiver.class, Boolean.TRUE);
//		classes.put(RemoteCmdVersion.class, Boolean.TRUE);
//		classes.put(RemoteCmdConfig.class, Boolean.TRUE);
//		classes.put(RemoteCmdHelp.class, Boolean.TRUE);
//		classes.put(RemoteCmdLocalization.class, Boolean.TRUE);
//		classes.put(RemoteCmdTracking.class, Boolean.TRUE);
//		classes.put(RemoteCmdUpload.class, Boolean.TRUE);
//		classes.put(RemoteCmdHeartrate.class, Boolean.TRUE);
//		classes.put(RemoteCmdStatus.class, Boolean.TRUE);
//		classes.put(RemoteCmdApp.class, Boolean.TRUE);
//		classes.put(RemoteCmdSystem.class, Boolean.TRUE);
//		classes.put(RemoteCmdBattery.class, Boolean.TRUE);
		classes.put(LaunchAppBroadcast.class, Boolean.TRUE);
		classes.put(AutoServiceThread.class, Boolean.TRUE);
		classes.put(UploadServiceThread.class, Boolean.TRUE);
	}

	private static boolean isLogForClassEnabled(Class<?> clazz) {
		return !VersionDsc.isRelease() &&
			(classes.containsKey(clazz) ? classes.get(clazz) : false);
	}
	
	public static void always(String logStr) {
		Log.i(LOG_TAG_GLOBAL, logStr);
	}

	public static void info(String logStr) {
		if (VersionDsc.isRelease()) return;
		Log.i(LOG_TAG_GLOBAL, logStr);
	}

	public static void info(Class<?> clazz, String logStr) {
		if (!isLogForClassEnabled(clazz)) return;
		String className = "unknown";
		if ((clazz != null) && !StringUtils.isEmpty(clazz.getSimpleName())) {
			className = clazz.getSimpleName();
		}
		String info = className + ": " + logStr;
		Log.i(LOG_TAG_GLOBAL, info);
	}

	public static void infoMethodIn(Class<?> clazz, String method, Object... params) {
		if (!isLogForClassEnabled(clazz)) return;
		String paramsStr = "";
		for (Object param : params) {
			if (!StringUtils.isEmpty(paramsStr)) {
				paramsStr += ",";
			}
			String paramStr = "<null>";
			if (param != null) {
				paramStr = param.toString();
				if (StringUtils.isEmpty(paramStr)) {
					paramStr = "<empty>";
				} 
			}
			paramsStr += paramStr;
		}
		info(clazz, "-->" + method + "(" + paramsStr + ")");
	}
	
	public static void infoMethodState(Class<?> clazz, String method, String stateName, Object... states) {
		if (!isLogForClassEnabled(clazz)) return;
		String statesStr = "";
		for (Object state : states) {
			if (!StringUtils.isEmpty(statesStr)) {
				statesStr += ",";
			}
			String stateStr = "<null>";
			if (state != null) {
				stateStr = state.toString();
				if (StringUtils.isEmpty(stateStr)) {
					stateStr = "<empty>";
				} 
			}
			statesStr += stateStr;
		}
		info(clazz, method + ":" + stateName + ":" + statesStr);
	}
	
	public static void infoMethodOut(Class<?> clazz, String method, Object... results) {
		if (!isLogForClassEnabled(clazz)) return;
		String resultsStr = (results.length == 0) ? "<void>" : "";
		for (Object result : results) {
			if (!StringUtils.isEmpty(resultsStr)) {
				resultsStr += ",";
			}
			String resultStr = "<null>";
			if (result != null) {
				resultStr = result.toString();
				if (StringUtils.isEmpty(resultStr)) {
					resultStr = "<empty>";
				} 
			}
			resultsStr += resultStr;
		}
		info(clazz, "<--" + method + "()=" + resultsStr);
	}
}
