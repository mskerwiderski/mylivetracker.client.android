package de.msk.mylivetracker.client.android.upload;

import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

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
public class UploadService extends AbstractService {
	
	
	@Override
	public NotificationDsc getNotificationDsc() {
		return new NotificationDsc(
			100, 
			R.drawable.icon_notification_red,
			R.string.app_name,
			R.string.nfTrackRunning);
	}

	@Override
	public Class<? extends AbstractServiceThread> getServiceThreadClass() {
		return UploadServiceThread.class;
	}
}
