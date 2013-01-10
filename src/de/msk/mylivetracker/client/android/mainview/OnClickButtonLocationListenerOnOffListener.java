package de.msk.mylivetracker.client.android.mainview;

import android.widget.ToggleButton;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.service.AbstractService;

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
	private ToggleButton btMain_LocationListenerOnOff;
	
	public OnClickButtonLocationListenerOnOffListener(
		ToggleButton btMain_LocationListenerOnOff) {
		this.btMain_LocationListenerOnOff = btMain_LocationListenerOnOff;
	}

	private static final class StartStopLocationListenerDialog extends AbstractYesNoDialog {
		private MainActivity activity;
		private ToggleButton btMain_LocationListenerOnOff;
		
		public StartStopLocationListenerDialog(MainActivity activity,
			ToggleButton btMain_LocationListenerOnOff) {
			super(activity,
				AbstractService.isServiceRunning(LocalizationService.class) ? 	
				R.string.txMain_QuestionStopLocalization :
				R.string.txMain_QuestionStartLocalization);
			this.activity = activity;			
			this.btMain_LocationListenerOnOff = btMain_LocationListenerOnOff;
		}

		@Override
		public void onYes() {		
			startStopLocationListener(activity, true,
				!AbstractService.isServiceRunning(LocalizationService.class));							
		}	
		
		@Override
		public void onNo() {
			this.btMain_LocationListenerOnOff.setChecked(
				AbstractService.isServiceRunning(LocalizationService.class));
		}
	}
	
	@Override 
	public void onClick() {	
		MainActivity activity = MainActivity.get();
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			this.btMain_LocationListenerOnOff.setChecked(
				AbstractService.isServiceRunning(LocalizationService.class));
			return;
		}
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh()) {
			StartStopLocationListenerDialog dlg = 
				new StartStopLocationListenerDialog(activity,
					btMain_LocationListenerOnOff);
			dlg.show();
		} else {
			startStopLocationListener(activity, true, 
				!AbstractService.isServiceRunning(LocalizationService.class));
		}
	}
	
	private static class StartLocationListenerProgressDialog extends AbstractProgressDialog<MainActivity> {
		private ToggleButton btMain_LocationListenerOnOff;
		public StartLocationListenerProgressDialog(
			ToggleButton btMain_LocationListenerOnOff) {
			this.btMain_LocationListenerOnOff = btMain_LocationListenerOnOff;
		}
		@Override
		public void doTask(MainActivity activity) {
			AbstractService.startService(LocalizationService.class);
		}
		@Override
		public void cleanUp(MainActivity activity) {
			this.btMain_LocationListenerOnOff.setChecked(true);
			activity.updateView();
		}
	}
	
	private static class StopLocationListenerProgressDialog extends AbstractProgressDialog<MainActivity> {
		private ToggleButton btMain_LocationListenerOnOff;
		public StopLocationListenerProgressDialog(
			ToggleButton btMain_LocationListenerOnOff) {
			this.btMain_LocationListenerOnOff = btMain_LocationListenerOnOff;
		}
		@Override
		public void doTask(MainActivity activity) {
			AbstractService.stopService(LocalizationService.class);
		}
		@Override
		public void cleanUp(MainActivity activity) {
			this.btMain_LocationListenerOnOff.setChecked(false);
			activity.updateView();
		}
	}
	
	public static void startStopLocationListener(
		final MainActivity activity, boolean withDialog, boolean start) {
		ToggleButton btMain_LocationListenerOnOff = (ToggleButton)
			activity.findViewById(R.id.btMain_LocationListenerOnOff);
		if (withDialog) {
			if (start) {
				StartLocationListenerProgressDialog startLocationListenerDialog = 
					new StartLocationListenerProgressDialog(
						btMain_LocationListenerOnOff);
				startLocationListenerDialog.run(activity, 
				R.string.txMain_InfoStartingLocalization, 
				R.string.txMain_InfoStartLocalizationDone);
			} else {
				StopLocationListenerProgressDialog stopLocationListenerDialog = 
					new StopLocationListenerProgressDialog(
						btMain_LocationListenerOnOff);
				stopLocationListenerDialog.run(activity, 
				R.string.txMain_InfoStoppingLocalization, 
				R.string.txMain_InfoStopLocalizationDone);
			}
		} else {
			if (!start && AbstractService.isServiceRunning(LocalizationService.class)) {
				AbstractService.stopService(LocalizationService.class);						
			} else if (start && 
				!AbstractService.isServiceRunning(LocalizationService.class)){
				AbstractService.startService(LocalizationService.class);				
			}
			btMain_LocationListenerOnOff.setChecked(
				AbstractService.isServiceRunning(LocalizationService.class));
			activity.updateView();
		}
	}
}
