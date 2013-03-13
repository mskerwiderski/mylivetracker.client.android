package de.msk.mylivetracker.client.android.other;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.antplus.AntPlusHardware;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.other.OtherPrefs.ConfirmLevel;
import de.msk.mylivetracker.client.android.other.OtherPrefs.TrackingOneTouchMode;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;

/**
 * classname: OtherPrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OtherPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private OtherPrefsActivity activity;
		private Spinner spOtherPrefs_TrackingOneTouch;
		private Spinner spOtherPrefs_ConfirmLevel;
		private CheckBox cbOtherPrefs_AdaptButtonsForOneTouchMode;
		private CheckBox cbOtherPrefs_EnableAntPlusIfAvailable;
		
		public OnClickButtonSaveListener(
			OtherPrefsActivity activity,
			Spinner spOtherPrefs_TrackingOneTouch,
			Spinner spOtherPrefs_ConfirmLevel,
			CheckBox cbOtherPrefs_AdaptButtonsForOneTouchMode,
			CheckBox cbOtherPrefs_EnableAntPlusIfAvailable) {
			this.activity = activity;
			this.spOtherPrefs_TrackingOneTouch = spOtherPrefs_TrackingOneTouch;
			this.spOtherPrefs_ConfirmLevel = spOtherPrefs_ConfirmLevel;
			this.cbOtherPrefs_AdaptButtonsForOneTouchMode = cbOtherPrefs_AdaptButtonsForOneTouchMode;
			this.cbOtherPrefs_EnableAntPlusIfAvailable = cbOtherPrefs_EnableAntPlusIfAvailable;
		}

		@Override
		public void onClick() {
			boolean valid = true;
			if (valid) {
				OtherPrefs prefs = PrefsRegistry.get(OtherPrefs.class);
				prefs.setTrackingOneTouchMode(TrackingOneTouchMode.values()[
                    spOtherPrefs_TrackingOneTouch.getSelectedItemPosition()]);
				prefs.setConfirmLevel(ConfirmLevel.values()[
                    spOtherPrefs_ConfirmLevel.getSelectedItemPosition()]);
				prefs.setAdaptButtonsForOneTouchMode(
					cbOtherPrefs_AdaptButtonsForOneTouchMode.isChecked());
				prefs.setAntPlusEnabledIfAvailable(
					cbOtherPrefs_EnableAntPlusIfAvailable.isChecked());
				PrefsRegistry.save(OtherPrefs.class);
				this.activity.finish();
			}			
		}		
	}
	
	private static final class AppResetDialog extends AbstractYesNoDialog {

		private Activity activity;
				
		public AppResetDialog(Activity activity) {
			super(activity, R.string.txOtherPrefs_QuestionResetToFactoryDefaults);
			this.activity = activity;			
		}

		@Override
		public void onYes() {
			TrackStatus.reset();
			TrackStatus.resetMileage();
			PrefsRegistry.reset();
			SimpleInfoDialog.show(this.activity, 
				R.string.txOtherPrefs_InfoResetToFactoryDefaultsDone);
		}	
	}
	
	private static final class OnClickButtonAppReset implements OnClickListener {
		private Activity activity;
		
		private OnClickButtonAppReset(Activity activity) {
			this.activity = activity;					
		}
		
		@Override
		public void onClick(View view) {		
			AppResetDialog dlg = new AppResetDialog(this.activity);
			dlg.show();			
		}		
	}

	private static final class MileageResetDialog extends AbstractYesNoDialog {

		private Activity activity;
				
		public MileageResetDialog(Activity activity) {
			super(activity, R.string.txOtherPrefs_QuestionResetOverallMileage);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			TrackStatus.resetMileage();
			Toast.makeText(activity.getApplicationContext(), 
				activity.getString(R.string.txOtherPrefs_InfoResetOverallMileageDone), 
				Toast.LENGTH_SHORT).show();
		}	
	}
	
	private static final class OnClickButtonMileageReset implements OnClickListener {
		private Activity activity;
		
		private OnClickButtonMileageReset(Activity activity) {
			this.activity = activity;					
		}
		
		@Override
		public void onClick(View view) {		
			MileageResetDialog dlg = new MileageResetDialog(this.activity);
			dlg.show();			
		}		
	}	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_prefs);
     
        this.setTitle(R.string.tiOtherPrefs);
        
        OtherPrefs prefs = PrefsRegistry.get(OtherPrefs.class);
        
        Spinner spOtherPrefs_ConfirmLevel = (Spinner)findViewById(R.id.spOtherPrefs_ConfirmLevel);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.confirmLevels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOtherPrefs_ConfirmLevel.setAdapter(adapter);
        spOtherPrefs_ConfirmLevel.setSelection(prefs.getConfirmLevel().ordinal());
        
        Spinner spOtherPrefs_TrackingOneTouch = (Spinner)findViewById(R.id.spOtherPrefs_TrackingOneTouch);
        adapter = ArrayAdapter.createFromResource(
            this, R.array.trackingOneTouchModes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOtherPrefs_TrackingOneTouch.setAdapter(adapter);
        spOtherPrefs_TrackingOneTouch.setSelection(prefs.getTrackingOneTouchMode().ordinal());
        
        CheckBox cbOtherPrefs_AdaptButtonsForOneTouchMode = (CheckBox)findViewById(R.id.cbOtherPrefs_AdaptButtonsForOneTouchMode);
        cbOtherPrefs_AdaptButtonsForOneTouchMode.setChecked(prefs.isAdaptButtonsForOneTouchMode());
        
        CheckBox cbOtherPrefs_EnableAntPlusIfAvailable = (CheckBox)findViewById(R.id.cbOtherPrefs_EnableAntPlusIfAvailable);
        if (!AntPlusHardware.supported()) {
        	cbOtherPrefs_EnableAntPlusIfAvailable.setVisibility(View.GONE);
        } else {
	        cbOtherPrefs_EnableAntPlusIfAvailable.setChecked(prefs.isAntPlusEnabledIfAvailable());
        }
        
        Button btOtherPrefs_ResetToFactoryDefaults = (Button)findViewById(R.id.btOtherPrefs_ResetToFactoryDefaults);
        Button btOtherPrefs_ResetOverallMileage = (Button)findViewById(R.id.btOtherPrefs_ResetOverallMileage);
        Button btnOtherPrefs_Save = (Button) findViewById(R.id.btOtherPrefs_Save);
        
        // Liontrack customization.
        Button btnOtherPrefs_Back = (Button) findViewById(R.id.btOtherPrefs_Back);
         
        btOtherPrefs_ResetToFactoryDefaults.setOnClickListener(
			new OnClickButtonAppReset(this));
        btOtherPrefs_ResetOverallMileage.setOnClickListener(
			new OnClickButtonMileageReset(this));
        btnOtherPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				spOtherPrefs_TrackingOneTouch,
				spOtherPrefs_ConfirmLevel,
				cbOtherPrefs_AdaptButtonsForOneTouchMode,
				cbOtherPrefs_EnableAntPlusIfAvailable));	
        
        // Liontrack customization.
        btnOtherPrefs_Back.setOnClickListener(
			new OnFinishActivityListener(this));
        
        // Liontrack customization.
        btnOtherPrefs_Save.setVisibility(View.GONE);
        spOtherPrefs_TrackingOneTouch.setVisibility(View.GONE);
        spOtherPrefs_ConfirmLevel.setVisibility(View.GONE);
        cbOtherPrefs_AdaptButtonsForOneTouchMode.setVisibility(View.GONE);
        cbOtherPrefs_EnableAntPlusIfAvailable.setVisibility(View.GONE);
        TextView tvOtherPrefs_ConfirmLevel = (TextView)findViewById(R.id.tvOtherPrefs_ConfirmLevel);
        TextView tvOtherPrefs_TrackingOneTouch = (TextView)findViewById(R.id.tvOtherPrefs_TrackingOneTouch);
        tvOtherPrefs_ConfirmLevel.setVisibility(View.GONE);
        tvOtherPrefs_TrackingOneTouch.setVisibility(View.GONE);
    }
}
