package de.msk.mylivetracker.client.android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.mainview.updater.UpdaterUtils;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.FormatUtils.Unit;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
            
        this.setTitle(R.string.tiInfo);
        
        Button btnInfo_Back = (Button)
        	findViewById(R.id.btInfo_Back);        
        TextView tvInfo_Version = (TextView)
        	findViewById(R.id.tvInfo_Version);
        TextView tvInfo_Mileage = (TextView)
        	findViewById(R.id.tvInfo_Mileage);
        
        tvInfo_Version.setText(App.getAppNameComplete());
        tvInfo_Mileage.setText(UpdaterUtils.getFltStr(
        	TrackStatus.get().getMileageInMtr() / 1000f, 2, 
        	Unit.Kilometer));
        
        btnInfo_Back.setOnClickListener(
			new OnFinishActivityListener(this));        
    }
}
