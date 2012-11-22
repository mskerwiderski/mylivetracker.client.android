package de.msk.mylivetracker.client.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * SmsReceiver.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 000 	2012-11-22 initial.
 * 
 */
public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
		smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}

		// show first message
		Toast toast = Toast.makeText(context,
		"Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
		toast.show();		
	}

}
