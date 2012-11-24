package de.msk.mylivetracker.client.android;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils.Unit;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.VersionUtils;

/**
 * InfoActivity.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class InfoActivity extends AbstractActivity {
	
	private static final class OnClickButtonBackListener implements OnClickListener {
		private InfoActivity activity;		
		/**
		 * @param aMain
		 */
		private OnClickButtonBackListener(InfoActivity activity) {
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
        setContentView(R.layout.info);
            
        this.setTitle(R.string.tiInfo);
        
        Button btnInfo_Back = (Button)findViewById(R.id.btInfo_Back);        
        TextView tvInfo_Version = (TextView)findViewById(R.id.tvInfo_Version);
        TextView tvInfo_Mileage = (TextView)findViewById(R.id.tvInfo_Mileage);
        
        tvInfo_Version.setText(VersionUtils.get().getVersionStr());
        tvInfo_Mileage.setText(UpdaterUtils.getFltStr(TrackStatus.get().getMileageInMtr() / 1000f, 2, Unit.Kilometer));
        
        btnInfo_Back.setOnClickListener(
			new OnClickButtonBackListener(this));        
    }
}
