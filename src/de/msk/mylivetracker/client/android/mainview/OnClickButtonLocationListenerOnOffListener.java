package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.localization.LocationListener;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonLocationListenerOnOffListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonLocationListenerOnOffListener extends ASafeOnClickListener {
	
	private static final class StartStopLocationListenerDialog extends AbstractYesNoDialog {

		private MainActivity activity;
				
		public StartStopLocationListenerDialog(MainActivity activity) {
			super(activity,
				LocationListener.isActive() ? 	
				R.string.txMain_QuestionStopLocalization :
				R.string.txMain_QuestionStartLocalization);
			this.activity = activity;			
		}

		@Override
		public void onYes() {		
			startStopLocationListener(activity, !LocationListener.isActive());							
		}	
		
		@Override
		public void onNo() {
			this.activity.getUiBtLocationListenerOnOff().setChecked(
				LocationListener.isActive());
		}
	}
	
	@Override 
	public void onClick() {	
		MainActivity activity = MainActivity.get();
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			activity.getUiBtLocationListenerOnOff().setChecked(
				LocationListener.isActive());
			return;
		}
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh()) {
			StartStopLocationListenerDialog dlg = new StartStopLocationListenerDialog(activity);
			dlg.show();
		} else {
			startStopLocationListener(activity, !LocationListener.isActive());
		}
	}
	
	public static void startStopLocationListener(MainActivity activity, boolean start) {
		if (!start && LocationListener.isActive()) {
			LocationListener.stop();						
		} else if (start && !LocationListener.isActive()){
			LocationListener.start();						
		}
		activity.getUiBtLocationListenerOnOff().setChecked(
			LocationListener.isActive());
		activity.updateView();
	}
}
