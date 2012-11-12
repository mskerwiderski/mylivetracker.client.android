package de.msk.mylivetracker.client.android.util.service;

import de.msk.mylivetracker.client.android.util.LogUtils;

public abstract class AbstractServiceThread extends Thread {

	private volatile boolean terminated = false;
	
	public void stopAndWaitUntilTerminated() {
		if (!this.isAlive() || this.isInterrupted()) {
			return;
		}
		LogUtils.info(this.getClass(), "stopAndWaitUntilTerminated...");
		this.interrupt();
		boolean interrupted = false;
		while (!this.isAlive() && !interrupted) {
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
		if (!this.isInterrupted()) {
			try {
				this.init();
			} catch (InterruptedException e) {
				LogUtils.info(this.getClass(), "init interrupted.");
			}
		}
		if (!this.isInterrupted()) {
			try {
				boolean run = !this.isInterrupted(); 
				while (run) {
					this.runSinglePass();
					if (this.runOnlyOneSinglePass()) {
						run = false;
					} else {
						Thread.sleep(this.getSleepAfterRunSinglePassInMSecs());
						run = !this.isInterrupted();
					}
				}
			} catch (InterruptedException e) {
				LogUtils.info(this.getClass(), "runSinglePass interrupted.");
			} finally {
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
