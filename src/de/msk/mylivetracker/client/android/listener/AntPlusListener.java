package de.msk.mylivetracker.client.android.listener;

import org.apache.commons.lang.StringUtils;

import com.wahoofitness.api.WFHardwareConnectorTypes.WFAntError;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFHardwareState;

import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.status.TrackStatus;

/**
 * AntPlusListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 	2011-08-11 initial.
 * 
 */
public class AntPlusListener implements IAntPlusListener {

	private static AntPlusListener antPlusListener = null;

	public static AntPlusListener get() {
		if (antPlusListener == null) {
			antPlusListener = new AntPlusListener();			
		} 
		return antPlusListener;
	}	

	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.listener.IAntPlusListener#onAntPlusSensorListenerAdded(int)
	 */
	@Override
	public void onAntPlusSensorListenerAdded(int countSensorListeners) {
		TrackStatus.get().setAntPlusStatus(MainActivity.get().getString(
			R.string.antPlus_CountSensorListeners, countSensorListeners));		
		MainActivity.get().updateView();
	}

	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.listener.IAntPlusListener#onAntPlusSensorListenerRemoved(int)
	 */
	@Override
	public void onAntPlusSensorListenerRemoved(int countSensorListeners) {		
		if (countSensorListeners == 0) {
			TrackStatus.get().setAntPlusStatus(null);
		} else {
			TrackStatus.get().setAntPlusStatus(MainActivity.get().getString(
				R.string.antPlus_CountSensorListeners, countSensorListeners));
		}
		MainActivity.get().updateView();
	}

	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.listener.IAntPlusListener#onAntPlusError(com.wahoofitness.api.WFHardwareConnectorTypes.WFAntError)
	 */
	@Override
	public void onAntPlusError(WFAntError error) {
		switch (error) {
			case WF_ANT_ERROR_CLAIM_FAILED:
				TrackStatus.get().setAntPlusStatus(
					MainActivity.get().getString(R.string.antPlus_InUse));
				MainActivity.get().updateView();
			break;
		}		
	}

	/* (non-Javadoc)
	 * @see de.msk.mylivetracker.client.android.listener.IAntPlusListener#onAntPlusStateChanged(com.wahoofitness.api.WFHardwareConnectorTypes.WFHardwareState)
	 */
	@Override
	public void onAntPlusStateChanged(WFHardwareState state) {
		String statusStr = null;
		switch (state) {
			case WF_HARDWARE_STATE_DISABLED:
	        	if (MainActivity.get().isAntPlusSupported()) {
	        		statusStr = MainActivity.get().getString(R.string.antPlus_Disabled);
	        	}
	        	else {
	        		statusStr = MainActivity.get().getString(R.string.antPlus_NotSupported);
	        	}
				break;
			case WF_HARDWARE_STATE_SERVICE_NOT_INSTALLED:
				statusStr = MainActivity.get().getString(R.string.antPlus_NotInstalled);
				break;
			case WF_HARDWARE_STATE_SUSPENDED:
				statusStr = MainActivity.get().getString(R.string.antPlus_Suspended);
	        	break;
			case WF_HARDWARE_STATE_READY:
			default:
				statusStr = null;
				break;
		}
		if (!StringUtils.isEmpty(statusStr)) {
			TrackStatus.get().setAntPlusStatus(statusStr);
			MainActivity.get().updateView();
		}
	}
}
