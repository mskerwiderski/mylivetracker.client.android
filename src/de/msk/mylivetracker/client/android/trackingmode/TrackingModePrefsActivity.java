package de.msk.mylivetracker.client.android.trackingmode;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.AutoModeResetTrackMode;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs.TrackingMode;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;

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

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private TrackingModePrefsActivity activity;
		private Spinner spTrackingModePrefs_TrackingMode;
		private Spinner spTrackingModePrefs_ResetTrackMode;
		private CheckBox cbTrackingModePrefs_AutoStart;
		
		public OnClickButtonSaveListener(
			TrackingModePrefsActivity activity,
			Spinner spTrackingModePrefs_TrackingMode,
			Spinner spTrackingModePrefs_ResetTrackMode,
			CheckBox cbTrackingModePrefs_AutoStart) {
			this.activity = activity;
			this.spTrackingModePrefs_TrackingMode = spTrackingModePrefs_TrackingMode;
			this.spTrackingModePrefs_ResetTrackMode = spTrackingModePrefs_ResetTrackMode;
			this.cbTrackingModePrefs_AutoStart = cbTrackingModePrefs_AutoStart;
		}

		@Override
		public void onClick() {
			boolean valid = true;
			if (valid) {
				TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);
				prefs.setTrackingMode(TrackingMode.values()[spTrackingModePrefs_TrackingMode.getSelectedItemPosition()]);
				prefs.setAutoModeResetTrackMode(AutoModeResetTrackMode.values()[spTrackingModePrefs_ResetTrackMode.getSelectedItemPosition()]);
				prefs.setAutoStartEnabled(cbTrackingModePrefs_AutoStart.isChecked());
				PrefsRegistry.save(TrackingModePrefs.class);
				this.activity.finish();
			}			
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trackingmode_prefs);
     
        this.setTitle(R.string.tiTrackingModePrefs);
        
        TrackingModePrefs prefs = PrefsRegistry.get(TrackingModePrefs.class);

        Spinner spTrackingModePrefs_TrackingMode = (Spinner)
        	findViewById(R.id.spTrackingModePrefs_TrackingMode);
        ArrayAdapter<?> adapterTrackingMode = ArrayAdapter.createFromResource(
            this, R.array.trackingMode, android.R.layout.simple_spinner_item);
        adapterTrackingMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrackingModePrefs_TrackingMode.setAdapter(adapterTrackingMode);
        spTrackingModePrefs_TrackingMode.setSelection(
        	prefs.getTrackingMode().ordinal());
            
        CheckBox cbTrackingModePrefs_AutoStart = (CheckBox)
        	findViewById(R.id.cbTrackingModePrefs_AutoStart);
        cbTrackingModePrefs_AutoStart.setText(App.getCtx().getString(
        	R.string.lbTrackingModePrefs_AutoStart, App.getAppName()));
        cbTrackingModePrefs_AutoStart.setChecked(prefs.isAutoStartEnabled());
            
        Spinner spTrackingModePrefs_ResetTrackMode = (Spinner)
        	findViewById(R.id.spTrackingModePrefs_ResetTrackMode);
        ArrayAdapter<?> adapterResetTrackMode = ArrayAdapter.createFromResource(
            this, R.array.trackingModeAutoResetTrackMode, android.R.layout.simple_spinner_item);
        adapterResetTrackMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTrackingModePrefs_ResetTrackMode.setAdapter(adapterResetTrackMode);
        spTrackingModePrefs_ResetTrackMode.setSelection(
        	prefs.getAutoModeResetTrackMode().ordinal());
        
        Button btnPrefsOther_Save = (Button)
        	findViewById(R.id.btTrackingModePrefs_Save);
        Button btnPrefsOther_Cancel = (Button)
    		findViewById(R.id.btTrackingModePrefs_Cancel);
                
        btnPrefsOther_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				spTrackingModePrefs_TrackingMode,
				spTrackingModePrefs_ResetTrackMode,
				cbTrackingModePrefs_AutoStart));		
        btnPrefsOther_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
