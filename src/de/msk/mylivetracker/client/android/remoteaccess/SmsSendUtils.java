package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import de.msk.mylivetracker.client.android.app.AbstractApp;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * SmsSendUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 000 2012-11-27 initial.
 * 
 */
public class SmsSendUtils {

	public static final String SEND_SMS_STATUS_SMS_SENT = "de.msk.mylivetracker.intent.action.SMS_SENT";
	public static final String SEND_SMS_STATUS_SMS_DELIVERED = "de.msk.mylivetracker.intent.action.SMS_DELIVERED";

	private static final int MAX_SMS_MESSAGE_LENGTH = 160;
	private static final int MAX_SMS_MESSAGE_COUNT = 3;

	public static void sendSms(String phoneNumber, String message) {
		sendSms(phoneNumber, message, MAX_SMS_MESSAGE_LENGTH, MAX_SMS_MESSAGE_COUNT);
	}
	
	private static void sendSms(String phoneNumber, String message, int maxLength, int maxCount) {
		if (StringUtils.isEmpty(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber must not be null.");
		}
		if (StringUtils.isEmpty(message)) {
			throw new IllegalArgumentException("message must not be null.");
		}
		if (!PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber '" + phoneNumber + "' is invalid.");
		}
		if (maxLength <= 0) {
			throw new IllegalArgumentException("maxLength must be greater than 0.");
		}
		if (maxCount <= 0) {
			throw new IllegalArgumentException("maxCount must be greater than 0.");
		}
		LogUtils.infoMethodIn(SmsSendUtils.class, "sendSms", phoneNumber, message);
		SmsManager manager = SmsManager.getDefault();

		PendingIntent piSend = PendingIntent.getBroadcast(AbstractApp.getCtx(), 0,
				new Intent(SEND_SMS_STATUS_SMS_SENT), 0);
		PendingIntent piDelivered = PendingIntent.getBroadcast(AbstractApp.getCtx(), 0,
				new Intent(SEND_SMS_STATUS_SMS_DELIVERED), 0);

		int length = message.length();
		LogUtils.infoMethodState(SmsSendUtils.class, "sendSms", "length of message", new Integer(length));
		
		if (length > maxLength) {
			ArrayList<String> messages = manager.divideMessage(message);
			// TODO hack!
			while (messages.size() > maxCount) {
				messages.remove(messages.size()-1);
			}
			manager.sendMultipartTextMessage(
				phoneNumber, null, messages,
				null, null);
		} else {
			manager.sendTextMessage(
				phoneNumber, null, message, piSend,
				piDelivered);
		}
		LogUtils.infoMethodOut(SmsSendUtils.class, "sendSms");
	}
}
