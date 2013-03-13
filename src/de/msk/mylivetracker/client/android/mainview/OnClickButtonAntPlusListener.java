package de.msk.mylivetracker.client.android.mainview;

import android.widget.ToggleButton;
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
	private ToggleButton btMain_ConnectDisconnectAnt;
	
	public OnClickButtonAntPlusListener(ToggleButton btMain_ConnectDisconnectAnt) {
		this.btMain_ConnectDisconnectAnt = btMain_ConnectDisconnectAnt;
	}

	private static final class StartStopAntPlusDialog extends AbstractYesNoDialog {

		private MainActivity activity;
		private ToggleButton btMain_ConnectDisconnectAnt;
		
		public StartStopAntPlusDialog(MainActivity activity,
			ToggleButton btMain_ConnectDisconnectAnt) {
			super(activity,
				AntPlusManager.get().hasSensorListeners() ? 	
				R.string.txMain_QuestionStopAntPlus :
				R.string.txMain_QuestionStartAntPlus);
			this.activity = activity;			
			this.btMain_ConnectDisconnectAnt = btMain_ConnectDisconnectAnt;
		}

		@Override
		public void onYes() {		
			startStopAntPlus(activity, !AntPlusManager.get().hasSensorListeners());
		}

		@Override
		public void onNo() {
			this.btMain_ConnectDisconnectAnt.setChecked(
				AntPlusManager.get().hasSensorListeners());
		}			
	}
	
	@Override
	public void onClick() {
		MainActivity activity = MainActivity.get();
		if (MainActivity.showStartStopInfoDialogIfInAutoMode()) {
			this.btMain_ConnectDisconnectAnt.setChecked(
				AntPlusManager.get().hasSensorListeners());
			return;
		}
		if (PrefsRegistry.get(OtherPrefs.class).getConfirmLevel().isHigh()) {
			StartStopAntPlusDialog dlg = new StartStopAntPlusDialog(
				activity, this.btMain_ConnectDisconnectAnt);
			dlg.show();
		} else {
			startStopAntPlus(activity, 
				!AntPlusManager.get().hasSensorListeners());
		}
	}
	
	public static void startStopAntPlus(MainActivity activity, boolean start) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null!");
		}
		ToggleButton btMain_ConnectDisconnectAnt = (ToggleButton)
			activity.findViewById(R.id.btMain_ConnectDisconnectAnt);
		if (!start && AntPlusManager.get().hasSensorListeners()) {
			AntPlusManager.stop();
		} else if (start && !AntPlusManager.get().hasSensorListeners()) {
			AntPlusManager.start();
		}			
		btMain_ConnectDisconnectAnt.setChecked(
			AntPlusManager.get().hasSensorListeners());
	}
}
