package de.msk.mylivetracker.client.android.mainview;

import de.msk.mylivetracker.client.android.protocol.ProtocolPrefsActivity;
import de.msk.mylivetracker.client.android.util.listener.ASafeOnClickListener;

/**
 * classname: OnClickButtonNetworkListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class OnClickButtonNetworkListener extends ASafeOnClickListener {
	@Override
	public void onClick() {			
		MainActivity.get().
			startActivityWithWarningDlgIfTrackRunning(ProtocolPrefsActivity.class);	
	}
}
