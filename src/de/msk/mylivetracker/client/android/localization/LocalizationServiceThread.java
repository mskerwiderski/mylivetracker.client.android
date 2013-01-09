package de.msk.mylivetracker.client.android.localization;

import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * classname: LocalizationServiceThread
 * 
 * @author michael skerwiderski, (c)2013
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-08	revised for v1.5.x.
 * 
 */
public class LocalizationServiceThread extends AbstractServiceThread {

	@Override
	public void init() throws InterruptedException {
	}
	
	@Override
	public void runSinglePass() throws InterruptedException {
		if (PrefsRegistry.get(LocalizationPrefs.class).
			getMaxWaitingPeriodForGpsFixInMSecs() > 0) {
			// check gps fix.
			this.sendMessage(0);
		}
	}

	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		return 500;
	}

	@Override
	public void cleanUp() {
		
	}
}
