package de.msk.mylivetracker.client.android.antplus;

import java.util.HashMap;
import java.util.Map;

import com.wahoofitness.api.WFHardwareConnector;
import com.wahoofitness.api.WFHardwareConnector.Callback;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFAntError;
import com.wahoofitness.api.WFHardwareConnectorTypes.WFHardwareState;
import com.wahoofitness.api.comm.WFConnectionParams;
import com.wahoofitness.api.comm.WFHeartrateConnection;
import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;

/**
 * classname: AntPlusManager
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class AntPlusManager implements Callback {

	private static class ConnDsc {
		private WFSensorConnection connection;
		private IAntPlusSensorListener listener;
		public ConnDsc(WFSensorConnection connection,
			IAntPlusSensorListener listener) {
			this.connection = connection;
			this.listener = listener;
			this.listener.setConnection(connection);
		}
		public WFSensorConnection getConnection() {
			return connection;
		}
		public IAntPlusSensorListener getListener() {
			return listener;
		}
	}
	private Map<Short, ConnDsc> connMap =
		new HashMap<Short, ConnDsc>();
	
	private IAntPlusListener antPlusListener = null;
	
	private static AntPlusManager antPlusManager = null;

	public static AntPlusManager get() {
		if (antPlusManager == null) {
			antPlusManager = new AntPlusManager();
			antPlusManager.setListener(AntPlusListener.get());
		} 
		return antPlusManager;
	}
	
	
	public void setListener(IAntPlusListener antPlusListener) {
		this.antPlusListener = antPlusListener;
	}
	
	public boolean hasSensorListeners() {
		return !connMap.isEmpty();
	}	
	
	public void requestSensorUpdates(
		IAntPlusSensorListener antPlusSensorListener) {
		WFHardwareConnector antPlusHwConnector = AntPlusHardware.getConn();
		WFSensorConnection connection = null;
		ConnDsc connDsc = connMap.get(antPlusSensorListener.getSensorType());
		if (connDsc != null) {
			connection = connDsc.getConnection();
		}
		if ((antPlusHwConnector != null) &&
			((connection == null) || !connection.isConnected())) {
			switch (getState(connection))	{
				case WF_SENSOR_CONNECTION_STATUS_IDLE: {
	        		WFConnectionParams connectionParams = new WFConnectionParams();
	        		connectionParams.sensorType = antPlusSensorListener.getSensorType();
	        		connection = (WFHeartrateConnection)
	        			antPlusHwConnector.initSensorConnection(connectionParams);
	        		if (connection != null) {
	        			connection.setCallback(antPlusSensorListener);
	        			connMap.put(antPlusSensorListener.getSensorType(), 
        					new ConnDsc(connection, antPlusSensorListener));
	        		}
					break;
				}
				case WF_SENSOR_CONNECTION_STATUS_CONNECTING:
				case WF_SENSOR_CONNECTION_STATUS_CONNECTED:
				case WF_SENSOR_CONNECTION_STATUS_DISCONNECTING:
				default:
					break;	
			}
		}
		antPlusSensorListener.onEnabled(getState(connection));
		if (this.antPlusListener != null) {
			this.antPlusListener.onAntPlusSensorListenerAdded(
				connMap.size());
		}		
	}
	
	public void removeUpdates(IAntPlusSensorListener antPlusSensorListener) {
		if (!connMap.containsKey(antPlusSensorListener.getSensorType())) {
			return;
		}
		WFSensorConnection connection = 
			connMap.get(antPlusSensorListener.getSensorType()).
			getConnection();
		if (connection == null) return;
		
		switch (getState(connection))	{
			case WF_SENSOR_CONNECTION_STATUS_IDLE: {
				if (connection != null) {
					connection.setCallback(null);
				}
				break;
			}
			case WF_SENSOR_CONNECTION_STATUS_CONNECTING:
			case WF_SENSOR_CONNECTION_STATUS_CONNECTED:
			case WF_SENSOR_CONNECTION_STATUS_DISCONNECTING:
				connection.disconnect();					
				break;
		}
		connMap.remove(antPlusSensorListener.getSensorType());
		antPlusSensorListener.onDisabled();
		if (this.antPlusListener != null) {
			this.antPlusListener.
				onAntPlusSensorListenerRemoved(connMap.size());
		}		
	}
	
	private WFSensorConnectionStatus getState(
		WFSensorConnection connection) {
		WFSensorConnectionStatus state = 
			WFSensorConnectionStatus.WF_SENSOR_CONNECTION_STATUS_IDLE;
		if (connection != null) {
			state = connection.getConnectionStatus();
		}
		return state;
	}
	
	@Override
	public void hwConnAntError(WFAntError error) {
		switch (error) {
			case WF_ANT_ERROR_CLAIM_FAILED:
				if (this.antPlusListener != null) {
					this.antPlusListener.onAntPlusError(error);
				}
				AntPlusHardware.getConn().forceAntConnection(
					MainActivity.get().getString(R.string.app_name));
			break;
		default:
			break;
		}
	}

	@Override
	public void hwConnConnectedSensor(WFSensorConnection connection) {
		// nothing to do.
	}

	@Override
	public void hwConnConnectionRestored() {
		for (Short sensorType : connMap.keySet()) {
			WFSensorConnection[] connections = 
				AntPlusHardware.getConn().
				getSensorConnections(sensorType);
			if (connections != null) {
				WFSensorConnection connection = 
					connections[0];
				connection.setCallback(
					connMap.get(sensorType).getListener());
			}
		}
	}

	@Override
	public void hwConnDisconnectedSensor(WFSensorConnection connection) {
		// nothing to do.
	}

	@Override
	public void hwConnHasData() {
		for (Short sensorType : connMap.keySet()) {
			WFSensorConnection[] connections = 
				AntPlusHardware.getConn().
				getSensorConnections(sensorType);
			if ((connections != null) && (connections[0] != null)) {
				connMap.get(sensorType).getListener().onHasData();				
			}
		}
	}

	@Override
	public void hwConnStateChanged(WFHardwareState state) {
		if (this.antPlusListener != null) {
			this.antPlusListener.onAntPlusStateChanged(state);
		}
	}	
}
