package de.msk.mylivetracker.client.android.antplus;

import com.wahoofitness.api.WFHardwareConnectorTypes.WFAntError;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFHardwareState;

/**
 * IAntPlusListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 001
 * 
 * history
 * 001	2012-12-25	revised for v1.5.x.
 * 000 	2011-08-11 	initial.
 * 
 */
public interface IAntPlusListener {
	public void onAntPlusSensorListenerAdded(int countSensorListeners);
	public void onAntPlusSensorListenerRemoved(int countSensorListeners);
	public void onAntPlusError(WFAntError error);
	public void onAntPlusStateChanged(WFHardwareState state);
}
