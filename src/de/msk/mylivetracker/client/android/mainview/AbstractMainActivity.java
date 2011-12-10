package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Chronometer;
import de.msk.mylivetracker.client.android.InfoActivity;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.listener.GpsStateListener;
import de.msk.mylivetracker.client.android.listener.LocationListener;
import de.msk.mylivetracker.client.android.listener.NmeaListener;
import de.msk.mylivetracker.client.android.mainview.updater.StatusBarUpdater;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.preferences.PrefsAccountActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsLocalizationActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsOtherActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsServerActivity;
import de.msk.mylivetracker.client.android.preferences.linksender.LinkSenderActivity;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadManager;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * AbstractMainActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history 000 initial 2011-08-18
 * 
 */
public abstract class AbstractMainActivity extends AbstractActivity {

	private GestureDetector gestureDetector = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
        return true;		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
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

	private void showPrefsWarningDialogIfIsTrackRunning(
		Class<? extends Activity> activityClassToStart) {
		if (Preferences.get().getConfirmLevel().isMedium()) {
			PrefsWarningDialog dlg = new PrefsWarningDialog(this,
				activityClassToStart);
			dlg.show();
		} else {
			this.startActivity(new Intent(this,
				activityClassToStart));
		}
	}

	private LocationManager locationManager;

	public LocationManager getLocationManager() {
		if (this.locationManager == null) {
			this.locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
		}
		return this.locationManager;
	}

	public void stopLocationListener() {
		this.getLocationManager().removeUpdates(LocationListener.get());
		this.getLocationManager().removeNmeaListener(NmeaListener.get());
		this.getLocationManager().removeGpsStatusListener(
				GpsStateListener.get());
		LocationListener.get().setActive(false);
	}	
	
	public void startActivityPrefsAccount() {
		if (TrackStatus.get().trackIsRunning()) {
			showPrefsWarningDialogIfIsTrackRunning(PrefsAccountActivity.class);
		} else {
			startActivity(new Intent(this, PrefsAccountActivity.class));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mnPrefsServer:
			if (TrackStatus.get().trackIsRunning()) {
				showPrefsWarningDialogIfIsTrackRunning(PrefsServerActivity.class);
			} else {
				startActivity(new Intent(this, PrefsServerActivity.class));
			}
			return true;
		case R.id.mnPrefsAccount:
			startActivityPrefsAccount();
			return true;
		case R.id.mnPrefsLocalization:
			if (TrackStatus.get().trackIsRunning()) {
				showPrefsWarningDialogIfIsTrackRunning(PrefsLocalizationActivity.class);
			} else {
				startActivity(new Intent(this, PrefsLocalizationActivity.class));
			}
			return true;
		case R.id.mnPrefsOther:
			if (TrackStatus.get().trackIsRunning()) {
				showPrefsWarningDialogIfIsTrackRunning(PrefsOtherActivity.class);
			} else {
				startActivity(new Intent(this, PrefsOtherActivity.class));
			}
			return true;
		case R.id.mnLinkSender:
			if (TrackStatus.get().trackIsRunning()) {
				showPrefsWarningDialogIfIsTrackRunning(LinkSenderActivity.class);
			} else {
				startActivity(new Intent(this, LinkSenderActivity.class));
			}
			return true;	
		case R.id.mnExit:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(this.getString(R.string.txMain_QuestionExit))
					.setCancelable(false)
					.setPositiveButton(this.getString(R.string.btYes),
							new DialogInterface.OnClickListener() {
								public void onClick(
									DialogInterface dialog, int id) {
									UploadManager.stopUploadManager();									
									MainActivity.get().stopLocationListener();
									MainActivity.get().stopAntPlusHeartrateListener();
									MainActivity.get().stopBatteryReceiver();
									MainActivity.get().stopPhoneStateListener();
									Chronometer chronometer = MainActivity.get().getUiChronometer();
									chronometer.stop();			
									chronometer.setBase(SystemClock.elapsedRealtime());
									TrackStatus.saveTrackStatus();
									//finish();
									StatusBarUpdater.cancelAppStatus();
									System.exit(0);
								}
							})
					.setNegativeButton(this.getString(R.string.btNo),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			builder.create().show();
			return true;
		case R.id.mnInfo:
			startActivity(new Intent(this, InfoActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
