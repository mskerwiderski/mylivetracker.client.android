package de.msk.mylivetracker.client.android.httpprotocolparams;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;
import de.msk.mylivetracker.client.android.util.validation.ValidatorUtils;

/**
 * classname: HttpProtocolParamsPrefsActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class HttpProtocolParamsPrefsActivity extends AbstractActivity {

	private static final class OnHttpParameterItemSelectedListener implements OnItemSelectedListener {
		private HttpProtocolParamsPrefsActivity activity;
		private EditText etHttpProtocolParamsPrefs_ParameterExample;
		private EditText etHttpProtocolParamsPrefs_ParameterName;
		private CheckBox cbHttpProtocolParamsPrefs_EnabledForUploading;
		
		public OnHttpParameterItemSelectedListener(
			HttpProtocolParamsPrefsActivity activity,
			EditText etHttpProtocolParamsPrefs_ParameterExample,
			EditText etHttpProtocolParamsPrefs_ParameterName,
			CheckBox cbHttpProtocolParamsPrefs_EnabledForUploading) {
			this.activity = activity;
			this.etHttpProtocolParamsPrefs_ParameterExample = etHttpProtocolParamsPrefs_ParameterExample;
			this.etHttpProtocolParamsPrefs_ParameterName = etHttpProtocolParamsPrefs_ParameterName;
			this.cbHttpProtocolParamsPrefs_EnabledForUploading = cbHttpProtocolParamsPrefs_EnabledForUploading;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
			int position, long rowId) {
			LogUtils.infoMethodIn(HttpProtocolParamsPrefsActivity.class, "onItemSelected", position);
			boolean valid = true;
			if (position != currSelectedId) {
				valid = 
					this.activity.updateHttpProtocolParamDsc(
						this.activity,
						this.etHttpProtocolParamsPrefs_ParameterName,
						this.cbHttpProtocolParamsPrefs_EnabledForUploading);
			}
			if (valid) {
				currSelectedId = position;
				updateFields(
					etHttpProtocolParamsPrefs_ParameterExample, 
					etHttpProtocolParamsPrefs_ParameterName, 
					cbHttpProtocolParamsPrefs_EnabledForUploading);
			}
			LogUtils.infoMethodOut(HttpProtocolParamsPrefsActivity.class, "onItemSelected", currSelectedId);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// noop.
		}
	}
	
	private static final class OnClickButtonResetToDefaultsListener extends ASafeOnClickListener {
		private HttpProtocolParamsPrefsActivity activity;
		private Spinner spHttpProtocolParamsPrefs_Parameter;
		
		private OnClickButtonResetToDefaultsListener(HttpProtocolParamsPrefsActivity activity,
			Spinner spHttpProtocolParamsPrefs_Parameter) {
			this.activity = activity;
			this.spHttpProtocolParamsPrefs_Parameter = spHttpProtocolParamsPrefs_Parameter;
		}

		@Override
		public void onClick() {			
			ResetToDefaultsDialog dlg = new ResetToDefaultsDialog(
				this.activity,
				spHttpProtocolParamsPrefs_Parameter);
			dlg.show();		
		}		
	}
	
	private static final class ResetToDefaultsDialog extends AbstractYesNoDialog {
		private Activity activity;
		private Spinner spHttpProtocolParamsPrefs_Parameter;
				
		public ResetToDefaultsDialog(Activity activity,
			Spinner spHttpProtocolParamsPrefs_Parameter) {
			super(activity, R.string.txHttpProtocolParamsPrefs_QuestionResetToDefaults);
			this.activity = activity;
			this.spHttpProtocolParamsPrefs_Parameter = spHttpProtocolParamsPrefs_Parameter;
		}

		@Override
		public void onYes() {			
			HttpProtocolParamsPrefs prefs = PrefsRegistry.get(HttpProtocolParamsPrefs.class);
			prefs.setHttpProtocolParams(HttpProtocolParams.create());
			httpProtocolParamsCopy = prefs.getHttpProtocolParams().copy();
			currSelectedId = 0;
			spHttpProtocolParamsPrefs_Parameter.setSelection(currSelectedId);
			SimpleInfoDialog.show(this.activity, 
				R.string.txHttpProtocolParamsPrefs_InfoResetToDefaultsDone);
		}	
	}
	
	private static final class OnClickButtonSaveListener extends ASafeOnClickListener {
		private HttpProtocolParamsPrefsActivity activity;
		private EditText etHttpProtocolParamsPrefs_ParameterName;
		private CheckBox cbHttpProtocolParamsPrefs_EnabledForUploading;
		
		public OnClickButtonSaveListener(
			HttpProtocolParamsPrefsActivity activity,
			EditText etHttpProtocolParamsPrefs_ParameterName,
			CheckBox cbHttpProtocolParamsPrefs_EnabledForUploading) {
			this.activity = activity;
			this.etHttpProtocolParamsPrefs_ParameterName = etHttpProtocolParamsPrefs_ParameterName;
			this.cbHttpProtocolParamsPrefs_EnabledForUploading = cbHttpProtocolParamsPrefs_EnabledForUploading;
		}

		@Override
		public void onClick() {
			boolean valid = 
				this.activity.updateHttpProtocolParamDsc(
					this.activity,
					this.etHttpProtocolParamsPrefs_ParameterName,
					this.cbHttpProtocolParamsPrefs_EnabledForUploading);
			if (valid) {
				PrefsRegistry.get(HttpProtocolParamsPrefs.class).
					setHttpProtocolParams(httpProtocolParamsCopy);
				this.activity.finish();
			}			
		}		
	}
	
	private volatile static int currSelectedId = 0;
	private volatile static HttpProtocolParams httpProtocolParamsCopy = null;
	
	public static void updateFields(
		EditText etHttpProtocolParamsPrefs_ParameterExample,
		EditText etHttpProtocolParamsPrefs_ParameterName,
		CheckBox cbHttpProtocolParamsPrefs_EnabledForUploading) {
		HttpProtocolParamDsc paramDsc = httpProtocolParamsCopy.getParamDsc(currSelectedId);
		etHttpProtocolParamsPrefs_ParameterExample.setText(paramDsc.getExample());
		etHttpProtocolParamsPrefs_ParameterName.setText(paramDsc.getName());
		cbHttpProtocolParamsPrefs_EnabledForUploading.setChecked(paramDsc.isEnabled());
		if (paramDsc.isDisableAllowed()) {
			cbHttpProtocolParamsPrefs_EnabledForUploading.setClickable(true);
			cbHttpProtocolParamsPrefs_EnabledForUploading.setFocusable(true);
			cbHttpProtocolParamsPrefs_EnabledForUploading.setEnabled(true);
		} else {
			cbHttpProtocolParamsPrefs_EnabledForUploading.setClickable(false);
			cbHttpProtocolParamsPrefs_EnabledForUploading.setFocusable(false);
			cbHttpProtocolParamsPrefs_EnabledForUploading.setEnabled(false);
		}
	}
	
	public boolean updateHttpProtocolParamDsc(
		HttpProtocolParamsPrefsActivity activity,
		EditText etHttpProtocolParamsPrefs_ParameterName,
		CheckBox cbHttpProtocolParamsPrefs_EnabledForUploading) {
		LogUtils.infoMethodIn(HttpProtocolParamsPrefsActivity.class, "updateHttpProtocolParamDsc");
		String paramName = etHttpProtocolParamsPrefs_ParameterName.getText().toString();
		HttpProtocolParamDsc paramDsc = httpProtocolParamsCopy.getParamDsc(currSelectedId);
		boolean valid = ValidatorUtils.validateHttpParamName(
			activity, httpProtocolParamsCopy, 
			currSelectedId, etHttpProtocolParamsPrefs_ParameterName);
		if (valid) {
			paramDsc.setName(paramName);
			paramDsc.setEnabled(
				cbHttpProtocolParamsPrefs_EnabledForUploading.isChecked());
		}
		LogUtils.infoMethodOut(HttpProtocolParamsPrefsActivity.class, "updateHttpProtocolParamDsc", "updated", currSelectedId, paramDsc);
		return valid;
	}
	
	@Override
	protected void onStart() {
		httpProtocolParamsCopy = PrefsRegistry.get(HttpProtocolParamsPrefs.class).
			getHttpProtocolParams().copy();		
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
        setContentView(R.layout.http_protocol_params_prefs);
     
        this.setTitle(R.string.tiHttpProtocolParamsPrefs);
        
        EditText etHttpProtocolParamsPrefs_ParameterExample = (EditText)
    		findViewById(R.id.etHttpProtocolParamsPrefs_ParameterExample);
        etHttpProtocolParamsPrefs_ParameterExample.setEnabled(false);
        etHttpProtocolParamsPrefs_ParameterExample.setFocusable(false);
        etHttpProtocolParamsPrefs_ParameterExample.setClickable(false);
        
        Spinner spHttpProtocolParamsPrefs_Parameter = (Spinner)
        	findViewById(R.id.spHttpProtocolParamsPrefs_Parameter);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(
            this, R.array.httpProtocolParams, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spHttpProtocolParamsPrefs_Parameter.setAdapter(adapter);
        spHttpProtocolParamsPrefs_Parameter.setSelection(currSelectedId);
        
        EditText etHttpProtocolParamsPrefs_ParameterName = (EditText)
        	findViewById(R.id.etHttpProtocolParamsPrefs_ParameterName);
        CheckBox cbHttpProtocolParamsPrefs_EnabledForUploading = (CheckBox)
        	findViewById(R.id.cbHttpProtocolParamsPrefs_EnabledForUploading);
        
        Button btHttpProtocolParamsPrefs_ResetToDefaults = (Button)
        	findViewById(R.id.btHttpProtocolParamsPrefs_ResetToDefaults);
        Button btHttpProtocolParamsPrefs_Save = (Button)
        	findViewById(R.id.btHttpProtocolParamsPrefs_Save);
        Button btHttpProtocolParamsPrefs_Cancel = (Button)
        	findViewById(R.id.btHttpProtocolParamsPrefs_Cancel);
           
        spHttpProtocolParamsPrefs_Parameter.setOnItemSelectedListener(
    		new OnHttpParameterItemSelectedListener(this, 
				etHttpProtocolParamsPrefs_ParameterExample,
				etHttpProtocolParamsPrefs_ParameterName,
				cbHttpProtocolParamsPrefs_EnabledForUploading));
        btHttpProtocolParamsPrefs_ResetToDefaults.setOnClickListener(
			new OnClickButtonResetToDefaultsListener(this,
				spHttpProtocolParamsPrefs_Parameter));
        btHttpProtocolParamsPrefs_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, 
				etHttpProtocolParamsPrefs_ParameterName,	
				cbHttpProtocolParamsPrefs_EnabledForUploading));		
        btHttpProtocolParamsPrefs_Cancel.setOnClickListener(
			new OnFinishActivityListener(this));
    }
}
