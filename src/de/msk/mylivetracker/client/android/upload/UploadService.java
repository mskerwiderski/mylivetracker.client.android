package de.msk.mylivetracker.client.android.upload;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.ServiceUtils.ServiceName;

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
	public void startServiceThread() {
		UploadServiceThread.startUploadManager();
	}
	@Override
	public void stopServiceThread() {
		UploadServiceThread.stopUploadManager();
	}
	@Override
	public NotificationDsc getNotificationDsc() {
		return new NotificationDsc(
			ServiceName.UploadService.getId(), 
			R.drawable.icon_notification_red,
			R.string.app_name,
			R.string.nfTrackRunning);
	}
}
