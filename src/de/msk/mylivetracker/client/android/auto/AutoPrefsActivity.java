package de.msk.mylivetracker.client.android.auto;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.auto.AutoPrefs.AutoModeResetTrackMode;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;

/**
 * AutoPrefsActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-24 	revised for v1.5.x.
 * 000 	2012-02-17 	initial.
 * 
 */
public class AutoPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private AutoPrefsActivity activity;
		private CheckBox cbAutoPrefs_AutoMode;
		private Spinner spAutoPrefs_ResetTrackMode;
		private CheckBox cbAutoPrefs_AutoStart;
		
		public OnClickButtonSaveListener(
			AutoPrefsActivity activity,
			CheckBox cbAutoPrefs_AutoMode,
			Spinner spAutoPrefs_ResetTrackMode,
			CheckBox cbAutoPrefs_AutoStart) {
			this.activity = activity;
			this.cbAutoPrefs_AutoMode = cbAutoPrefs_AutoMode;
			this.spAutoPrefs_ResetTrackMode = spAutoPrefs_ResetTrackMode;
			this.cbAutoPrefs_AutoStart = cbAutoPrefs_AutoStart;
		}

		@Override
		public void onClick(View v) {
			boolean valid = true;
			if (valid) {
				AutoPrefs prefs = PrefsRegistry.get(AutoPrefs.class);
				prefs.setAutoModeEnabled(cbAutoPrefs_AutoMode.isChecked());
				prefs.setAutoModeResetTrackMode(AutoModeResetTrackMode.values()[spAutoPrefs_ResetTrackMode.getSelectedItemPosition()]);
				prefs.setAutoStartEnabled(cbAutoPrefs_AutoStart.isChecked());
				PrefsRegistry.save(AutoPrefs.class);
				this.activity.finish();
			}			
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private AutoPrefsActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(AutoPrefsActivity activity) {
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
        setContentView(R.layout.auto_prefs);
     
        this.setTitle(R.string.tiAutoPrefs);
        
        AutoPrefs prefs = PrefsRegistry.get(AutoPrefs.class);
        
        CheckBox cbAutoPrefs_AutoMode = (CheckBox)findViewById(R.id.cbAutoPrefs_AutoMode);
        cbAutoPrefs_AutoMode.setChecked(prefs.isAutoModeEnabled());
        
        Spinner spAutoPrefs_ResetTrackMode = (Spinner)findViewById(R.id.spAutoPrefs_ResetTrackMode);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.autoModeResetTrackMode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAutoPrefs_ResetTrackMode.setAdapter(adapter);
        spAutoPrefs_ResetTrackMode.setSelection(prefs.getAutoModeResetTrackMode().ordinal());
        
        CheckBox cbAutoPrefs_AutoStart = (CheckBox)findViewById(R.id.cbAutoPrefs_AutoStart);
        cbAutoPrefs_AutoStart.setChecked(prefs.isAutoStartEnabled());
        
        Button btnPrefsOther_Save = (Button) findViewById(R.id.btAutoPrefs_Save);
        Button btnPrefsOther_Cancel = (Button) findViewById(R.id.btAutoPrefs_Cancel);
                
        btnPrefsOther_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				cbAutoPrefs_AutoMode,
				spAutoPrefs_ResetTrackMode,
				cbAutoPrefs_AutoStart));		
        btnPrefsOther_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
