package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.account.AccountPrefsActivity;
import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.dropbox.DropboxConnectActivity;
import de.msk.mylivetracker.client.android.emergency.EmergencyPrefsActivity;
import de.msk.mylivetracker.client.android.httpprotocolparams.HttpProtocolParamsPrefsActivity;
import de.msk.mylivetracker.client.android.info.InfoActivity;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefsActivity;
import de.msk.mylivetracker.client.android.message.MessagePrefsActivity;
import de.msk.mylivetracker.client.android.mylivetrackerportal.MyLiveTrackerPortalConnectActivity;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefsActivity;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefsActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefsActivity;
import de.msk.mylivetracker.client.android.remoteaccess.RemoteAccessPrefsActivity;
import de.msk.mylivetracker.client.android.server.ServerPrefsActivity;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackexport.TrackExportActivity;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefsActivity;
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

	private Menu menu = null;
	private int currentMenuStructure = -1;
	
	private void updateMenuStructure() {
		MenuInflater inflater = getMenuInflater();
		int menuStructure = R.menu.menu_without_settings;
		if (App.isAdminMode()) {
			menuStructure = R.menu.menu_complete;
		} 
		if ((this.menu != null) && (menuStructure != this.currentMenuStructure)) {
			menu.clear();
			inflater.inflate(menuStructure, this.menu);
			this.currentMenuStructure = menuStructure;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (this.menu == null) {
			this.menu = menu;
		}
		updateMenuStructure();
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
		if (TrackingModePrefs.isAuto() && 
			!PrefsRegistry.get(TrackingModePrefs.class).isInterruptibleOnMainWindow()) {
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
		
		protected ExitYesNoDialog(Context ctx) {
			super(ctx, R.string.txMain_QuestionExit, new String[] {App.getAppName()});
		}

		@Override
		public void onYes() {
			AppControl.exitApp(100);
		}
	}

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
		case R.id.mnPrefsMessages:
			startActivityWithWarningDlgIfTrackRunning(MessagePrefsActivity.class);
			return true;
		case R.id.mnPrefsEmergency:
			startActivityWithWarningDlgIfTrackRunning(EmergencyPrefsActivity.class);
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
		case R.id.mnPrefsRemoteAccess:
			startActivityWithWarningDlgIfTrackRunning(RemoteAccessPrefsActivity.class);
			return true;
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
			ExitYesNoDialog dlg = new ExitYesNoDialog(this);
			dlg.show();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected boolean isPrefsActivity() {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateMenuStructure();
	}
}
