package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.localization.LocalizationService;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
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
 * 001	2014-03-29	revised for v1.7.x.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonLocationListenerOnOffListener extends ASafeOnClickListener {
	
	private static final class StartStopLocationListenerDialog extends AbstractYesNoDialog {
		public StartStopLocationListenerDialog() {
			super(MainActivity.get(),
				AbstractService.isServiceRunning(LocalizationService.class) ? 	
				R.string.txMain_QuestionStopLocalization :
				R.string.txMain_QuestionStartLocalization);
		}
		@Override
		public void onYes() {		
			AppControl.startOrStopLocalization();							
		}	
	}
	
	@Override 
	public void onClick() {	
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			return;
		}
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh()) {
			StartStopLocationListenerDialog dlg = 
				new StartStopLocationListenerDialog();
			dlg.show();
		} else {
			AppControl.startOrStopLocalization();
		}
	}
}
