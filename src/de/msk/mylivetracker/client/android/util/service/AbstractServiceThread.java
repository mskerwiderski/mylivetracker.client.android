package de.msk.mylivetracker.client.android.util.service;

import android.os.Handler;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: AbstractServiceThread
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class AbstractServiceThread extends Thread {

	private boolean runOnlyOnce = false;
	private Handler handler = null;
	
	public void sendMessage(int what) {
		this.handler.sendEmptyMessage(what);
	}
	
	protected void initThreadObject(Handler handler) {
		this.handler = handler;
	}
	
	private volatile boolean stopThread = false;
	
	public void stopAndWaitUntilTerminated() {
		if (!this.isAlive() || this.isInterrupted()) {
			return;
		}
		LogUtils.info(this.getClass(), "stopAndWaitUntilTerminated...");
		boolean interrupted = false;
		this.stopThread = true;
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
		if (!this.stopThread) {
			try {
				this.init();
			} catch (InterruptedException e) {
				this.stopThread = true;
				LogUtils.info(this.getClass(), "init interrupted.");
			}
		}
		int loopCounter = 0;
		try {
			while (!this.stopThread && !this.doStopService() && 
				!(this.runOnlyOnce && (loopCounter > 0))) {
				this.runSinglePass();
				loopCounter++;
				Thread.sleep(this.getSleepAfterRunSinglePassInMSecs());
			}
		} catch (InterruptedException e) {
			LogUtils.info(this.getClass(), "runSinglePass interrupted.");
		} finally {
			this.cleanUp();
			LogUtils.info(this.getClass(), "cleanUp executed.");
		}
		
		this.sendMessage(AbstractService.MSG_STOP_SERVICE);
	}
	
	public abstract void init() throws InterruptedException;
	public abstract void runSinglePass() throws InterruptedException;
	public abstract long getSleepAfterRunSinglePassInMSecs();
	public abstract void cleanUp();
	public boolean doStopService() {
		return false;
	}
	protected void setRunOnlyOnce(boolean runOnlyOnce) {
		this.runOnlyOnce = runOnlyOnce;
	}
	protected boolean isRunOnlyOnce() {
		return runOnlyOnce;
	}
}
