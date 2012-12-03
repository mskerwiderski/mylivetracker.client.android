package de.msk.mylivetracker.client.android.preferences;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * PrefsHttpProtocolParamsActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-11-30 initial.
 * 
 */
public class PrefsHttpProtocolParamsActivity extends AbstractActivity {

	private static final class OnHttpParameterItemSelectedListener implements OnItemSelectedListener {
		private PrefsHttpProtocolParamsActivity activity;
		private EditText etPrefsHttpProtocolParams_ParameterExample;
		private EditText etPrefsHttpProtocolParams_ParameterName;
		private CheckBox cbPrefsHttpProtocolParams_EnabledForUploading;
		
		public OnHttpParameterItemSelectedListener(
			PrefsHttpProtocolParamsActivity activity,
			EditText etPrefsHttpProtocolParams_ParameterExample,
			EditText etPrefsHttpProtocolParams_ParameterName,
			CheckBox cbPrefsHttpProtocolParams_EnabledForUploading) {
			this.activity = activity;
			this.etPrefsHttpProtocolParams_ParameterExample = etPrefsHttpProtocolParams_ParameterExample;
			this.etPrefsHttpProtocolParams_ParameterName = etPrefsHttpProtocolParams_ParameterName;
			this.cbPrefsHttpProtocolParams_EnabledForUploading = cbPrefsHttpProtocolParams_EnabledForUploading;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
			int position, long rowId) {
			LogUtils.infoMethodIn(PrefsHttpProtocolParamsActivity.class, "onItemSelected", position);
			boolean valid = true;
			if (position != currSelectedId) {
				valid = 
					this.activity.updateHttpProtocolParamDsc(
						this.activity,
						this.etPrefsHttpProtocolParams_ParameterName,
						this.cbPrefsHttpProtocolParams_EnabledForUploading);
			}
			if (valid) {
				currSelectedId = position;
				updateFields(
					etPrefsHttpProtocolParams_ParameterExample, 
					etPrefsHttpProtocolParams_ParameterName, 
					cbPrefsHttpProtocolParams_EnabledForUploading);
			}
			LogUtils.infoMethodOut(PrefsHttpProtocolParamsActivity.class, "onItemSelected", currSelectedId);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// noop.
		}
	}
	
	private static final class OnClickButtonResetToDefaultsListener implements OnClickListener {
		private PrefsHttpProtocolParamsActivity activity;
		private Preferences preferences;
		private Spinner spPrefsHttpProtocolParams_Parameter;
		
		private OnClickButtonResetToDefaultsListener(PrefsHttpProtocolParamsActivity activity,
			Preferences preferences,
			Spinner spPrefsHttpProtocolParams_Parameter) {
			this.activity = activity;
			this.preferences = preferences;
			this.spPrefsHttpProtocolParams_Parameter = spPrefsHttpProtocolParams_Parameter;
		}

		@Override
		public void onClick(View v) {			
			ResetToDefaultsDialog dlg = new ResetToDefaultsDialog(
				this.activity,
				this.preferences,
				spPrefsHttpProtocolParams_Parameter);
			dlg.show();		
		}		
	}
	
	private static final class ResetToDefaultsDialog extends AbstractYesNoDialog {
		private Activity activity;
		private Preferences preferences;
		private Spinner spPrefsHttpProtocolParams_Parameter;
				
		public ResetToDefaultsDialog(Activity activity,
			Preferences preferences,	
			Spinner spPrefsHttpProtocolParams_Parameter) {
			super(activity, R.string.txPrefsHttpProtocolParams_QuestionResetToDefaults);
			this.activity = activity;
			this.preferences = preferences;
			this.spPrefsHttpProtocolParams_Parameter = spPrefsHttpProtocolParams_Parameter;
		}

		@Override
		public void onYes() {			
			this.preferences.setHttpProtocolParams(HttpProtocolParams.create());
			httpProtocolParamsCopy = this.preferences.getHttpProtocolParams().copy();
			currSelectedId = 0;
			spPrefsHttpProtocolParams_Parameter.setSelection(currSelectedId);
			SimpleInfoDialog dlg = new SimpleInfoDialog(
				this.activity, R.string.txPrefsHttpProtocolParams_InfoResetToDefaultsDone);
			dlg.show();
		}	
	}
	
	private static final class OnClickButtonSaveListener implements OnClickListener {
		private PrefsHttpProtocolParamsActivity activity;
		private Preferences preferences;
		private EditText etPrefsHttpProtocolParams_ParameterName;
		private CheckBox cbPrefsHttpProtocolParams_EnabledForUploading;
		
		public OnClickButtonSaveListener(
			PrefsHttpProtocolParamsActivity activity,
			Preferences preferences,
			EditText etPrefsHttpProtocolParams_ParameterName,
			CheckBox cbPrefsHttpProtocolParams_EnabledForUploading) {
			this.activity = activity;
			this.preferences = preferences;
			this.etPrefsHttpProtocolParams_ParameterName = etPrefsHttpProtocolParams_ParameterName;
			this.cbPrefsHttpProtocolParams_EnabledForUploading = cbPrefsHttpProtocolParams_EnabledForUploading;
		}

		@Override
		public void onClick(View v) {
			boolean valid = 
				this.activity.updateHttpProtocolParamDsc(
					this.activity,
					this.etPrefsHttpProtocolParams_ParameterName,
					this.cbPrefsHttpProtocolParams_EnabledForUploading);
			if (valid) {
				this.preferences.setHttpProtocolParams(httpProtocolParamsCopy);
				this.activity.finish();
			}			
		}		
	}
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private PrefsHttpProtocolParamsActivity activity;
		
		private OnClickButtonCancelListener(PrefsHttpProtocolParamsActivity activity) {
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	private volatile static int currSelectedId = 0;
	private volatile static HttpProtocolParams httpProtocolParamsCopy = null;
	
	public static void updateFields(
		EditText etPrefsHttpProtocolParams_ParameterExample,
		EditText etPrefsHttpProtocolParams_ParameterName,
		CheckBox cbPrefsHttpProtocolParams_EnabledForUploading) {
		HttpProtocolParamDsc paramDsc = httpProtocolParamsCopy.getParamDsc(currSelectedId);
		etPrefsHttpProtocolParams_ParameterExample.setText(paramDsc.getExample());
		etPrefsHttpProtocolParams_ParameterName.setText(paramDsc.getName());
		cbPrefsHttpProtocolParams_EnabledForUploading.setChecked(paramDsc.isEnabled());
	}
	
	public boolean updateHttpProtocolParamDsc(
		PrefsHttpProtocolParamsActivity activity,
		EditText etPrefsHttpProtocolParams_ParameterName,
		CheckBox cbPrefsHttpProtocolParams_EnabledForUploading) {
		LogUtils.infoMethodIn(PrefsHttpProtocolParamsActivity.class, "updateHttpProtocolParamDsc");
		String paramName = etPrefsHttpProtocolParams_ParameterName.getText().toString();
		HttpProtocolParamDsc paramDsc = httpProtocolParamsCopy.getParamDsc(currSelectedId);
		boolean valid = ValidatorUtils.validateHttpParamName(
			activity, httpProtocolParamsCopy, 
			currSelectedId, etPrefsHttpProtocolParams_ParameterName);
		if (valid) {
			paramDsc.setName(paramName);
			paramDsc.setEnabled(
				cbPrefsHttpProtocolParams_EnabledForUploading.isChecked());
		}
		LogUtils.infoMethodOut(PrefsHttpProtocolParamsActivity.class, "updateHttpProtocolParamDsc", "updated", currSelectedId, paramDsc);
		return valid;
	}
	
	@Override
	protected void onStart() {
		httpProtocolParamsCopy = Preferences.get().getHttpProtocolParams().copy();		
		super.onStart();
	}

	@Override
	protected void onStop() {
		httpProtocolParamsCopy = null;
		super.onStop();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_http_protocol_params);
     
        this.setTitle(R.string.tiPrefsHttpProtocolParams);
        
        Preferences prefs = Preferences.get();
        
        EditText etPrefsHttpProtocolParams_ParameterExample = (EditText)findViewById(R.id.etPrefsHttpProtocolParams_ParameterExample);
        etPrefsHttpProtocolParams_ParameterExample.setEnabled(false);
        etPrefsHttpProtocolParams_ParameterExample.setFocusable(false);
        etPrefsHttpProtocolParams_ParameterExample.setClickable(false);
        
        Spinner spPrefsHttpProtocolParams_Parameter = (Spinner)findViewById(R.id.spPrefsHttpProtocolParams_Parameter);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.httpProtocolParams, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrefsHttpProtocolParams_Parameter.setAdapter(adapter);
        spPrefsHttpProtocolParams_Parameter.setSelection(currSelectedId);
        
        EditText etPrefsHttpProtocolParams_ParameterName = (EditText)findViewById(R.id.etPrefsHttpProtocolParams_ParameterName);
        CheckBox cbPrefsHttpProtocolParams_EnabledForUploading = (CheckBox)findViewById(R.id.cbPrefsHttpProtocolParams_EnabledForUploading);
        
        Button btPrefsHttpProtocolParams_ResetToDefaults = (Button)findViewById(R.id.btPrefsHttpProtocolParams_ResetToDefaults);
        Button btPrefsHttpProtocolParams_Save = (Button)findViewById(R.id.btPrefsHttpProtocolParams_Save);
        Button btPrefsHttpProtocolParams_Cancel = (Button)findViewById(R.id.btPrefsHttpProtocolParams_Cancel);
           
        spPrefsHttpProtocolParams_Parameter.setOnItemSelectedListener(
    		new OnHttpParameterItemSelectedListener(this, 
				etPrefsHttpProtocolParams_ParameterExample,
				etPrefsHttpProtocolParams_ParameterName,
				cbPrefsHttpProtocolParams_EnabledForUploading));
        btPrefsHttpProtocolParams_ResetToDefaults.setOnClickListener(
			new OnClickButtonResetToDefaultsListener(this,
				prefs,
				spPrefsHttpProtocolParams_Parameter));
        btPrefsHttpProtocolParams_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, prefs, 
				etPrefsHttpProtocolParams_ParameterName,	
				cbPrefsHttpProtocolParams_EnabledForUploading));		
        btPrefsHttpProtocolParams_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
