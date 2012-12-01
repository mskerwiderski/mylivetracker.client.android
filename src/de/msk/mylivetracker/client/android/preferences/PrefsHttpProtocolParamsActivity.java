package de.msk.mylivetracker.client.android.preferences;

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
import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.util.LogUtils;

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
		private HttpProtocolParams httpProtocolParamsCopy;
		private EditText etPrefsHttpProtocolParams_ParameterExample;
		private EditText etPrefsHttpProtocolParams_ParameterName;
		private CheckBox cbPrefsHttpProtocolParams_EnabledForUploading;
		
		public OnHttpParameterItemSelectedListener(
			PrefsHttpProtocolParamsActivity activity,
			HttpProtocolParams httpProtocolParamsCopy,
			EditText etPrefsHttpProtocolParams_ParameterExample,
			EditText etPrefsHttpProtocolParams_ParameterName,
			CheckBox cbPrefsHttpProtocolParams_EnabledForUploading) {
			this.activity = activity;
			this.httpProtocolParamsCopy = httpProtocolParamsCopy;
			this.etPrefsHttpProtocolParams_ParameterExample = etPrefsHttpProtocolParams_ParameterExample;
			this.etPrefsHttpProtocolParams_ParameterName = etPrefsHttpProtocolParams_ParameterName;
			this.cbPrefsHttpProtocolParams_EnabledForUploading = cbPrefsHttpProtocolParams_EnabledForUploading;
		}
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
			int position, long rowId) {
			LogUtils.infoMethodIn(PrefsHttpProtocolParamsActivity.class, "onItemSelected", position);
			if (position != currSelectedId) {
				this.activity.updateHttpProtocolParamDsc(
					this.httpProtocolParamsCopy, 
					this.etPrefsHttpProtocolParams_ParameterName,
					this.cbPrefsHttpProtocolParams_EnabledForUploading);
			}
			HttpProtocolParamDsc paramDsc = this.httpProtocolParamsCopy.getParamDsc(position);
			this.etPrefsHttpProtocolParams_ParameterExample.setText(paramDsc.getExample());
			this.etPrefsHttpProtocolParams_ParameterName.setText(paramDsc.getName());
			this.cbPrefsHttpProtocolParams_EnabledForUploading.setChecked(paramDsc.isEnabled());
			currSelectedId = position;
			LogUtils.infoMethodOut(PrefsHttpProtocolParamsActivity.class, "onItemSelected", currSelectedId);
		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// noop.
		}
	}
	
	private static final class OnClickButtonSaveListener implements OnClickListener {
		private PrefsHttpProtocolParamsActivity activity;
		private Preferences preferences;
		private EditText etPrefsHttpProtocolParams_ParameterName;
		private CheckBox cbPrefsHttpProtocolParams_EnabledForUploading;
		private HttpProtocolParams httpProtocolParamsCopy;
		
		public OnClickButtonSaveListener(
			PrefsHttpProtocolParamsActivity activity,
			Preferences preferences,
			EditText etPrefsHttpProtocolParams_ParameterName,
			CheckBox cbPrefsHttpProtocolParams_EnabledForUploading,
			HttpProtocolParams httpProtocolParamsCopy) {
			this.activity = activity;
			this.preferences = preferences;
			this.etPrefsHttpProtocolParams_ParameterName = etPrefsHttpProtocolParams_ParameterName;
			this.cbPrefsHttpProtocolParams_EnabledForUploading = cbPrefsHttpProtocolParams_EnabledForUploading;
			this.httpProtocolParamsCopy = httpProtocolParamsCopy;
		}

		@Override
		public void onClick(View v) {
			boolean valid = true;
			if (valid) {
				this.activity.updateHttpProtocolParamDsc(
					this.httpProtocolParamsCopy, 
					this.etPrefsHttpProtocolParams_ParameterName,
					this.cbPrefsHttpProtocolParams_EnabledForUploading);
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
	
	public void updateHttpProtocolParamDsc(
		HttpProtocolParams httpProtocolParamsCopy,
		EditText etPrefsHttpProtocolParams_ParameterName,
		CheckBox cbPrefsHttpProtocolParams_EnabledForUploading) {
		LogUtils.infoMethodIn(PrefsHttpProtocolParamsActivity.class, "updateHttpProtocolParamDsc");
		HttpProtocolParamDsc paramDsc = httpProtocolParamsCopy.getParamDsc(currSelectedId);
		paramDsc.setName(
			etPrefsHttpProtocolParams_ParameterName.getText().toString());
		paramDsc.setEnabled(
			cbPrefsHttpProtocolParams_EnabledForUploading.isChecked());	
		LogUtils.infoMethodOut(PrefsHttpProtocolParamsActivity.class, "updateHttpProtocolParamDsc", "updated", currSelectedId, paramDsc);
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences_http_protocol_params);
     
        this.setTitle(R.string.tiPrefsHttpProtocolParams);
        
        Preferences prefs = Preferences.get();
        HttpProtocolParams httpProtocolParamsCopy = prefs.getHttpProtocolParams().copy();
        
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
        
        Button btPrefsHttpProtocolParams_Save = (Button) findViewById(R.id.btPrefsHttpProtocolParams_Save);
        Button btPrefsHttpProtocolParams_Cancel = (Button) findViewById(R.id.btPrefsHttpProtocolParams_Cancel);
           
        spPrefsHttpProtocolParams_Parameter.setOnItemSelectedListener(
    		new OnHttpParameterItemSelectedListener(this, 
				httpProtocolParamsCopy,   				
				etPrefsHttpProtocolParams_ParameterExample,
				etPrefsHttpProtocolParams_ParameterName,
				cbPrefsHttpProtocolParams_EnabledForUploading));
        btPrefsHttpProtocolParams_Save.setOnClickListener(
			new OnClickButtonSaveListener(this, prefs, 
				etPrefsHttpProtocolParams_ParameterName,	
				cbPrefsHttpProtocolParams_EnabledForUploading,
				httpProtocolParamsCopy));		
        btPrefsHttpProtocolParams_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
