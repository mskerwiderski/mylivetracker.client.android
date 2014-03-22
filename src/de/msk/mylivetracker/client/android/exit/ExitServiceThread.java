package de.msk.mylivetracker.client.android.exit;

import android.os.Handler;
import android.os.Message;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.service.AbstractServiceThread;

/**
 * classname: ExitServiceThread
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-20	origin.
 * 
 */
public class ExitServiceThread extends AbstractServiceThread {

	public static class ExitProgressDialog extends AbstractProgressDialog<AbstractActivity> {
		@Override
		public void beforeTask(AbstractActivity unused) {
		}
		@Override
		public void doTask(AbstractActivity unused) {
			LogUtils.infoMethodIn(this.getClass(), "doTask");
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
	
	public static Handler exitHandler = new Handler() {
		public void handleMessage(Message msg) {
			ExitProgressDialog dlg = new ExitProgressDialog();
			LogUtils.infoMethodState(ExitProgressDialog.class, "handleMessage", 
				"active", AbstractActivity.getActive().getClass());
			dlg.run(AbstractActivity.getActive(), R.string.exitProgressMessage);
	    }
	};
	
	@Override
	public void init() throws InterruptedException {
		
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		if (ExitService.isMarkedAsExit()) {
			sleep(ExitService.getExitMarker().getTimeoutMSecs());
			ExitService.resetExitMarker();
			exitHandler.sendEmptyMessage(0);
		}
	}

	@Override
	public long getSleepAfterRunSinglePassInMSecs() {
		return 1000;
	}

	@Override
	public void cleanUp() {
		// noop.
	}
}
