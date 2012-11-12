package de.msk.mylivetracker.client.android.automode;

import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * AutoService.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 initial 2012-11-10
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
