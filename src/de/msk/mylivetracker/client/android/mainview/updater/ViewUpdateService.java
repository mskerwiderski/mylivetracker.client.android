package de.msk.mylivetracker.client.android.mainview.updater;

import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

public class ViewUpdateService extends AbstractService {
	@Override
	public Class<? extends AbstractServiceThread> getServiceThreadClass() {
		return ViewUpdateServiceThread.class;
	}
	@Override
	public NotificationDsc getNotificationDsc() {
		return null;
	}
}
