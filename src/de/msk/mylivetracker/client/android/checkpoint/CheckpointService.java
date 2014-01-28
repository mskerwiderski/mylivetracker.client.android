package de.msk.mylivetracker.client.android.checkpoint;

import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * classname: CheckpointService
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-01-25	origin.
 * 
 */
public class CheckpointService extends AbstractService {

	@Override
	public NotificationDsc getNotificationDsc() {
		return null;
	}

	@Override
	public Class<? extends AbstractServiceThread> getServiceThreadClass() {
		return CheckpointServiceThread.class;
	}
}
