package de.msk.mylivetracker.client.android.mainview;

import android.os.SystemClock;
import android.widget.Chronometer;
import android.widget.ToggleButton;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.upload.UploadService;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

/**
 * classname: OnClickButtonResetListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonResetListener extends ASafeOnClickListener {
	
	private static final class ResetTrackDialog extends AbstractYesNoDialog {

		private MainActivity activity;
				
		public ResetTrackDialog(MainActivity activity) {
			super(activity, R.string.txMain_QuestionResetTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {		
			resetTrack(activity);							
		}	
	}
	
	public void onClick() {
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) return;
		final MainActivity activity = MainActivity.get();
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isMedium()) {
			ResetTrackDialog dlg = new ResetTrackDialog(activity);
			dlg.show();
		} else {
			resetTrack(activity);
		}
	}	
	
	private static class ResetTrackProgressDialog extends AbstractProgressDialog<MainActivity> {
		private Chronometer chronometer;
		private ToggleButton btMain_StartStopTrack;
    	private ToggleButton btMain_LocationListenerOnOff;
		private ToggleButton btMain_ConnectDisconnectAnt;
		
		public ResetTrackProgressDialog(Chronometer chronometer,
			ToggleButton btMain_StartStopTrack,
			ToggleButton btMain_LocationListenerOnOff,
			ToggleButton btMain_ConnectDisconnectAnt) {
			super();
			this.chronometer = chronometer;
			this.btMain_StartStopTrack = btMain_StartStopTrack;
			this.btMain_LocationListenerOnOff = btMain_LocationListenerOnOff;
			this.btMain_ConnectDisconnectAnt = btMain_ConnectDisconnectAnt;
		}
		@Override
		public void beforeTask(MainActivity activity) {
			this.chronometer.stop();
		}
		@Override
		public void doTask(MainActivity activity) {
			AbstractService.stopService(UploadService.class);
		}
		@Override
		public void cleanUp(MainActivity activity) {
			this.chronometer.setBase(SystemClock.elapsedRealtime());
			AbstractService.stopService(LocalizationService.class);
			activity.stopAntPlusHeartrateListener();
			this.btMain_StartStopTrack.setChecked(false);
			this.btMain_LocationListenerOnOff.setChecked(false);
			this.btMain_ConnectDisconnectAnt.setChecked(false);
			this.btMain_StartStopTrack.setChecked(false);
			TrackStatus.reset();
			activity.updateView();			
		}
	}
	
	public static void resetTrack(final MainActivity activity) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null!");
		}
		Chronometer chronometer = (Chronometer)
        	activity.findViewById(R.id.tvMain_Runtime);
		ToggleButton btMain_StartStopTrack = (ToggleButton)
			activity.findViewById(R.id.btMain_StartStopTrack);
    	ToggleButton btMain_LocationListenerOnOff = (ToggleButton)
			activity.findViewById(R.id.btMain_LocationListenerOnOff);
		ToggleButton btMain_ConnectDisconnectAnt = (ToggleButton)
			activity.findViewById(R.id.btMain_ConnectDisconnectAnt);
		ResetTrackProgressDialog resetTrackDialog = 
			new ResetTrackProgressDialog(
				chronometer,
				btMain_StartStopTrack,
				btMain_LocationListenerOnOff,
				btMain_ConnectDisconnectAnt);
		resetTrackDialog.run(activity, 
			R.string.txMain_InfoResettingTracking, 
			R.string.txMain_InfoResetTrackDone);
	}
}
