package de.msk.mylivetracker.client.android.other;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.other.OtherPrefs.ConfirmLevel;
import de.msk.mylivetracker.client.android.other.OtherPrefs.TrackingOneTouchMode;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * OtherPrefsActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 002
 * 
 * history
 * 002	2012-12-25 	revised for v1.5.x.
 * 001  2011-02-18
 * 		o phoneNumber removed.
 * 		o trackinOneTouch added.
 * 000 	2011-08-11 	initial.
 * 
 */
public class OtherPrefsActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private OtherPrefsActivity activity;
		private Spinner spOtherPrefs_TrackingOneTouch;
		private Spinner spOtherPrefs_ConfirmLevel;
		
		public OnClickButtonSaveListener(
			OtherPrefsActivity activity,
			Spinner spOtherPrefs_TrackingOneTouch,
			Spinner spOtherPrefs_ConfirmLevel) {
			this.activity = activity;
			this.spOtherPrefs_TrackingOneTouch = spOtherPrefs_TrackingOneTouch;
			this.spOtherPrefs_ConfirmLevel = spOtherPrefs_ConfirmLevel;
		}

		@Override
		public void onClick(View v) {
			boolean valid = true;
				
			if (valid) {
				OtherPrefs prefs = PrefsRegistry.get(OtherPrefs.class);
				prefs.setTrackingOneTouchMode(TrackingOneTouchMode.values()[spOtherPrefs_TrackingOneTouch.getSelectedItemPosition()]);
				prefs.setConfirmLevel(ConfirmLevel.values()[spOtherPrefs_ConfirmLevel.getSelectedItemPosition()]);
				PrefsRegistry.save(OtherPrefs.class);
				this.activity.finish();
			}			
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private OtherPrefsActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(OtherPrefsActivity activity) {
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
		
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
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
        
        Button btOtherPrefs_ResetToFactoryDefaults = (Button)findViewById(R.id.btOtherPrefs_ResetToFactoryDefaults);
        Button btOtherPrefs_ResetOverallMileage = (Button)findViewById(R.id.btOtherPrefs_ResetOverallMileage);
        Button btnOtherPrefs_Save = (Button) findViewById(R.id.btOtherPrefs_Save);
        Button btnOtherPrefs_Cancel = (Button) findViewById(R.id.btOtherPrefs_Cancel);
         
        btOtherPrefs_ResetToFactoryDefaults.setOnClickListener(
			new OnClickButtonAppReset(this));
        btOtherPrefs_ResetOverallMileage.setOnClickListener(
			new OnClickButtonMileageReset(this));
        btnOtherPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				spOtherPrefs_TrackingOneTouch,
				spOtherPrefs_ConfirmLevel));		
        btnOtherPrefs_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
