package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;

/**
 * EmergencySignalInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class EmergencySignalInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = -3481065429800174013L;

	private static EmergencySignalInfo emergencySignalInfo = null;
	
	public static void update(boolean activated) {
		emergencySignalInfo = 
			EmergencySignalInfo.createNewEmergencySignalInfo(activated);
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
	
	private EmergencySignalInfo() {
	}
	
	private EmergencySignalInfo(boolean activated) {
		this.activated = activated;
	}

	public static EmergencySignalInfo createNewEmergencySignalInfo(
		boolean activated) {
		return new EmergencySignalInfo(activated);			
	}

	public boolean isActivated() {
		return activated;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmergencySignalInfo [activated=").append(activated)
			.append("]");
		return builder.toString();
	}
}
