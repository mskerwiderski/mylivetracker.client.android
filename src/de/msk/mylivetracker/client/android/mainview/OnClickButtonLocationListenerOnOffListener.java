package de.msk.mylivetracker.client.android.mainview;

import android.view.View;
import android.view.View.OnClickListener;
import de.msk.mylivetracker.client.android.listener.LocationListener;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * OnClickButtonLocationListenerOnOffListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001	2012-02-18 
 *     	o startStopLocationListener adapted, that it can be used by OnClickButtonStartStopListener::startStopTrack.
 *     	o If in auto mode, startStopLocationListener is rejected.
 * 000 	2011-08-11 initial.
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
			startStopLocationListener(activity, !LocationListener.get().isActive());							
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
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			activity.getUiBtLocationListenerOnOff().setChecked(
				LocationListener.get().isActive());
			return;
		}
		
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh()) {
			StartStopLocationListenerDialog dlg = new StartStopLocationListenerDialog(activity);
			dlg.show();
		} else {
			startStopLocationListener(activity, !LocationListener.get().isActive());
		}
	}
	
	public static void startStopLocationListener(MainActivity activity, boolean start) {
		if (!start && LocationListener.get().isActive()) {
			activity.stopLocationListener();						
		} else if (start && !LocationListener.get().isActive()){
			activity.startLocationListener();						
		}
		activity.getUiBtLocationListenerOnOff().setChecked(
			LocationListener.get().isActive());
		activity.updateView();
	}
}
