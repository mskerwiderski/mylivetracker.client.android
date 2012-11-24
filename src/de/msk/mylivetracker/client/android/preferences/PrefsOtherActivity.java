package de.msk.mylivetracker.client.android.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences.BufferSize;
import de.msk.mylivetracker.client.android.preferences.Preferences.ConfirmLevel;
import de.msk.mylivetracker.client.android.preferences.Preferences.TrackingOneTouchMode;
import de.msk.mylivetracker.client.android.status.PositionBufferInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * PrefsOtherActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001  2011-02-18
 * 		o phoneNumber removed.
 * 		o trackinOneTouch added.
 * 000 	2011-08-11 initial.
 * 
 */
public class PrefsOtherActivity extends AbstractActivity {

	private static final class OnClickButtonSaveListener implements OnClickListener {
		private PrefsOtherActivity activity;
		private Preferences preferences;
		private Spinner spPrefsOther_MaxPositionBufferSize;
		private Spinner spPrefsOther_TrackingOneTouch;
		private Spinner spPrefsOther_ConfirmLevel;
		
		public OnClickButtonSaveListener(
			PrefsOtherActivity activity,
			Preferences preferences,
			Spinner spPrefsOther_MaxPositionBufferSize,
			Spinner spPrefsOther_TrackingOneTouch,
			Spinner spPrefsOther_ConfirmLevel) {
			this.activity = activity;
			this.preferences = preferences;
			this.spPrefsOther_MaxPositionBufferSize = spPrefsOther_MaxPositionBufferSize;
			this.spPrefsOther_TrackingOneTouch = spPrefsOther_TrackingOneTouch;
			this.spPrefsOther_ConfirmLevel = spPrefsOther_ConfirmLevel;
		}

		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View v) {
			boolean valid = true;
				
			if (valid) {
				preferences.setUplPositionBufferSize(BufferSize.values()[spPrefsOther_MaxPositionBufferSize.getSelectedItemPosition()]);
				if (preferences.getUplPositionBufferSize().isDisabled()) {
					PositionBufferInfo.reset();
				}
				preferences.setTrackingOneTouchMode(TrackingOneTouchMode.values()[spPrefsOther_TrackingOneTouch.getSelectedItemPosition()]);
				preferences.setConfirmLevel(ConfirmLevel.values()[spPrefsOther_ConfirmLevel.getSelectedItemPosition()]);
				Preferences.save();
				this.activity.finish();
			}			
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PrefsOtherActivity activity;
		
		/**
		 * @param aMain
		 */
		private OnClickButtonCancelListener(PrefsOtherActivity activity) {
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
			super(activity, R.string.txPrefsOther_QuestionResetToFactoryDefaults);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			Preferences.reset(this.activity);
			SimpleInfoDialog dlg = new SimpleInfoDialog(
				this.activity, R.string.txPrefsOther_InfoResetToFactoryDefaultsDone);
			dlg.show();
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
			super(activity, R.string.txPrefsOther_QuestionResetOverallMileage);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			TrackStatus.resetMileage();
			Toast.makeText(activity.getApplicationContext(), 
				activity.getString(R.string.txPrefsOther_InfoResetOverallMileageDone), 
				Toast.LENGTH_SHORT).show();
		}	
	}
	
	private static final class OnClickButtonMileageReset implements OnClickListener {
		private Activity activity;
		
		private OnClickButtonMileageReset(Activity activity) {
			this.activity = activity;					
		}
		
		/* (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View view) {		
			MileageResetDialog dlg = new MileageResetDialog(this.activity);
			dlg.show();			
		}		
	}	
    	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_other);
     
        this.setTitle(R.string.tiPrefsOther);
        
        Preferences prefs = Preferences.get();
        
        Spinner spPrefsOther_MaxPositionBufferSize = (Spinner)findViewById(R.id.spPrefsOther_MaxPositionBufferSize);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.bufferSizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrefsOther_MaxPositionBufferSize.setAdapter(adapter);
        spPrefsOther_MaxPositionBufferSize.setSelection(prefs.getUplPositionBufferSize().ordinal());
        
        Spinner spPrefsOther_ConfirmLevel = (Spinner)findViewById(R.id.spPrefsOther_ConfirmLevel);
        adapter = ArrayAdapter.createFromResource(
            this, R.array.confirmLevels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrefsOther_ConfirmLevel.setAdapter(adapter);
        spPrefsOther_ConfirmLevel.setSelection(prefs.getConfirmLevel().ordinal());
        
        Spinner spPrefsOther_TrackingOneTouch = (Spinner)findViewById(R.id.spPrefsOther_TrackingOneTouch);
        adapter = ArrayAdapter.createFromResource(
            this, R.array.trackingOneTouchModes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrefsOther_TrackingOneTouch.setAdapter(adapter);
        spPrefsOther_TrackingOneTouch.setSelection(prefs.getTrackingOneTouchMode().ordinal());
        
        Button btPrefsOther_ResetToFactoryDefaults = (Button)findViewById(R.id.btPrefsOther_ResetToFactoryDefaults);
        Button btPrefsOther_ResetOverallMileage = (Button)findViewById(R.id.btPrefsOther_ResetOverallMileage);      
        Button btnPrefsOther_Save = (Button) findViewById(R.id.btPrefsOther_Save);
        Button btnPrefsOther_Cancel = (Button) findViewById(R.id.btPrefsOther_Cancel);
                
        btPrefsOther_ResetToFactoryDefaults.setOnClickListener(
			new OnClickButtonAppReset(this));
        btPrefsOther_ResetOverallMileage.setOnClickListener(
			new OnClickButtonMileageReset(this));
        btnPrefsOther_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, prefs,
				spPrefsOther_MaxPositionBufferSize,
				spPrefsOther_TrackingOneTouch,
				spPrefsOther_ConfirmLevel));		
        btnPrefsOther_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
