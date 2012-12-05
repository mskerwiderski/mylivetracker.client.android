package de.msk.mylivetracker.client.android.mainview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
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
import de.msk.mylivetracker.client.android.listener.GpsStateListener;
import de.msk.mylivetracker.client.android.listener.LocationListener;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.preferences.PrefsAccountActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsAutoActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsHttpProtocolParamsActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsLocalizationActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsOtherActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsPinCodeQueryActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRemoteAccessActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsServerActivity;
import de.msk.mylivetracker.client.android.preferences.linksender.LinkSenderActivity;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.track.TrackActivity;
import de.msk.mylivetracker.client.android.util.dialog.AbstractInfoDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * AbstractMainActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history 
 * 001	2012-02-20 
 * 		o startActivityPrefsAuto implemented.
 * 		o startActivityPrefsLocalization implemented.
 * 		o startActivityPrefsServer implemented.
 * 		o isGpsEnabled implemented.
 * 		o isLocalizationByNetworkEnabled implemented.
 * 		o getWifiManager implemented.
 * 		o isWifiEnabled implemented.
 * 000 	2011-08-18 initial.
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
	
	private static final class IsProFeatureDialog extends AbstractInfoDialog {

		public IsProFeatureDialog(AbstractMainActivity activity) {
			super(activity, R.string.txIsProFeature);
		}

		@Override
		public void onOk() {
			// noop.
		}
	}
	
	public static boolean showStartStopInfoDialogIfInAutoMode() {
		if (Preferences.get().isAutoModeEnabled()) {
			SimpleInfoDialog dlg = new SimpleInfoDialog(
				MainActivity.get(), R.string.txPrefs_InfoAutoModeEnabled);
			dlg.show();
			return true;
		}
		return false;
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

	private void showIsProFeatureDialog() {
		IsProFeatureDialog dlg = new IsProFeatureDialog(this);
		dlg.show();
	}
	
	private LocationManager locationManager;

	public LocationManager getLocationManager() {
		if (this.locationManager == null) {
			this.locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
		}
		return this.locationManager;
	}

	private WifiManager wifiManager;
	
	public WifiManager getWifiManager() {
		if (this.wifiManager == null) {
			this.wifiManager = (WifiManager) this
					.getSystemService(Context.WIFI_SERVICE);
		}
		return this.wifiManager;
	}
	
	public static boolean isLocalizationByGpsEnabled() {
		LocationManager locationManager = MainActivity.get().getLocationManager();
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public static boolean isLocalizationByNetworkEnabled() {
		LocationManager locationManager = MainActivity.get().getLocationManager();
		return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public static boolean isWifiEnabled() {
		WifiManager wifiManager = MainActivity.get().getWifiManager();
		return wifiManager.isWifiEnabled();
	}
	
	public void stopLocationListener() {
		this.getLocationManager().removeUpdates(LocationListener.get());
		this.getLocationManager().removeGpsStatusListener(
				GpsStateListener.get());
		LocationListener.get().setActive(false);
	}	
	
	public void startActivityPrefsLocalization() {
		if (TrackStatus.get().trackIsRunning()) {
			showPrefsWarningDialogIfIsTrackRunning(PrefsLocalizationActivity.class);
		} else {
			startActivity(new Intent(this, PrefsLocalizationActivity.class));
		}
	}
	
	public void startActivityPrefsServer() {
		if (TrackStatus.get().trackIsRunning()) {
			showPrefsWarningDialogIfIsTrackRunning(PrefsServerActivity.class);
		} else {
			startActivity(new Intent(this, PrefsServerActivity.class));
		}
	}
	
	public void startActivityPrefsAccount() {
		if (TrackStatus.get().trackIsRunning()) {
			showPrefsWarningDialogIfIsTrackRunning(PrefsAccountActivity.class);
		} else {
			startActivity(new Intent(this, PrefsAccountActivity.class));
		}
	}
	
	public void startActivityPrefsAuto() {
		if (TrackStatus.get().trackIsRunning()) {
			showPrefsWarningDialogIfIsTrackRunning(PrefsAutoActivity.class);
		} else {
			startActivity(new Intent(this, PrefsAutoActivity.class));
		}
	}
	
	private static class ExitYesNoDialog extends AbstractYesNoDialog {
		
		private Handler handler;
		
		protected ExitYesNoDialog(Context ctx, int question, Handler handler) {
			super(ctx, question);
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
		case R.id.mnPrefsAuto:
			if (TrackStatus.get().trackIsRunning()) {
				showPrefsWarningDialogIfIsTrackRunning(PrefsAutoActivity.class);
			} else {
				startActivity(new Intent(this, PrefsAutoActivity.class));
			}
			return true;
		case R.id.mnPrefsPinCodeQuery:
			if (!App.isPro()) {
				showIsProFeatureDialog();
			} else {
				startActivity(new Intent(this, PrefsPinCodeQueryActivity.class));
			}
			return true;
		case R.id.mnPrefsRemoteAccess:
			if (!App.isPro()) {
				showIsProFeatureDialog();
			} else {
				startActivity(new Intent(this, PrefsRemoteAccessActivity.class));
			}
			return true;	
		case R.id.mnPrefsHttpProtocolParams:
			if (!App.isPro()) {
				showIsProFeatureDialog();
			} else {
				if (TrackStatus.get().trackIsRunning()) {
					showPrefsWarningDialogIfIsTrackRunning(PrefsHttpProtocolParamsActivity.class);
				} else {
					startActivity(new Intent(this, PrefsHttpProtocolParamsActivity.class));
				}
			}
			return true;
		case R.id.mnTrack:
			if (TrackStatus.get().trackIsRunning()) {
				showPrefsWarningDialogIfIsTrackRunning(TrackActivity.class);
			} else {
				startActivity(new Intent(this, TrackActivity.class));
			}
			return true;	
		case R.id.mnLinkSender:
			if (TrackStatus.get().trackIsRunning()) {
				showPrefsWarningDialogIfIsTrackRunning(LinkSenderActivity.class);
			} else {
				startActivity(new Intent(this, LinkSenderActivity.class));
			}
			return true;	
		case R.id.mnInfo:
			startActivity(new Intent(this, InfoActivity.class));
			return true;
		case R.id.mnExit:
			ExitYesNoDialog dlg = new ExitYesNoDialog(this,
				R.string.txMain_QuestionExit, MainActivity.exitHandler);
			dlg.show();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
