package de.msk.mylivetracker.client.android.phonestate;

import org.apache.commons.lang3.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.telephony.TelephonyManager;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.status.PhoneStateInfo;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: PhoneStateReceiver
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.7.0
 * 
 * history:
 * 000	2014-04-12	origin.
 * 
 */
public class PhoneStateReceiver extends BroadcastReceiver {
	public static final String ACTION_PHONE_STATE_CHANGED = 
		"de.msk.mylivetracker.client.android.phonestate.PHONE_STATE_CHANGED";
	public static final String ACTION_PARAM_PHONE_STATE = "PHONE_STATE";
	
	private static PhoneStateReceiver phoneStateReceiver = null;
	private static PhoneStateListener phoneStateListener = null;
	
	public static boolean isRegistered() {
		return (phoneStateReceiver != null) && (phoneStateListener != null);
	}
	
	public static void register() {
		LogUtils.infoMethodIn(PhoneStateReceiver.class, "register");
		if (!isRegistered()) {
			phoneStateReceiver = new PhoneStateReceiver();
			IntentFilter filter = new IntentFilter(ACTION_PHONE_STATE_CHANGED);
	        App.getCtx().registerReceiver(phoneStateReceiver, filter);
        	if (Looper.myLooper() == null) {
				Looper.prepare();
			}
			phoneStateListener = new PhoneStateListener();
			getTelephonyManager().listen(
				phoneStateListener, 
				PhoneStateListener.LISTEN_SERVICE_STATE |
				PhoneStateListener.LISTEN_CELL_LOCATION |
				PhoneStateListener.LISTEN_DATA_CONNECTION_STATE |
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		}
		LogUtils.infoMethodOut(PhoneStateReceiver.class, "register");
	}
	
	public static void unregister() {
		LogUtils.infoMethodIn(PhoneStateReceiver.class, "unregister");
		if (isRegistered()) {
			getTelephonyManager().listen(
				phoneStateListener, PhoneStateListener.LISTEN_NONE);
			phoneStateListener = null;
			App.getCtx().unregisterReceiver(phoneStateReceiver);
			phoneStateReceiver = null;
		}
		LogUtils.infoMethodOut(PhoneStateReceiver.class, "unregister");
	}
	
	public static void updatePhoneStateInfo() {
		//TODO does not work
		IntentFilter filter = new IntentFilter(ACTION_PHONE_STATE_CHANGED);
		Intent intent = App.getCtx().registerReceiver(null, filter);
		updatePhoneStateInfo(intent);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.infoMethodIn(PhoneStateReceiver.class, "onReceive");
		updatePhoneStateInfo(intent);
		LogUtils.infoMethodOut(PhoneStateReceiver.class, "onReceive");
	}

	private static void updatePhoneStateInfo(Intent intent) {
		if (StringUtils.equals(intent.getAction(), ACTION_PHONE_STATE_CHANGED)) {
			LogUtils.infoMethodState(PhoneStateReceiver.class, 
				"updatePhoneStateInfo", "action", intent.getAction());
			PhoneStateData phoneStateData = (PhoneStateData)
				intent.getSerializableExtra(ACTION_PARAM_PHONE_STATE);
			PhoneStateInfo.update(phoneStateData);
		} else {
			throw new IllegalArgumentException("unknown intent action: " + intent.getAction());
		}
	}
	
	private static TelephonyManager getTelephonyManager() {
		return (TelephonyManager)App.getCtx().
			getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public static boolean isPhoneTypeGsm() {
		int phoneType = getTelephonyManager().getPhoneType();
		return (phoneType == TelephonyManager.PHONE_TYPE_GSM);
	}
}
