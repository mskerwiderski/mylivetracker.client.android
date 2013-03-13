package de.msk.mylivetracker.client.android.mainview.updater;

import java.util.concurrent.atomic.AtomicInteger;

import de.msk.mylivetracker.client.android.util.LogUtils;

public abstract class AViewUpdater implements Runnable {

	private static AtomicInteger aiCnt = new AtomicInteger(0);
	
	public AViewUpdater() {
		aiCnt.getAndIncrement();
	}

	@Override
	public void run() {
		if (aiCnt.get() > 1) {
			// skipped.
		} else {
			try {
				this.updateView();
			} catch (Exception e) {
				LogUtils.info(e.toString());
			}
		}
		aiCnt.decrementAndGet();
	}

	public abstract void updateView();
}
