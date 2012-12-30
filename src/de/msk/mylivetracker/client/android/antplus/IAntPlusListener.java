package de.msk.mylivetracker.client.android.antplus;

import com.wahoofitness.api.WFHardwareConnectorTypes.WFAntError;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFHardwareState;

/**
 * classname: IAntPlusListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public interface IAntPlusListener {
	public void onAntPlusSensorListenerAdded(int countSensorListeners);
	public void onAntPlusSensorListenerRemoved(int countSensorListeners);
	public void onAntPlusError(WFAntError error);
	public void onAntPlusStateChanged(WFHardwareState state);
}
