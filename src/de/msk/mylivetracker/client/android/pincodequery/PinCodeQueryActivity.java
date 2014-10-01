package de.msk.mylivetracker.client.android.pincodequery;

import org.apache.commons.lang3.StringUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.PinCodeStatus;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: PinCodeQueryActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class PinCodeQueryActivity extends AbstractActivity {	
	
	@Override
	protected boolean isPrefsActivity() {
		return false;
	}
	
	public static void runPinCodeQuery() {
		AbstractActivity act = AbstractActivity.getActive();
		if (act == null) {
			throw new IllegalStateException("pincode query cannot run without an active activity.");
		}
		act.startActivity(new Intent(act, PinCodeQueryActivity.class));
	}
	
	private static final class OnClickButtonOkListener extends ASafeOnClickListener {
		private PinCodeQueryActivity activity;
		private EditText etPinCodeQuery_PinCode;
		
		public OnClickButtonOkListener(
			PinCodeQueryActivity activity,
			EditText etPinCodeQuery_PinCode) {
			this.activity = activity;
			this.etPinCodeQuery_PinCode = etPinCodeQuery_PinCode;		
		}
	
		@Override
		public void onClick() {
			boolean valid = StringUtils.equals(
				etPinCodeQuery_PinCode.getText().toString(), 
				PrefsRegistry.get(PinCodeQueryPrefs.class).getPinCode());
			if (!valid) {
				SimpleInfoDialog.show(this.activity, 
					R.string.vdPinCodeQueryPrefs_PinCodeInvalid);
			} else {
				PinCodeStatus.get().setSuccessful(true);
				this.activity.finish();
			}
		}		
	}
	
	private static final class OnClickButtonCancelListener extends ASafeOnClickListener {
		private PinCodeQueryActivity activity;
		
		public OnClickButtonCancelListener(
			PinCodeQueryActivity activity) {
			this.activity = activity;
		}
	
		@Override
		public void onClick() {
			PinCodeStatus.get().setCanceled(true);
			this.activity.finish();
		}		
	}
	
	@Override
	public void onBackPressed() {
		// noop.
		return;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_code_query);
        this.setTitle(R.string.tiPinCodeQuery);
        EditText etPinCodeQuery_PinCode = (EditText) findViewById(R.id.etPinCodeQuery_PinCode);
        etPinCodeQuery_PinCode.setText("");
        etPinCodeQuery_PinCode.requestFocus();
        Button btPinCodeQuery_Ok = (Button) findViewById(R.id.btPinCodeQuery_Ok);
        btPinCodeQuery_Ok.setOnClickListener(
			new OnClickButtonOkListener(
				this, etPinCodeQuery_PinCode));
        Button btPinCodeQuery_Cancel = (Button) findViewById(R.id.btPinCodeQuery_Cancel);
        if (PinCodeQueryPrefs.protectEntireAppConfigured()) {
        	btPinCodeQuery_Cancel.setVisibility(View.GONE);
        } else {
        	btPinCodeQuery_Cancel.setOnClickListener(
    			new OnClickButtonCancelListener(this));
        }
    }
}
