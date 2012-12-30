package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.antplus.AntPlusManager;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonAntPlusListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonAntPlusListener extends ASafeOnClickListener {
	
	private static final class StartStopAntPlusDialog extends AbstractYesNoDialog {

		private MainActivity activity;
				
		public StartStopAntPlusDialog(MainActivity activity) {
			super(activity,
				AntPlusManager.get().hasSensorListeners() ? 	
				R.string.txMain_QuestionStopAntPlus :
				R.string.txMain_QuestionStartAntPlus);
			this.activity = activity;			
		}

		@Override
		public void onYes() {		
			startStopAntPlus(activity, !AntPlusManager.get().hasSensorListeners());
		}

		@Override
		public void onNo() {
			this.activity.getUiBtConnectDisconnectAnt().setChecked(
				AntPlusManager.get().hasSensorListeners());
		}			
	}
	
	@Override
	public void onClick() {
		MainActivity activity = MainActivity.get();
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			activity.getUiBtConnectDisconnectAnt().setChecked(
				AntPlusManager.get().hasSensorListeners());
			return;
		}
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh()) {
			StartStopAntPlusDialog dlg = new StartStopAntPlusDialog(activity);
			dlg.show();
		} else {
			startStopAntPlus(activity, 
				!AntPlusManager.get().hasSensorListeners());
		}
	}
	
	public static void startStopAntPlus(MainActivity activity, boolean start) {
		if (!start && AntPlusManager.get().hasSensorListeners()) {
			activity.stopAntPlusHeartrateListener();
		} else if (start && !AntPlusManager.get().hasSensorListeners()) {
			activity.startAntPlusHeartrateListener();
		}			
		activity.getUiBtConnectDisconnectAnt().setChecked(
			AntPlusManager.get().hasSensorListeners());
		activity.updateView();
	}
}
