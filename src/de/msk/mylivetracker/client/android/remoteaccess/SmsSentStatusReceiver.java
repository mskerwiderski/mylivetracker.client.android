package de.msk.mylivetracker.client.android.remoteaccess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * SmsSentStatusReceiver.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 000 2012-11-23 initial.
 * 
 */
public class SmsSentStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.infoMethodIn(this.getClass(), "onReceive", intent.getAction());
		LogUtils.infoMethodOut(this.getClass(), "onReceive");
	}
}
