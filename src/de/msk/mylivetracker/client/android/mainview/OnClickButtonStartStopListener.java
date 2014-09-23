package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonStartStopListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 001	2014-03-29	revised for v1.7.x.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonStartStopListener extends ASafeOnClickListener {
	
	private static final class StartStopTrackDialog extends AbstractYesNoDialog {
		public StartStopTrackDialog() {
			super(MainActivity.get(),
				AppControl.trackIsRunning() ? 	
				(TrackingModePrefs.isCheckpoint() ? 
					R.string.txMain_QuestionStopCheckpoint : 
					R.string.txMain_QuestionStopTrack) :
				(TrackingModePrefs.isCheckpoint() ?
					R.string.txMain_QuestionStartCheckpoint :
					R.string.txMain_QuestionStartTrack));
		}
		@Override
		public void onYes() {
			if (AppControl.trackIsRunning()) {
				AppControl.stopTrack();
			} else {
				AppControl.startTrack();
			}
		}	
	}
	
	@Override
	public void onClick() {
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			return;
		}
		if ((PrefsRegistry.get(TrackingModePrefs.class).
				getTrackingMode().equals(TrackingMode.Checkpoint) &&
			PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isMax()) ||
			(PrefsRegistry.get(TrackingModePrefs.class).
				getTrackingMode().equals(TrackingMode.Standard) &&
			PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh())) {
			StartStopTrackDialog dlg = new StartStopTrackDialog();
			dlg.show();
		} else {
			if (AppControl.trackIsRunning()) {
				AppControl.stopTrack();
			} else {
				AppControl.startTrack();
			}
		}
	}
}
