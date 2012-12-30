package de.msk.mylivetracker.client.android.antplus;

import com.wahoofitness.api.WFHardwareConnectorTypes.WFSensorType;
import com.wahoofitness.api.comm.WFHeartrateConnection;
import com.wahoofitness.api.comm.WFSensorConnection;
import com.wahoofitness.api.comm.WFSensorConnection.WFSensorConnectionStatus;
import com.wahoofitness.api.data.WFHeartrateData;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.status.HeartrateInfo;
import de.msk.mylivetracker.client.android.status.TrackStatus;

/**
 * classname: AntPlusHeartrateListener
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class AntPlusHeartrateListener implements IAntPlusSensorListener {

	private static final Short SENSOR_TYPE = WFSensorType.WF_SENSORTYPE_HEARTRATE;

	private WFHeartrateConnection connection;
	private static AntPlusHeartrateListener antPlusHeartrateListener = null;

	public static AntPlusHeartrateListener get() {
		if (antPlusHeartrateListener == null) {
			antPlusHeartrateListener = new AntPlusHeartrateListener();			
		} 
		return antPlusHeartrateListener;
	}
	
	@Override
	public Short getSensorType() {
		return SENSOR_TYPE;
	}
	
	@Override
	public void setConnection(WFSensorConnection connection) {
		this.connection = (WFHeartrateConnection)connection;
	}

	@Override
	public void onEnabled(WFSensorConnectionStatus status) {
		String statusStr = null;
		switch (status)	{
			case WF_SENSOR_CONNECTION_STATUS_IDLE: {
	     		statusStr = null;
				break;
			}
			case WF_SENSOR_CONNECTION_STATUS_CONNECTING:
				statusStr = MainActivity.get().getString(R.string.antPlus_ConnStateConnecting);
				break;
			case WF_SENSOR_CONNECTION_STATUS_CONNECTED:
				statusStr = MainActivity.get().getString(R.string.antPlus_ConnStateConnected);
				break;
			case WF_SENSOR_CONNECTION_STATUS_DISCONNECTING:
				statusStr = MainActivity.get().getString(R.string.antPlus_ConnStateDisconnecting);
				break;
			default:
				break;	
		}
		TrackStatus.get().setAntPlusHeartrateStatus(statusStr);
		MainActivity.get().updateView();
	}

	@Override
	public void onDisabled() {
		TrackStatus.get().setAntPlusHeartrateStatus(null);
		MainActivity.get().updateView();		
	}

	@Override
	public void onHasData() {
		WFHeartrateData heartrateData = connection.getHeartrateData();
		if (heartrateData != null) {
			HeartrateInfo.update(heartrateData);				
			MainActivity.get().updateView();
		}		
	}

	@Override
	public void connectionStateChanged(WFSensorConnectionStatus connState) {
		if ((this.connection != null) && !this.connection.isValid()) {
			this.connection.setCallback(null);
			this.connection = null;
		}
		String statusStr = null;
		switch (connState) {
			case WF_SENSOR_CONNECTION_STATUS_IDLE:
				statusStr = null;
				break;
			case WF_SENSOR_CONNECTION_STATUS_CONNECTING:
				statusStr = MainActivity.get().getString(R.string.antPlus_ConnStateConnecting);
				break; 
			case WF_SENSOR_CONNECTION_STATUS_CONNECTED:
				statusStr = MainActivity.get().getString(R.string.antPlus_ConnStateConnected);
				break;
			case WF_SENSOR_CONNECTION_STATUS_DISCONNECTING:
				statusStr = MainActivity.get().getString(R.string.antPlus_ConnStateDisconnecting);
				break;
			default:
				break;
		}
		TrackStatus.get().setAntPlusHeartrateStatus(statusStr);
		MainActivity.get().updateView();
	}
}
