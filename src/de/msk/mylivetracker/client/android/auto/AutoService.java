package de.msk.mylivetracker.client.android.auto;

import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * AutoService.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-24 	revised for v1.5.x.
 * 000 	2012-11-10 	initial.
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
