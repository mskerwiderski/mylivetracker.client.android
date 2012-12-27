package de.msk.mylivetracker.client.android.antplus;

import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.Callback;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;

/**
 * IAntPlusSensorListener.
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
public interface IAntPlusSensorListener extends Callback {
	public Short getSensorType();	
	public void setConnection(WFSensorConnection connection);
	public void onEnabled(WFSensorConnectionStatus status);
	public void onDisabled();
	public void onHasData();
}
