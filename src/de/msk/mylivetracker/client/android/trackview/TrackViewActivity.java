package de.msk.mylivetracker.client.android.trackview;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import android.os.Bundle;
import android.webkit.WebView;
import de.msk.mylivetracker.client.android.app.pro.R;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.MyLiveTrackerUtils;

/**
 * TrackViewActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-11-01 initial.
 * 
 */
public class TrackViewActivity extends AbstractActivity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_view);
     
        this.setTitle(R.string.tiTrackView);
        
        WebView webView = (WebView)findViewById(R.id.wvTrackView);
        webView.getSettings().setJavaScriptEnabled(true);
        
        String statusParamsId = Preferences.get().getStatusParamsId();
        if (!StringUtils.isEmpty(statusParamsId)) {
        	String url = MyLiveTrackerUtils.getPortalStatusUrl(statusParamsId);
        	webView.loadUrl(url);
        	LogUtils.info(this.getClass(), url);
        } else {
        	String notLinkedToMyLiveTrackerPortal = 
        		"<html><body>" + 
				StringEscapeUtils.escapeHtml(
					MainActivity.get().getText(
						R.string.wvInfoTrackView_NotLinkedToMyLiveTrackerPortal).toString()) + 
				"</body></html>";
        	webView.loadData(notLinkedToMyLiveTrackerPortal, "text/html", null);
        }
    }
}
