package de.msk.mylivetracker.client.android.status;

/**
 * classname: PinCodeStatus
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.7.0
 * 
 * history:
 * 000	2014-09-26	origin.
 * 
 */
public class PinCodeStatus {

	private static PinCodeStatus pinCodeStatus = null;

	public static PinCodeStatus get() {
		if (pinCodeStatus == null) {
			pinCodeStatus = new PinCodeStatus();
		}
		return pinCodeStatus;
	}
	
	private PinCodeStatus() {
	}
	
	private boolean successful = false;
	private boolean canceled = false;

	public void reset() {
		this.successful = false;
		this.canceled = false;
	}
	
	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}
}
