package de.msk.mylivetracker.client.android.preferences;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences.AutoModeResetTrackMode;
import de.msk.mylivetracker.client.android.pro.R;

/**
 * PrefsAutoActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-02-17 initial.
 * 
 */
public class PrefsAutoActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private PrefsAutoActivity activity;
		private Preferences preferences;
		private CheckBox cbPrefsAuto_AutoMode;
		private Spinner spPrefsAuto_ResetTrackMode;
		private CheckBox cbPrefsAuto_AutoStart;
		
		public OnClickButtonSaveListener(
			PrefsAutoActivity activity,
			Preferences preferences,
			CheckBox cbPrefsAuto_AutoMode,
			Spinner spPrefsAuto_ResetTrackMode,
			CheckBox cbPrefsAuto_AutoStart) {
			this.activity = activity;
			this.preferences = preferences;
			this.cbPrefsAuto_AutoMode = cbPrefsAuto_AutoMode;
			this.spPrefsAuto_ResetTrackMode = spPrefsAuto_ResetTrackMode;
			this.cbPrefsAuto_AutoStart = cbPrefsAuto_AutoStart;
		}

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			boolean valid = true;
				
			if (valid) {
				preferences.setAutoModeEnabled(cbPrefsAuto_AutoMode.isChecked());
				preferences.setAutoModeResetTrackMode(AutoModeResetTrackMode.values()[spPrefsAuto_ResetTrackMode.getSelectedItemPosition()]);
				preferences.setAutoStartEnabled(cbPrefsAuto_AutoStart.isChecked());
				Preferences.save();
				this.activity.finish();
			}			
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PrefsAutoActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(PrefsAutoActivity activity) {
			this.activity = activity;
		}
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	    	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_auto);
     
        this.setTitle(R.string.tiPrefsAuto);
        
        Preferences prefs = Preferences.get();
        
        CheckBox cbPrefsAuto_AutoMode = (CheckBox)findViewById(R.id.cbPrefsAuto_AutoMode);
        cbPrefsAuto_AutoMode.setChecked(prefs.isAutoModeEnabled());
        
        Spinner spPrefsAuto_ResetTrackMode = (Spinner)findViewById(R.id.spPrefsAuto_ResetTrackMode);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.autoModeResetTrackMode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrefsAuto_ResetTrackMode.setAdapter(adapter);
        spPrefsAuto_ResetTrackMode.setSelection(prefs.getAutoModeResetTrackMode().ordinal());
        
        CheckBox cbPrefsAuto_AutoStart = (CheckBox)findViewById(R.id.cbPrefsAuto_AutoStart);
        cbPrefsAuto_AutoStart.setChecked(prefs.isAutoStartEnabled());
        
        Button btnPrefsOther_Save = (Button) findViewById(R.id.btPrefsAuto_Save);
        Button btnPrefsOther_Cancel = (Button) findViewById(R.id.btPrefsAuto_Cancel);
                
        btnPrefsOther_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, prefs,
				cbPrefsAuto_AutoMode,
				spPrefsAuto_ResetTrackMode,
				cbPrefsAuto_AutoStart));		
        btnPrefsOther_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
