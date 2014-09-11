package de.msk.mylivetracker.client.android.trackingmode;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.AutoModeResetTrackMode;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.CountdownInSecs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: TrackingModePrefsActivity
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-01-25	origin.
 * 
 */
public class TrackingModePrefsActivity extends AbstractActivity {

	private static final class OnTrackingModeItemSelectedListener implements OnItemSelectedListener {
		private TrackingModePrefsActivity activity;
		
		public OnTrackingModeItemSelectedListener(
			TrackingModePrefsActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
			int position, long rowId) {
			LogUtils.infoMethodIn(TrackingModePrefsActivity.class, "onItemSelected", position);
			if (position != currSelectedModeId) {
				currSelectedModeId = position;
				this.activity.viewOrHideOptionFiels();
			}
			LogUtils.infoMethodOut(TrackingModePrefsActivity.class, "onItemSelected", currSelectedModeId);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// noop.
		}
	}
	
	private volatile static int currSelectedModeId = 0;
	
	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private TrackingModePrefsActivity activity;
		private Spinner spTrackingModePrefs_TrackingMode;
		private Spinner spTrackingModePrefs_CountdownInSecs;
		private EditText etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs;
		private EditText etTrackingModePrefs_CheckpointMessage;
		private CheckBox cbTrackingModePrefs_StartAfterReboot;
		private CheckBox cbTrackingModePrefs_RunOnlyIfBattFullOrCharging;
		private Spinner spTrackingModePrefs_ResetTrackMode;
		
		public OnClickButtonSaveListener(
			TrackingModePrefsActivity activity,
			Spinner spTrackingModePrefs_TrackingMode,
			Spinner spTrackingModePrefs_CountdownInSecs,
			EditText etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs,
			EditText etTrackingModePrefs_CheckpointMessage,
			CheckBox cbTrackingModePrefs_StartAfterReboot,
			CheckBox cbTrackingModePrefs_RunOnlyIfBattFullOrCharging,
			Spinner spTrackingModePrefs_ResetTrackMode) {
			this.activity = activity;
			this.spTrackingModePrefs_TrackingMode = spTrackingModePrefs_TrackingMode;
			this.spTrackingModePrefs_CountdownInSecs = spTrackingModePrefs_CountdownInSecs;
			this.etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs = etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs;
			this.etTrackingModePrefs_CheckpointMessage = etTrackingModePrefs_CheckpointMessage;
			this.cbTrackingModePrefs_StartAfterReboot = cbTrackingModePrefs_StartAfterReboot;
			this.cbTrackingModePrefs_RunOnlyIfBattFullOrCharging = cbTrackingModePrefs_RunOnlyIfBattFullOrCharging;
			this.spTrackingModePrefs_ResetTrackMode = spTrackingModePrefs_ResetTrackMode;
		}

		@Override
		public void onClick() {
			boolean valid = true;
			TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);
			prefs.setTrackingMode(TrackingMode.values()[spTrackingModePrefs_TrackingMode.getSelectedItemPosition()]);
			if (prefs.getTrackingMode().equals(TrackingMode.Standard)) {
				prefs.setCountdownInSecs(CountdownInSecs.values()[spTrackingModePrefs_CountdownInSecs.getSelectedItemPosition()]);
			} else if (prefs.getTrackingMode().equals(TrackingMode.Checkpoint)) {
				valid = 
					ValidatorUtils.validateEditTextNumber(
						this.activity, 
						R.string.fdTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs, 
						etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs, 
						true, 10, 600, true) &&
					ValidatorUtils.validateEditTextString(
						this.activity, 
						R.string.fdTrackingModePrefs_CheckpointMessage, 
						etTrackingModePrefs_CheckpointMessage, 
						0, 20, true);	
				if (valid) {
					prefs.setMaxCheckpointPeriodInSecs(Integer.parseInt(
						etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs.getText().toString()));
					prefs.setCheckpointMessage(etTrackingModePrefs_CheckpointMessage.getText().toString());
				}
			} else if (prefs.getTrackingMode().equals(TrackingMode.Auto)) {
				prefs.setStartAfterReboot(
					cbTrackingModePrefs_StartAfterReboot.isChecked());
				prefs.setRunOnlyIfBattFullOrCharging(
					cbTrackingModePrefs_RunOnlyIfBattFullOrCharging.isChecked());
				prefs.setAutoModeResetTrackMode(AutoModeResetTrackMode.values()[
                    spTrackingModePrefs_ResetTrackMode.getSelectedItemPosition()]);
			} 
			
			if (valid) {
				PrefsRegistry.save(TrackingModePrefs.class);
				this.activity.finish();
			}
		}		
	}
	
	private void viewOrHideOptionFiels() {
		LogUtils.infoMethodIn(TrackingModePrefsActivity.class, "viewOrHideOptionFiels", currSelectedModeId);
		int viewStandardState = (TrackingMode.Standard.ordinal() == currSelectedModeId) ? View.VISIBLE : View.GONE;
		int viewCheckpointState = (TrackingMode.Checkpoint.ordinal() == currSelectedModeId) ? View.VISIBLE : View.GONE;
		int viewAutoState = (TrackingMode.Auto.ordinal() == currSelectedModeId) ? View.VISIBLE : View.GONE;
		LogUtils.infoMethodState(TrackingModePrefsActivity.class, "viewOrHideOptionFiels",
			"viewStandardState=" + viewStandardState,
			"viewCheckpointState=" + viewCheckpointState, 
			"viewAutoState=" + viewAutoState);	
		
		((LinearLayout)findViewById(R.id.llTrackingModePrefs_OptionsOnlyForTrackingStandardMode)).setVisibility(viewStandardState);
		((LinearLayout)findViewById(R.id.llTrackingModePrefs_tvCountdownInSecs)).setVisibility(viewStandardState);
		((LinearLayout)findViewById(R.id.llTrackingModePrefs_cbCountdownInSecs)).setVisibility(viewStandardState);
    	
		((LinearLayout)findViewById(R.id.llTrackingModePrefs_OptionsOnlyForTrackingCheckpointMode)).setVisibility(viewCheckpointState);
		((LinearLayout)findViewById(R.id.llTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs)).setVisibility(viewCheckpointState);
    	((LinearLayout)findViewById(R.id.llTrackingModePrefs_etCheckpointMessage)).setVisibility(viewCheckpointState);
    	((LinearLayout)findViewById(R.id.llTrackingModePrefs_tvCheckpointMessage)).setVisibility(viewCheckpointState);
    	
    	((LinearLayout)findViewById(R.id.llTrackingModePrefs_OptionsOnlyForTrackingAutoMode)).setVisibility(viewAutoState);
    	((LinearLayout)findViewById(R.id.llTrackingModePrefs_cbStartAfterReboot)).setVisibility(viewAutoState);
    	((LinearLayout)findViewById(R.id.llTrackingModePrefs_cbRunOnlyIfBattFullOrCharging)).setVisibility(viewAutoState);
    	((LinearLayout)findViewById(R.id.llTrackingModePrefs_tvResetTrackMode)).setVisibility(viewAutoState);
    	((LinearLayout)findViewById(R.id.llTrackingModePrefs_cbResetTrackMode)).setVisibility(viewAutoState);
    	LogUtils.infoMethodOut(TrackingModePrefsActivity.class, "viewOrHideOptionFiels", currSelectedModeId);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackingmode_prefs);
     
        this.setTitle(R.string.tiTrackingModePrefs);
        
        TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);

        currSelectedModeId = prefs.getTrackingMode().ordinal();
        
        Spinner spTrackingModePrefs_TrackingMode = (Spinner)
        	findViewById(R.id.spTrackingModePrefs_TrackingMode);
        ArrayAdapter<?> adapterTrackingMode = ArrayAdapter.createFromResource(
            this, R.array.trackingMode, android.R.layout.simple_spinner_item);
        adapterTrackingMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrackingModePrefs_TrackingMode.setAdapter(adapterTrackingMode);
        spTrackingModePrefs_TrackingMode.setSelection(currSelectedModeId);
        
        // options for trackingmode standard.
        Spinner spTrackingModePrefs_CountdownInSecs = (Spinner)
        	findViewById(R.id.spTrackingModePrefs_CountdownInSecs);
        ArrayAdapter<?> adapterCountdownInSecs = ArrayAdapter.createFromResource(
            this, R.array.countdownInSecs, android.R.layout.simple_spinner_item);
        adapterCountdownInSecs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrackingModePrefs_CountdownInSecs.setAdapter(adapterCountdownInSecs);
        spTrackingModePrefs_CountdownInSecs.setSelection(prefs.getCountdownInSecs().ordinal());
        
        // options for trackingmode checkpoint.
        EditText etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs = 
        	(EditText)findViewById(R.id.etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs);
        etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs.setText(
        	String.valueOf(prefs.getMaxCheckpointPeriodInSecs()));
        EditText etTrackingModePrefs_CheckpointMessage = 
        	(EditText)findViewById(R.id.etTrackingModePrefs_CheckpointMessage);
        etTrackingModePrefs_CheckpointMessage.setText(prefs.getCheckpointMessage());

        // options for trackingmode auto.
        CheckBox cbTrackingModePrefs_StartAfterReboot =
    		(CheckBox)findViewById(R.id.cbTrackingModePrefs_StartAfterReboot);
        cbTrackingModePrefs_StartAfterReboot.setChecked(prefs.isStartAfterReboot());
        CheckBox cbTrackingModePrefs_RunOnlyIfBattFullOrCharging =
        	(CheckBox)findViewById(R.id.cbTrackingModePrefs_RunOnlyIfBattFullOrCharging);
        cbTrackingModePrefs_RunOnlyIfBattFullOrCharging.setChecked(
        	prefs.isRunOnlyIfBattFullOrCharging());
        Spinner spTrackingModePrefs_ResetTrackMode = (Spinner)
        	findViewById(R.id.spTrackingModePrefs_ResetTrackMode);
        ArrayAdapter<?> adapterResetTrackMode = ArrayAdapter.createFromResource(
            this, R.array.trackingModeAutoResetTrackMode, android.R.layout.simple_spinner_item);
        adapterResetTrackMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrackingModePrefs_ResetTrackMode.setAdapter(adapterResetTrackMode);
        spTrackingModePrefs_ResetTrackMode.setSelection(
        	prefs.getAutoModeResetTrackMode().ordinal());
        spTrackingModePrefs_TrackingMode.setOnItemSelectedListener(
    		new OnTrackingModeItemSelectedListener(this));
        
        Button btnPrefsOther_Save = (Button)
        	findViewById(R.id.btTrackingModePrefs_Save);
        Button btnPrefsOther_Cancel = (Button)
    		findViewById(R.id.btTrackingModePrefs_Cancel);
                
        btnPrefsOther_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				spTrackingModePrefs_TrackingMode,
				spTrackingModePrefs_CountdownInSecs,
				etTrackingModePrefs_MaxWaitingPeriodForCheckpointInSecs,
				etTrackingModePrefs_CheckpointMessage,
				cbTrackingModePrefs_StartAfterReboot,
				cbTrackingModePrefs_RunOnlyIfBattFullOrCharging,
				spTrackingModePrefs_ResetTrackMode));		
        btnPrefsOther_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
        
        viewOrHideOptionFiels();
    }
}
