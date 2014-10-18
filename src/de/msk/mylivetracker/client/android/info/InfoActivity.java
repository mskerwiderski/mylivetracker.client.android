package de.msk.mylivetracker.client.android.info;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryActivity;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.status.PinCodeStatus;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.FormatUtils.Unit;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;
import de.msk.mylivetracker.client.android.util.listener.OnFinishActivityListener;

/**
 * classname: InfoActivity
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class InfoActivity extends AbstractActivity {
	
	@Override
	protected boolean isPrefsActivity() {
		return false;
	}
	
	private static final class OnClickButtonEnterLeaveAdminMode extends ASafeOnClickListener {
		private Button btInfo_EnterLeaveAdminMode;
		public OnClickButtonEnterLeaveAdminMode(
			Button btInfo_EnterLeaveAdminMode) {
			this.btInfo_EnterLeaveAdminMode = btInfo_EnterLeaveAdminMode;
		}

		@Override
		public void onClick() {
			if (App.isAdminMode()) {
				PinCodeStatus.get().reset();
				btInfo_EnterLeaveAdminMode.setText(
        			App.getCtx().getString(R.string.btInfo_EnterAdminMode));
			} else {
				PinCodeQueryActivity.runPinCodeQuery();
			}
		}		
	}

	private void checkEnterLeaveAdminMode() {
		Button btInfo_EnterLeaveAdminMode = (Button)
        	findViewById(R.id.btInfo_EnterLeaveAdminMode);
		if (!PinCodeQueryPrefs.pinCodeQueryEnabledForPrefsViaAdminMode()) {
        	btInfo_EnterLeaveAdminMode.setVisibility(View.GONE);
        } else {
        	if (App.isAdminMode()) {
        		btInfo_EnterLeaveAdminMode.setText(
        			App.getCtx().getString(R.string.btInfo_LeaveAdminMode));
        	} else {
        		btInfo_EnterLeaveAdminMode.setText(
        			App.getCtx().getString(R.string.btInfo_EnterAdminMode));
        	}
        }
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkEnterLeaveAdminMode();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
            
        this.setTitle(R.string.tiInfo);
        
        Button btInfo_Back = (Button)
        	findViewById(R.id.btInfo_Back);
        Button btInfo_EnterLeaveAdminMode = (Button)
        	findViewById(R.id.btInfo_EnterLeaveAdminMode);
        checkEnterLeaveAdminMode();
        
        TextView tvInfo_Version = (TextView)
        	findViewById(R.id.tvInfo_Version);
        TextView tvInfo_Mileage = (TextView)
        	findViewById(R.id.tvInfo_Mileage);
        
        tvInfo_Version.setText(App.getAppNameComplete());
        tvInfo_Mileage.setText(UpdaterUtils.getFltStr(
        	TrackStatus.get().getMileageInMtr() / 1000f, 2, 
        	Unit.Kilometer));
        
        btInfo_Back.setOnClickListener(
			new OnFinishActivityListener(this));        
        btInfo_EnterLeaveAdminMode.setOnClickListener(
        	new OnClickButtonEnterLeaveAdminMode(
        		btInfo_EnterLeaveAdminMode));
    }
}
