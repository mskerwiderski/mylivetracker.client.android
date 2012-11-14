package de.msk.mylivetracker.client.android.util.service;

import de.msk.mylivetracker.client.android.util.LogUtils;

public abstract class AbstractServiceThread extends Thread {

	private volatile boolean stop = false;
	private volatile boolean terminated = false;
	
	public void stopAndWaitUntilTerminated() {
		if (!this.isAlive() || this.isInterrupted()) {
			return;
		}
		LogUtils.info(this.getClass(), "stopAndWaitUntilTerminated...");
		this.stop = true;
		boolean interrupted = false;
		while (this.isAlive() && !interrupted) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}
		LogUtils.info(this.getClass(), "stopAndWaitUntilTerminated...done.");
	}

	@Override
	public void run() {
		if (this.terminated) {
			throw new RuntimeException("thread already terminated.");
		}
		if (!this.stop) {
			try {
				this.init();
			} catch (InterruptedException e) {
				this.stop = true;
				LogUtils.info(this.getClass(), "init interrupted.");
			}
		}
		if (!this.stop) {
			try {
				while (!this.stop) {
					this.runSinglePass();
					if (this.runOnlyOneSinglePass()) {
						this.stop = true;
					} else {
						Thread.sleep(this.getSleepAfterRunSinglePassInMSecs());
					}
				}
			} catch (InterruptedException e) {
				this.stop = true;
				LogUtils.info(this.getClass(), "runSinglePass interrupted.");
			} finally {
				this.stop = true;
				this.cleanUp();
				LogUtils.info(this.getClass(), "cleanUp executed.");
			}
		}
		
		this.terminated = true;
	}
	
	public abstract void init() throws InterruptedException;
	public abstract void runSinglePass() throws InterruptedException;
	public abstract long getSleepAfterRunSinglePassInMSecs();
	public abstract void cleanUp();
	public boolean runOnlyOneSinglePass() {
		return false;
	}
}
