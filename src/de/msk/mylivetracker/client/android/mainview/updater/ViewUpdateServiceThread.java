package de.msk.mylivetracker.client.android.mainview.updater;

import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.MainDetailsActivity;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

public class ViewUpdateServiceThread extends AbstractServiceThread {

	@Override
	public void init() throws InterruptedException {
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		if (MainDetailsActivity.get() != null) {
			MainDetailsActivity.get().updateView();
		} else {
			MainActivity.get().updateView();
		}
	}

	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		return 200;
	}

	@Override
	public void cleanUp() {
	}
}
