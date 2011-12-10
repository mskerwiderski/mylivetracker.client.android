package de.msk.mylivetracker.client.android.listener;

import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.Callback;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;

/**
 * IAntPlusSensorListener.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public interface IAntPlusSensorListener extends Callback {
	public Short getSensorType();	
	public void setConnection(WFSensorConnection connection);
	public void onEnabled(WFSensorConnectionStatus status);
	public void onDisabled();
	public void onHasData();
}
