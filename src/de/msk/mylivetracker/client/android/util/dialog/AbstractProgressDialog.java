package de.msk.mylivetracker.client.android.util.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * classname: AbstractProgressDialog
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class AbstractProgressDialog<T extends Activity> {

	public void beforeTask(final T activity) {}
	public void doTask(final T activity) {}
	public void cleanUp(final T activity) {}
	
	private static final int NO_MESSAGE_ID = -1;
	private static final int SLEEP_BEFORE_RUN_TASK_IN_MSECS = 50;
	
	private static class CleanUpHandler extends Handler {
	}
	
	private class TaskRunner implements Runnable {

		@SuppressWarnings("rawtypes")
		private AbstractProgressDialog dialog;
		private Activity activity;
		private int progressMsgId;
		private int doneMsgId;

		@SuppressWarnings({ "rawtypes" })
		public TaskRunner(AbstractProgressDialog dialog, Activity activity,
			int progressMsgId, int doneMsgId) {
			this.dialog = dialog;
			this.activity = activity;
			this.progressMsgId = progressMsgId;
			this.doneMsgId = doneMsgId;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			dialog.run(activity, progressMsgId, doneMsgId);
		}
		
	}
	
	public void runOnUiThread(final T activity, final int progressMsgId) {
		this.runOnUiThread(activity, progressMsgId, NO_MESSAGE_ID);
	}
	
	public void runOnUiThread(final T activity, final int progressMsgId, final int doneMsgId) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null.");
		}
		activity.runOnUiThread(new TaskRunner(this, activity, progressMsgId, doneMsgId));
	}
	
	public void run(final T activity, final int progressMsgId) {
		this.run(activity, progressMsgId, NO_MESSAGE_ID);
	}
	
	public void run(final T activity, final int progressMsgId, final int doneMsgId) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null.");
		}
		String progressMessage = 
			((activity != null) && (progressMsgId != NO_MESSAGE_ID)) ?
			activity.getText(progressMsgId).toString() : null;		
				
		final ProgressDialog dialog = 
			((activity != null) && (progressMessage != null)) ?
			ProgressDialog.show(activity, "", 
				activity.getText(progressMsgId).toString(), true) : 
			null;
		
		final Handler handler = new CleanUpHandler() {
			public void handleMessage(Message msg) {
				if (activity != null) {
					cleanUp(activity);
				} 
				if (dialog != null) {
					dialog.dismiss();
				}
				if (doneMsgId != NO_MESSAGE_ID) {
					Toast.makeText(activity.getApplicationContext(), 
						activity.getString(doneMsgId),
						Toast.LENGTH_SHORT).show();
				}
		    }
		};
		Thread taskThread = new Thread() {  
			public void run() {
				try {
					Thread.sleep(SLEEP_BEFORE_RUN_TASK_IN_MSECS);
				} catch (InterruptedException e) {
					// noop.
				}
				if (activity != null) {
					doTask(activity);
				} 
				handler.sendEmptyMessage(0);
		    }
		};
		this.beforeTask(activity);
		taskThread.start();	
	}
}
