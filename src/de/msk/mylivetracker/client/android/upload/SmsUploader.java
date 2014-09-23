package de.msk.mylivetracker.client.android.upload;

import org.apache.commons.lang3.StringUtils;

import android.os.SystemClock;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.server.ServerPrefs;
import de.msk.mylivetracker.client.android.upload.protocol.IProtocol;
import de.msk.mylivetracker.client.android.util.sms.SmsSendUtils;

/**
 * classname: SmsUploader
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.7.0
 * 
 * history:
 * 000	2014-09-21	origin.
 * 
 */
public class SmsUploader extends AbstractUploader {

	public SmsUploader(IProtocol protocol) {
		super(protocol);
	}

	@Override
	public void finish() {
		// noop.
	}

	@Override
	public UploadResult upload(String dataStr) {
		long start = SystemClock.elapsedRealtime();
		ServerPrefs serverPrefs = 
			PrefsRegistry.get(ServerPrefs.class);
		String[] receivers = StringUtils.split(
			serverPrefs.getSmsReceivers(), ",");
		for (String receiver : receivers) {
			SmsSendUtils.sendSms(receiver, dataStr);
		}
		long stop = SystemClock.elapsedRealtime();
		return new UploadResult(true, stop-start, 1, 
			App.getCtx().getString(R.string.txMain_UploadResultOk));
	}
}
