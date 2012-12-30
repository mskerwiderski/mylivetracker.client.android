package de.msk.mylivetracker.client.android.localization;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefs.LocalizationMode;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: LocalizationPrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class LocalizationPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private LocalizationPrefsActivity activity;
		private Spinner spLocalizationPrefs_LocalizationMode;
		private EditText etLocalizationPrefs_TimeTriggerInSecs;
		private EditText etLocalizationPrefs_DistanceTriggerInMtr;
		private EditText etLocalizationPrefs_LocationAccuracyRequiredInMtr;
		private EditText etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr;
		
		public OnClickButtonSaveListener(
			LocalizationPrefsActivity activity,
			Spinner spLocalizationPrefs_LocalizationMode,
			EditText etLocalizationPrefs_TimeTriggerInSecs,
			EditText etLocalizationPrefs_DistanceTriggerInMtr,
			EditText etLocalizationPrefs_LocationAccuracyRequiredInMtr,
			EditText etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr) {
			this.activity = activity;
			this.spLocalizationPrefs_LocalizationMode = spLocalizationPrefs_LocalizationMode;
			this.etLocalizationPrefs_TimeTriggerInSecs = etLocalizationPrefs_TimeTriggerInSecs;
			this.etLocalizationPrefs_DistanceTriggerInMtr = etLocalizationPrefs_DistanceTriggerInMtr;
			this.etLocalizationPrefs_LocationAccuracyRequiredInMtr = etLocalizationPrefs_LocationAccuracyRequiredInMtr;
			this.etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr = etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr;
		}

		@Override
		public void onClick() {
			boolean valid = true;
			LocalizationMode localizationMode =
				LocalizationMode.values()[spLocalizationPrefs_LocalizationMode.getSelectedItemPosition()];
			String localizationModeDisplayName = 
				this.activity.getResources().getStringArray(R.array.localizationModes)
				[spLocalizationPrefs_LocalizationMode.getSelectedItemPosition()];
			valid = valid && 
				ValidatorUtils.validateIfLocalizationModeIsSupported(
					this.activity,
					localizationMode,
					localizationModeDisplayName) &&	
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdLocalizationPrefs_TimeTriggerInSecs, 
					etLocalizationPrefs_TimeTriggerInSecs, 
					true, 
					0, 3600, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdLocalizationPrefs_DistanceTriggerInMtr, 
					etLocalizationPrefs_DistanceTriggerInMtr, 
					true, 
					0, 10000, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdLocalizationPrefs_LocationAccuracyRequiredInMtr, 
					etLocalizationPrefs_LocationAccuracyRequiredInMtr, 
					true, 
					0, 1000, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr, 
					etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr, 
					true, 
					0, 10000, true);			
			
			if (valid) {
				LocalizationPrefs prefs = PrefsRegistry.get(LocalizationPrefs.class);
				prefs.setLocalizationMode(localizationMode);
				prefs.setTimeTriggerInSeconds(
					Integer.parseInt(etLocalizationPrefs_TimeTriggerInSecs.getText().toString()));
				prefs.setDistanceTriggerInMeter(
					Integer.parseInt(etLocalizationPrefs_DistanceTriggerInMtr.getText().toString()));
				prefs.setAccuracyRequiredInMeter(
					Integer.parseInt(etLocalizationPrefs_LocationAccuracyRequiredInMtr.getText().toString()));
				prefs.setDistBtwTwoLocsForDistCalcRequiredInCMtr(
					Integer.parseInt(etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr.getText().toString()));
				PrefsRegistry.save(LocalizationPrefs.class);
				this.activity.finish();
			}
		}		
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.localization_prefs);
     
        this.setTitle(R.string.tiLocalizationPrefs);
        
        LocalizationPrefs prefs = PrefsRegistry.get(LocalizationPrefs.class);
        
        Spinner spLocalizationPrefs_LocalizationMode = 
        	(Spinner)findViewById(R.id.spLocalizationPrefs_LocalizationMode);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.localizationModes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocalizationPrefs_LocalizationMode.setAdapter(adapter);
        spLocalizationPrefs_LocalizationMode.setSelection(prefs.getLocalizationMode().ordinal());     
        
        EditText etLocalizationPrefs_TimeTriggerInSecs = 
        	(EditText)findViewById(R.id.etLocalizationPrefs_TimeTriggerInSecs);
        etLocalizationPrefs_TimeTriggerInSecs.setText(String.valueOf(prefs.getTimeTriggerInSeconds()));
        EditText etLocalizationPrefs_DistanceTriggerInMtr = 
        	(EditText)findViewById(R.id.etLocalizationPrefs_DistanceTriggerInMtr);
        etLocalizationPrefs_DistanceTriggerInMtr.setText(String.valueOf(prefs.getDistanceTriggerInMeter()));
        EditText etLocalizationPrefs_LocationAccuracyRequiredInMtr = 
        	(EditText)findViewById(R.id.etLocalizationPrefs_LocationAccuracyRequiredInMtr);
        etLocalizationPrefs_LocationAccuracyRequiredInMtr.setText(String.valueOf(prefs.getAccuracyRequiredInMeter()));
        EditText etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr = 
        	(EditText)findViewById(R.id.etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr);
        etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr.setText(
        	String.valueOf(prefs.getDistBtwTwoLocsForDistCalcRequiredInCMtr()));
        
    	Button btLocalizationPrefs_Save = (Button)
    		findViewById(R.id.btLocalizationPrefs_Save);
        Button btLocalizationPrefs_Cancel = (Button)
        	findViewById(R.id.btLocalizationPrefs_Cancel);
                
        btLocalizationPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				spLocalizationPrefs_LocalizationMode,
				etLocalizationPrefs_TimeTriggerInSecs,
				etLocalizationPrefs_DistanceTriggerInMtr,
				etLocalizationPrefs_LocationAccuracyRequiredInMtr,
				etLocalizationPrefs_DistBtwTwoLocsForDistCalcRequiredInCMtr));
		
        btLocalizationPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
