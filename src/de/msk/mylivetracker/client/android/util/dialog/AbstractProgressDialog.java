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
	
	public void beforeTask() {}
	public void doTask() {}
	public void cleanUp() {}
	
	private static final int NO_MESSAGE_ID = -1;
	private static final int SLEEP_BEFORE_RUN_TASK_IN_MSECS = 50;
	
	public void run() {
		this.run(null, NO_MESSAGE_ID, NO_MESSAGE_ID);
	}
	
	public void run(final T activity) {
		this.run(activity, NO_MESSAGE_ID, NO_MESSAGE_ID);
	}
	
	public void run(final T activity, final int progressMsgId) {
		this.run(activity, progressMsgId, NO_MESSAGE_ID);
	}
	
	private static class CleanUpHandler extends Handler {
	}
	
	public void run(final T activity, final int progressMsgId, final int doneMsgId) {
				
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
				} else {
					cleanUp();
				}
				if (dialog != null) {
					dialog.dismiss();
				}
				if ((activity != null) && (doneMsgId != NO_MESSAGE_ID)) {
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
				} else {
					doTask();
				}
				handler.sendEmptyMessage(0);
		    }
		};
		if (activity != null) {
			this.beforeTask(activity);
		} else {
			this.beforeTask();
		}
		taskThread.start();	
	}
}
