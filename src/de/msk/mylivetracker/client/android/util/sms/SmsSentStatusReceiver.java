package de.msk.mylivetracker.client.android.util.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: SmsSentStatusReceiver
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class SmsSentStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.infoMethodIn(this.getClass(), "onReceive", intent.getAction());
		LogUtils.infoMethodOut(this.getClass(), "onReceive");
	}
}
