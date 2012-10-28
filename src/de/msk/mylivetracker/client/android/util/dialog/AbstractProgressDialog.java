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

	private T activity;
	private int progressMsgId;
	private int doneMsgId;
	
	public AbstractProgressDialog(T activity, int progressMsgId, int doneMsgId) {
		this.activity = activity;
		this.progressMsgId = progressMsgId;
		this.doneMsgId = doneMsgId;
	}
	
	public abstract void doTask();
	public abstract void cleanUp();
	
	public void run() {
		String message = activity.getText(progressMsgId).toString();
				
		final ProgressDialog dialog = 
			ProgressDialog.show(activity, "", message, true);
		
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				cleanUp();
				dialog.dismiss();
				Toast.makeText(activity.getApplicationContext(), 
					activity.getString(doneMsgId),
					Toast.LENGTH_LONG).show();
		    }
		};
				
		Thread taskThread = new Thread() {  
			public void run() {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// noop.
				}
				doTask();
				handler.sendEmptyMessage(0);
		    }
		};
		taskThread.start();	
	}
}
