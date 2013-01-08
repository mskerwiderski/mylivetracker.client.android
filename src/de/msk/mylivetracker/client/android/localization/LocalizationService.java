package de.msk.mylivetracker.client.android.localization;

import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

public class LocalizationService extends AbstractService {

	@Override
	public Class<? extends AbstractServiceThread> getServiceThreadClass() {
		return LocalizationServiceThread.class;
	}

	@Override
	public NotificationDsc getNotificationDsc() {
		return null;
	}

}
