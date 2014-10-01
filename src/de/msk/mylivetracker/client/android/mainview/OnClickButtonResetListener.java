package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonResetListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2014-03-29	revised for v1.7.x.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonResetListener extends ASafeOnClickListener {
	
	private static final class ResetTrackDialog extends AbstractYesNoDialog {

		public ResetTrackDialog(MainActivity activity) {
			super(activity, R.string.txMain_QuestionResetTrack);
		}

		@Override
		public void onYes() {		
			AppControl.resetTrack();							
		}	
	}
	
	public void onClick() {
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) return;
		final MainActivity activity = MainActivity.get();
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isMedium()) {
			ResetTrackDialog dlg = new ResetTrackDialog(activity);
			dlg.show();
		} else {
			AppControl.resetTrack();
		}
	}	
}
