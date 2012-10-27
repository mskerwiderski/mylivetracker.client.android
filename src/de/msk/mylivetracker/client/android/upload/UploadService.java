package de.msk.mylivetracker.client.android.upload;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * UploadService.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 initial 2012-10-27
 * 
 */
public class UploadService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		//Toast.makeText(this, "UploadService created", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		UploadManager.stopUploadManager();
		//Toast.makeText(this, "UploadService destroyed", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		UploadManager.startUploadManager();
		//Toast.makeText(this, "UploadService started", Toast.LENGTH_LONG).show();
	}
}
