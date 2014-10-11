package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;

/**
 * classname: EmergencySignalInfo
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class EmergencySignalInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = -3481065429800174013L;

	private static EmergencySignalInfo emergencySignalInfo = null;
	
	public static void update(boolean activated, String message) {
		if (TrackStatus.isInResettingState()) return;
		emergencySignalInfo = 
			EmergencySignalInfo.createNewEmergencySignalInfo(
				activated, message);
	}
	public static EmergencySignalInfo get() {
		return emergencySignalInfo;
	}
	public static void reset() {
		emergencySignalInfo = null;
	}
	public static void set(EmergencySignalInfo emergencySignalInfo) {
		EmergencySignalInfo.emergencySignalInfo = emergencySignalInfo;
	}
	
	public boolean activated = false;
	public String message = null;
	
	private EmergencySignalInfo() {
	}
	private EmergencySignalInfo(boolean activated, String message) {
		this.activated = activated;
		this.message = message;
	}
	public static EmergencySignalInfo createNewEmergencySignalInfo(
		boolean activated, String message) {
		return new EmergencySignalInfo(activated, message);			
	}
	public boolean isActivated() {
		return activated;
	}
	public String getMessage() {
		return message;
	}
	@Override
	public String toString() {
		return "EmergencySignalInfo [activated=" + activated + ", message="
			+ message + ", getId()=" + getId() + ", getTimestamp()="
			+ getTimestamp() + "]";
	}
}
