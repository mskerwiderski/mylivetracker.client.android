package de.msk.mylivetracker.client.android.mainview;

import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.updater.StatusBarUpdater;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * OnClickButtonAntPlusListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
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
			startStopAntPlus(activity);							
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
		if (Preferences.get().getConfirmLevel().isHigh()) {
			StartStopAntPlusDialog dlg = new StartStopAntPlusDialog(activity);
			dlg.show();
		} else {
			startStopAntPlus(activity);
		}
	}
	
	private static void startStopAntPlus(MainActivity activity) {
		if (activity.getAntPlusManager().hasSensorListeners()) {
			activity.stopAntPlusHeartrateListener();
		} else {
			activity.startAntPlusHeartrateListener();
		}				
		StatusBarUpdater.updateAppStatus();
		activity.updateView();
	}
}
