package de.msk.mylivetracker.client.android.upload;

import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * classname: UploadService
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class UploadService extends AbstractService {
	
	
	@Override
	public NotificationDsc getNotificationDsc() {
		return new NotificationDsc(
			100, 
			R.drawable.icon_notification,
			R.string.app_name,
			R.string.nfTrackRunning);
	}

	@Override
	public Class<? extends AbstractServiceThread> getServiceThreadClass() {
		return UploadServiceThread.class;
	}
}
