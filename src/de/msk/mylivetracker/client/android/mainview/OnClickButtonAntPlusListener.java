package de.msk.mylivetracker.client.android.mainview;

import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * OnClickButtonAntPlusListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001	2012-02-20 
 *     	o startStopAntPlus adapted, that it can be used by OnClickButtonStartStopListener::startStopTrack.
 *      o If in auto mode, startStopAntPlus is rejected.
 * 000 	2011-08-11 initial.
 * 
 */
public class OnClickButtonAntPlusListener implements OnClickListener {
	
	private static final class StartStopAntPlusDialog extends AbstractYesNoDialog {

		private MainActivity activity;
				
		public StartStopAntPlusDialog(MainActivity activity) {
			super(activity,
				activity.getAntPlusManager().hasSensorListeners() ? 	
				R.string.txMain_QuestionStopAntPlus :
				R.string.txMain_QuestionStartAntPlus);
			this.activity = activity;			
		}

		@Override
		public void onYes() {		
			startStopAntPlus(activity, !activity.getAntPlusManager().hasSensorListeners());
		}

		/* (non-Javadoc)
		 * @see de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog#onNo()
		 */
		@Override
		public void onNo() {
			this.activity.getUiBtConnectDisconnectAnt().setChecked(
				activity.getAntPlusManager().hasSensorListeners());
		}			
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		MainActivity activity = MainActivity.get();
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			activity.getUiBtConnectDisconnectAnt().setChecked(
				activity.getAntPlusManager().hasSensorListeners());
			return;
		}
		if (Preferences.get().getConfirmLevel().isHigh()) {
			StartStopAntPlusDialog dlg = new StartStopAntPlusDialog(activity);
			dlg.show();
		} else {
			startStopAntPlus(activity, !activity.getAntPlusManager().hasSensorListeners());
		}
	}
	
	public static void startStopAntPlus(MainActivity activity, boolean start) {
		if (!start && activity.getAntPlusManager().hasSensorListeners()) {
			activity.stopAntPlusHeartrateListener();
		} else if (start && !activity.getAntPlusManager().hasSensorListeners()) {
			activity.startAntPlusHeartrateListener();
		}			
		activity.getUiBtConnectDisconnectAnt().setChecked(
			activity.getAntPlusManager().hasSensorListeners());
		activity.updateView();
	}
}
