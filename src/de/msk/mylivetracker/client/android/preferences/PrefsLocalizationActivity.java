package de.msk.mylivetracker.client.android.preferences;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences.LocalizationMode;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * PrefsLocalizationActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001 	2012-02-04 localizationModes implemented (gps, network, gpsAndNetwork).
 * 000 	2011-08-11 initial.
 * 
 */
public class PrefsLocalizationActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private PrefsLocalizationActivity activity;
		private Preferences preferences;
		private Spinner spPrefsLocalization_LocalizationMode;
		private EditText etPrefsLocalization_TimeTriggerInSecs;
		private EditText etPrefsLocalization_DistanceTriggerInMtr;
		private EditText etPrefsLocalization_LocationAccuracyRequiredInMtr;
		private EditText etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr;
		
		public OnClickButtonSaveListener(
			PrefsLocalizationActivity activity,
			Preferences preferences,
			Spinner spPrefsLocalization_LocalizationMode,
			EditText etPrefsLocalization_TimeTriggerInSecs,
			EditText etPrefsLocalization_DistanceTriggerInMtr,
			EditText etPrefsLocalization_LocationAccuracyRequiredInMtr,
			EditText etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr) {
			this.activity = activity;
			this.preferences = preferences;
			this.spPrefsLocalization_LocalizationMode = spPrefsLocalization_LocalizationMode;
			this.etPrefsLocalization_TimeTriggerInSecs = etPrefsLocalization_TimeTriggerInSecs;
			this.etPrefsLocalization_DistanceTriggerInMtr = etPrefsLocalization_DistanceTriggerInMtr;
			this.etPrefsLocalization_LocationAccuracyRequiredInMtr = etPrefsLocalization_LocationAccuracyRequiredInMtr;
			this.etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr = etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr;
		}

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			boolean valid = true;
			
			LocalizationMode localizationMode =
				LocalizationMode.values()[spPrefsLocalization_LocalizationMode.getSelectedItemPosition()];
			String localizationModeDisplayName = 
				this.activity.getResources().getStringArray(R.array.localizationModes)[spPrefsLocalization_LocalizationMode.getSelectedItemPosition()];
			valid = valid && 
				ValidatorUtils.validateIfLocalizationModeIsSupported(
					this.activity,
					localizationMode,
					localizationModeDisplayName) &&	
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdPrefsLocalization_TimeTriggerInSecs, 
					etPrefsLocalization_TimeTriggerInSecs, 
					true, 
					0, 3600, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdPrefsLocalization_DistanceTriggerInMtr, 
					etPrefsLocalization_DistanceTriggerInMtr, 
					true, 
					0, 10000, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdPrefsLocalization_LocationAccuracyRequiredInMtr, 
					etPrefsLocalization_LocationAccuracyRequiredInMtr, 
					true, 
					0, 1000, true) &&
				ValidatorUtils.validateEditTextNumber(
					this.activity, 
					R.string.fdPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr, 
					etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr, 
					true, 
					0, 10000, true);			
			
			if (valid) {
				preferences.setLocalizationMode(localizationMode);
				preferences.setLocTimeTriggerInSeconds(
					Integer.parseInt(etPrefsLocalization_TimeTriggerInSecs.getText().toString()));
				preferences.setLocDistanceTriggerInMeter(
					Integer.parseInt(etPrefsLocalization_DistanceTriggerInMtr.getText().toString()));
				preferences.setLocAccuracyRequiredInMeter(
					Integer.parseInt(etPrefsLocalization_LocationAccuracyRequiredInMtr.getText().toString()));
				preferences.setLocDistBtwTwoLocsForDistCalcRequiredInCMtr(
					Integer.parseInt(etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr.getText().toString()));
				Preferences.save();
				this.activity.finish();
			}
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PrefsLocalizationActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(PrefsLocalizationActivity activity) {
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
        setContentView(R.layout.preferences_localization);
     
        this.setTitle(R.string.tiPrefsLocalization);
        
        Preferences prefs = Preferences.get();
        
        Spinner spPrefsLocalization_LocalizationMode = (Spinner) findViewById(R.id.spPrefsLocalization_LocalizationMode);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
                this, R.array.localizationModes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrefsLocalization_LocalizationMode.setAdapter(adapter);
        spPrefsLocalization_LocalizationMode.setSelection(prefs.getLocalizationMode().ordinal());     
        
        EditText etPrefsLocalization_TimeTriggerInSecs = (EditText)findViewById(R.id.etPrefsLocalization_TimeTriggerInSecs);
        etPrefsLocalization_TimeTriggerInSecs.setText(String.valueOf(prefs.getLocTimeTriggerInSeconds()));
        EditText etPrefsLocalization_DistanceTriggerInMtr = (EditText)findViewById(R.id.etPrefsLocalization_DistanceTriggerInMtr);
        etPrefsLocalization_DistanceTriggerInMtr.setText(String.valueOf(prefs.getLocDistanceTriggerInMeter()));
        EditText etPrefsLocalization_LocationAccuracyRequiredInMtr = (EditText)findViewById(R.id.etPrefsLocalization_LocationAccuracyRequiredInMtr);
        etPrefsLocalization_LocationAccuracyRequiredInMtr.setText(String.valueOf(prefs.getLocAccuracyRequiredInMeter()));
        EditText etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr = (EditText)findViewById(R.id.etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr);
        etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr.setText(String.valueOf(prefs.getLocDistBtwTwoLocsForDistCalcRequiredInCMtr()));
        
    	Button btPrefsLocalization_Save = (Button) findViewById(R.id.btPrefsLocalization_Save);
        Button btPrefsLocalization_Cancel = (Button) findViewById(R.id.btPrefsLocalization_Cancel);
                
        btPrefsLocalization_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, prefs,
				spPrefsLocalization_LocalizationMode,
				etPrefsLocalization_TimeTriggerInSecs,
				etPrefsLocalization_DistanceTriggerInMtr,
				etPrefsLocalization_LocationAccuracyRequiredInMtr,
				etPrefsLocalization_DistBtwTwoLocsForDistCalcRequiredInCMtr));
		
        btPrefsLocalization_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
