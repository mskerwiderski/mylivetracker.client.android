package de.msk.mylivetracker.client.android.remoteaccess;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.sms.SmsSendUtils;

/**
 * classname: RemoteSmsCmdReceiver
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-15	origin.
 * 
 */
public class RemoteSmsCmdReceiver extends ARemoteMessageCmdReceiver {

	/*
	 * sms message format: #mlt <password> <command>
	 */
	public static final String CMD_INDICATOR = "#mlt";

	@Override
	public void onReceive(Context context, Intent intent) {
		RemoteAccessPrefs prefs = PrefsRegistry.get(RemoteAccessPrefs.class);
		if (!prefs.isRemoteAccessEnabled()) return;
		LogUtils.infoMethodIn(this.getClass(), "onReceive");
		Bundle bundle = intent.getExtras();
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
			String sender = smsMessage[0].getOriginatingAddress();
			if (prefs.isRemoteAccessUseReceiver()) {
				sender = prefs.getRemoteAccessReceiver();
			}
			String message = StringUtils.lowerCase(smsMessage[0].getMessageBody());
			
			if (!StringUtils.isEmpty(sender) && PhoneNumberUtils.isGlobalPhoneNumber(sender) && 
				!StringUtils.isEmpty(message)) {
				super.onProcessRemoteMessageCmd(sender, message);
			}
		}
		LogUtils.infoMethodOut(this.getClass(), "onReceive");
	}

	public static class SmsResponseSender implements IResponseSender {
		@Override
		public void sendResponse(String receiver, String response) {
			if (StringUtils.isEmpty(receiver)) {
				throw new IllegalArgumentException("receiver must not be null.");
			}
			if (StringUtils.isEmpty(response)) {
				throw new IllegalArgumentException("response must not be null.");
			}
			SmsSendUtils.sendSms(receiver, response);		
		}
	}

	@Override
	public IResponseSender getResponseSender() {
		return new SmsResponseSender();
	}
}
