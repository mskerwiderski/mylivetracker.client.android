package de.msk.mylivetracker.client.android.auto;

import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * classname: AutoService
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class AutoService extends AbstractService {

	@Override
	public NotificationDsc getNotificationDsc() {
		return null;
	}

	@Override
	public Class<? extends AbstractServiceThread> getServiceThreadClass() {
		return AutoServiceThread.class;
	}
}
