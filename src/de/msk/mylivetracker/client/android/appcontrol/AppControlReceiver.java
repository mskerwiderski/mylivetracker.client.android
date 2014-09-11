package de.msk.mylivetracker.client.android.appcontrol;

import org.apache.commons.lang3.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;

/**
 * classname: AppControlReceiver
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.7.0
 * 
 * history:
 * 000	2014-04-02	origin.
 * 
 */
public class AppControlReceiver extends BroadcastReceiver {

	public static class ExitProgressDialog extends AbstractProgressDialog<AbstractActivity> {
		@Override
		public void beforeTask(AbstractActivity unused) {
		}
		@Override
		public void doTask(AbstractActivity unused) {
			LogUtils.infoMethodIn(this.getClass(), "doTask");
			AppControl.stopAllAppsActivities();
			AbstractActivity[] activities = AbstractActivity.getActivityArray();
			for (int idx = activities.length-1; idx > 0; idx--) {
				AbstractActivity activity = activities[idx];
				if (activity != null) {
					LogUtils.infoMethodState(this.getClass(), "doTask", "destroy", activity.getClass());
					activity.finish(); 
					while (AbstractActivity.activityExists(activity)) {
						try { 
							Thread.sleep(500); 
							LogUtils.infoMethodState(this.getClass(), "doTask", "still exists", activity.getClass());
						} catch(Exception e) 
						{};
					}
					LogUtils.infoMethodState(this.getClass(), "doTask", "destroyed");
				}
			}
			LogUtils.infoMethodOut(this.getClass(), "doTask");
		}
		@Override
		public void cleanUp(AbstractActivity unused) {
			LogUtils.infoMethodState(this.getClass(), "cleanUp", "destroy and exit MainActivity");
			MainActivity.destroy();
			System.exit(0);	
		}
	}
	
	public static final String ACTION_APP_EXIT = "de.msk.mylivetracker.client.android.appcontrol.EXIT";
	public static final String ACTION_PARAM_TIMEOUT_MSECS = "TIMEOUT_MSECS";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.infoMethodIn(AppControlReceiver.class, "onReceive");
		if (StringUtils.equals(intent.getAction(), ACTION_APP_EXIT)) {
			LogUtils.infoMethodState(AppControlReceiver.class, "onReceive", "action", intent.getAction());
			int timeoutMSecs = intent.getIntExtra(ACTION_PARAM_TIMEOUT_MSECS, 100);
			LogUtils.infoMethodState(AppControlReceiver.class, "onReceive", "timeoutMSecs", timeoutMSecs);
			new Handler().postDelayed(new Runnable(){
			    public void run() {
			    	exitHandler.sendEmptyMessage(0);
			    }
			}, timeoutMSecs);
		} else {
			throw new IllegalArgumentException("unknown intent action: " + intent.getAction());
		}
		LogUtils.infoMethodOut(AppControlReceiver.class, "onReceive");
	}

	public static Handler exitHandler = new Handler() {
		public void handleMessage(Message msg) {
			ExitProgressDialog dlg = new ExitProgressDialog();
			dlg.run(AbstractActivity.getActive(), R.string.exitProgressMessage);
	    }
	};
}
