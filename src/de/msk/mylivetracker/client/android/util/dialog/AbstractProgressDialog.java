package de.msk.mylivetracker.client.android.util.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * AbstractProgressDialog.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-10-28 initial.
 * 
 */
public abstract class AbstractProgressDialog<T extends Activity> {

	public abstract void beforeTask(final T activity);
	public abstract void doTask(final T activity);
	public abstract void cleanUp(final T activity);
	
	private static final int NO_PROGRESS_MSG_ID = -1;
	private static final int SLEEP_BEFORE_RUN_TASK_IN_MSECS = 50;
	
	public void run(final T activity, final int progressMsgId) {
		this.run(activity, progressMsgId, NO_PROGRESS_MSG_ID);
	}
	
	public void run(final T activity, final int progressMsgId, final int doneMsgId) {
		String message = activity.getText(progressMsgId).toString();
				
		final ProgressDialog dialog = 
			ProgressDialog.show(activity, "", message, true);
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				cleanUp(activity);
				dialog.dismiss();
				if (doneMsgId != NO_PROGRESS_MSG_ID) {
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
				doTask(activity);
				handler.sendEmptyMessage(0);
		    }
		};
		this.beforeTask(activity);
		taskThread.start();	
	}
}
