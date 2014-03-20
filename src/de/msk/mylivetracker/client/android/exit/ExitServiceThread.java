package de.msk.mylivetracker.client.android.exit;

import android.os.Handler;
import android.os.Message;
import de.msk.mylivetracker.client.android.mainview.AbstractMainActivity;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.MainDetailsActivity;
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

	private static class ExitProgressDialog extends AbstractProgressDialog<AbstractMainActivity> {
		@Override
		public void beforeTask(AbstractMainActivity activity) {
		}
		@Override
		public void doTask(AbstractMainActivity activity) {
			if (MainDetailsActivity.isActive()) {
				MainDetailsActivity.close();
				while (MainDetailsActivity.isActive()) {
					try { Thread.sleep(50); } catch(Exception e) {};
				}
			}
		}
		@Override
		public void cleanUp(AbstractMainActivity activity) {
			MainActivity.destroy();
			System.exit(0);	
		}
	}
	
	public static Handler exitHandler = new Handler() {
		public void handleMessage(Message msg) {
			ExitProgressDialog dlg = new ExitProgressDialog();
			dlg.run(
				MainDetailsActivity.isActive() ? 
					MainDetailsActivity.get() : MainActivity.get());
	    }
	};
	
	@Override
	public void init() throws InterruptedException {
		
	}

	@Override
	public void runSinglePass() throws InterruptedException {
		if (ExitService.isMarkedAsExit()) {
			sleep(ExitService.getExitMarker().getTimeoutMSecs());
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
