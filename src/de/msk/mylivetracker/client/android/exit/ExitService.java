package de.msk.mylivetracker.client.android.exit;

import de.msk.mylivetracker.client.android.util.service.AbstractService;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * classname: ExitService
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-20	origin.
 * 
 */
public class ExitService extends AbstractService {

	private static ExitMarker exitMarker = null;
	
	protected static class ExitMarker {
		private long timeoutMSecs = 0;
		public ExitMarker(long timeoutMSecs) {
			this.timeoutMSecs = timeoutMSecs;
		}
		public long getTimeoutMSecs() {
			return timeoutMSecs;
		}
	}
	
	public static void markAsExit(long timeoutMSecs) {
		exitMarker = new ExitMarker(timeoutMSecs);
	}
	
	protected static boolean isMarkedAsExit() {
		return exitMarker != null;
	}
	
	protected static ExitMarker getExitMarker() {
		return exitMarker;
	}

	protected static void resetExitMarker() {
		exitMarker = null;
	}
	
	@Override
	public NotificationDsc getNotificationDsc() {
		return null;
	}

	@Override
	public Class<? extends AbstractServiceThread> getServiceThreadClass() {
		return ExitServiceThread.class;
	}
}
