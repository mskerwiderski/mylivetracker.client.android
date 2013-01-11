package de.msk.mylivetracker.client.android.auto;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.auto.AutoPrefs.AutoModeResetTrackMode;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;

/**
 * classname: AutoPrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class AutoPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
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
		public void onClick() {
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
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_prefs);
     
        this.setTitle(R.string.tiAutoPrefs);
        
        AutoPrefs prefs = PrefsRegistry.get(AutoPrefs.class);

        CheckBox cbAutoPrefs_AutoStart = (CheckBox)
        	findViewById(R.id.cbAutoPrefs_AutoStart);
        cbAutoPrefs_AutoStart.setText(App.getCtx().getString(
        	R.string.lbAutoPrefs_AutoStart, App.getAppName()));
        cbAutoPrefs_AutoStart.setChecked(prefs.isAutoStartEnabled());
            
        CheckBox cbAutoPrefs_AutoMode = (CheckBox)
        	findViewById(R.id.cbAutoPrefs_AutoMode);
        cbAutoPrefs_AutoMode.setChecked(prefs.isAutoModeEnabled());
        
        Spinner spAutoPrefs_ResetTrackMode = (Spinner)
        	findViewById(R.id.spAutoPrefs_ResetTrackMode);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.autoModeResetTrackMode, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAutoPrefs_ResetTrackMode.setAdapter(adapter);
        spAutoPrefs_ResetTrackMode.setSelection(
        	prefs.getAutoModeResetTrackMode().ordinal());
        
        Button btnPrefsOther_Save = (Button)
        	findViewById(R.id.btAutoPrefs_Save);
        Button btnPrefsOther_Cancel = (Button)
    		findViewById(R.id.btAutoPrefs_Cancel);
                
        btnPrefsOther_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				cbAutoPrefs_AutoMode,
				spAutoPrefs_ResetTrackMode,
				cbAutoPrefs_AutoStart));		
        btnPrefsOther_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
