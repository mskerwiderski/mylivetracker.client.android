package de.msk.mylivetracker.client.android.mainview;

import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.listener.LocationListener;
import de.msk.mylivetracker.client.android.mainview.updater.StatusBarUpdater;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * OnClickButtonLocationListenerOnOffListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class OnClickButtonLocationListenerOnOffListener implements OnClickListener {
	
	private static final class StartStopLocationListenerDialog extends AbstractYesNoDialog {

		private MainActivity activity;
				
		public StartStopLocationListenerDialog(MainActivity activity) {
			super(activity,
				LocationListener.get().isActive() ? 	
				R.string.txMain_QuestionStopLocalization :
				R.string.txMain_QuestionStartLocalization);
			this.activity = activity;			
		}

		@Override
		public void onYes() {		
			startStopLocationListener(activity);							
		}	
		
		/* (non-Javadoc)
		 * @see de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog#onNo()
		 */
		@Override
		public void onNo() {
			this.activity.getUiBtLocationListenerOnOff().setChecked(
				LocationListener.get().isActive());
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override 
	public void onClick(View v) {			
		MainActivity activity = MainActivity.get();
		if (Preferences.get().getConfirmLevel().isHigh()) {
			StartStopLocationListenerDialog dlg = new StartStopLocationListenerDialog(activity);
			dlg.show();
		} else {
			startStopLocationListener(activity);
		}
	}
	
	private static void startStopLocationListener(MainActivity activity) {
		if (LocationListener.get().isActive()) {
			activity.stopLocationListener();						
		} else {
			activity.startLocationListener();						
		}
		StatusBarUpdater.updateAppStatus();
		activity.updateView();
	}
}
