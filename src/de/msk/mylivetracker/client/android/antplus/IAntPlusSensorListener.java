package de.msk.mylivetracker.client.android.antplus;

import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.Callback;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;

/**
 * classname: IAntPlusSensorListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public interface IAntPlusSensorListener extends Callback {
	public Short getSensorType();	
	public void setConnection(WFSensorConnection connection);
	public void onEnabled(WFSensorConnectionStatus status);
	public void onDisabled();
	public void onHasData();
}
