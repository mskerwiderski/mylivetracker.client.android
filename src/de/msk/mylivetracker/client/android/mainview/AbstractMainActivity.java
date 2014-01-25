package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.InfoActivity;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.account.AccountPrefsActivity;
import de.msk.mylivetracker.client.android.dropbox.DropboxConnectActivity;
import de.msk.mylivetracker.client.android.httpprotocolparams.HttpProtocolParamsPrefsActivity;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefsActivity;
import de.msk.mylivetracker.client.android.mylivetrackerportal.MyLiveTrackerPortalConnectActivity;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefsActivity;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefsActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefsActivity;
import de.msk.mylivetracker.client.android.server.ServerPrefsActivity;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackexport.TrackExportActivity;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefsActivity;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * classname: AbstractMainActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class AbstractMainActivity extends AbstractActivity {

	public abstract Class<? extends Runnable> getViewUpdater();
	
	public void updateView() {
		try {
			this.runOnUiThread(getViewUpdater().newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private GestureDetector gestureDetector = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
	    gestureDetector = 
        	new GestureDetector(this, 
        		new GestureDetector.SimpleOnGestureListener() {
                	public boolean onFling(
                		MotionEvent e1, MotionEvent e2, 
                		float velocityX, float velocityY) {
                		int dx = (int) (e2.getX() - e1.getX());
 
                		// don't accept the fling if it's too short
                		// as it may conflict with a button push
                		if (Math.abs(dx) > 60 && Math.abs(velocityX) > Math.abs(velocityY)) {
                			onSwitchToView(velocityX > 0);
                			return true;
                		} else {
                			return false;
                		}
                	}
            	});
	}
    
	public abstract void onSwitchToView(boolean next);
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
        return true;		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	private static final class PrefsWarningDialog extends AbstractYesNoDialog {

		private AbstractMainActivity activity;
		private Class<? extends Activity> activityClassToStart;

		public PrefsWarningDialog(AbstractMainActivity activity,
				Class<? extends Activity> activityClassToStart) {
			super(activity,
					R.string.txPrefs_WarningChangePrefsDuringRunningTrack,
					R.string.btContinue, R.string.btCancel);
			this.activity = activity;
			this.activityClassToStart = activityClassToStart;
		}

		@Override
		public void onYes() {
			this.activity.startActivity(new Intent(this.activity,
				this.activityClassToStart));
		}
	}
	
	public static boolean showStartStopInfoDialogIfInAutoMode() {
		if (PrefsRegistry.get(TrackingModePrefs.class).getTrackingMode().equals(TrackingMode.Auto)) {
			SimpleInfoDialog.show(MainActivity.get(), 
				R.string.txPrefs_InfoAutoModeEnabled);
			return true;
		}
		return false;
	}
	
	private void showPrefsWarningDialogIfIsTrackRunning(
		Class<? extends Activity> activityClassToStart) {
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isMedium()) {
			PrefsWarningDialog dlg = new PrefsWarningDialog(this,
				activityClassToStart);
			dlg.show();
		} else {
			this.startActivity(new Intent(this,
				activityClassToStart));
		}
	}

	public void startActivityWithWarningDlgIfTrackRunning(Class<? extends Activity> activityClassToStart) {
		if (TrackStatus.get().trackIsRunning()) {
			showPrefsWarningDialogIfIsTrackRunning(activityClassToStart);
		} else {
			startActivity(new Intent(this, activityClassToStart));
		}
	}
	
	private static class ExitYesNoDialog extends AbstractYesNoDialog {
		
		private Handler handler;
		
		protected ExitYesNoDialog(Context ctx, Handler handler) {
			super(ctx, R.string.txMain_QuestionExit, new String[] {App.getAppName()});
			this.handler = handler;
		}

		@Override
		public void onYes() {
			handler.sendEmptyMessage(0);
		}
	}

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
			MainActivity.exit();
		}
	}
	
	public static Handler exitHandler = new Handler() {
		public void handleMessage(Message msg) {
			ExitProgressDialog exitDialog = new ExitProgressDialog();
			exitDialog.run(
				MainDetailsActivity.isActive() ? 
					MainDetailsActivity.get() : MainActivity.get());
	    }
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mnPrefsServer:
			startActivityWithWarningDlgIfTrackRunning(ServerPrefsActivity.class);
			return true;
		case R.id.mnPrefsAccount:
			startActivityWithWarningDlgIfTrackRunning(AccountPrefsActivity.class);
			return true;
		case R.id.mnPrefsProtocol:
			startActivityWithWarningDlgIfTrackRunning(ProtocolPrefsActivity.class);
			return true;	
		case R.id.mnPrefsLocalization:
			startActivityWithWarningDlgIfTrackRunning(LocalizationPrefsActivity.class);
			return true;
		case R.id.mnTrackExport:
			startActivityWithWarningDlgIfTrackRunning(TrackExportActivity.class);
			return true;	
		case R.id.mnMyLiveTrackerPortalConnect:
			startActivityWithWarningDlgIfTrackRunning(MyLiveTrackerPortalConnectActivity.class);
			return true;
		case R.id.mnDropboxConnect:
			startActivityWithWarningDlgIfTrackRunning(DropboxConnectActivity.class);
			return true;	
		case R.id.mnPrefsPinCodeQuery:
			startActivityWithWarningDlgIfTrackRunning(PinCodeQueryPrefsActivity.class);
			return true;
		case R.id.mnPrefsTrackingMode:
			startActivityWithWarningDlgIfTrackRunning(TrackingModePrefsActivity.class);
			return true;
//		case R.id.mnPrefsRemoteAccess:
//			startActivityWithWarningDlgIfTrackRunning(RemoteAccessPrefsActivity.class);
//			return true;
		case R.id.mnPrefsHttpProtocolParams:
			startActivityWithWarningDlgIfTrackRunning(HttpProtocolParamsPrefsActivity.class);
			return true;
		case R.id.mnPrefsOther:
			startActivityWithWarningDlgIfTrackRunning(OtherPrefsActivity.class);
			return true;	
		case R.id.mnInfo:
			startActivity(new Intent(this, InfoActivity.class));
			return true;
		case R.id.mnExit:
			ExitYesNoDialog dlg = new ExitYesNoDialog(
				this, MainActivity.exitHandler);
			dlg.show();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
