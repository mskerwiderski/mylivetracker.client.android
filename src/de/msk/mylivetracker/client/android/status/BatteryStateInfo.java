package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;

/**
 * classname: BatteryStateInfo
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class BatteryStateInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = 1017373978215679750L;

	public enum State {
		Charging("CHG", "charging"), 
		Discharging("DCHG", "discharging"), 
		NotCharging("NCHG", "not charging"), 
		Full("FULL", "full"), 
		Unknown("UKWN", "unknown");
		String abbr;
		String label;
		private State(String abbr, String label) {
			this.abbr = abbr;
			this.label = label;
		}
		public String getAbbr() {
			return abbr;
		}
		public String getLabel() {
			return label;
		}
	};
	
	private static BatteryStateInfo batteryStateInfo = null;
	public static void update(State state,
		Integer percent, Integer degrees, Double voltage,
		Boolean usbCharge, Boolean acCharge) {
		if (TrackStatus.isInResettingState()) return;
		batteryStateInfo = 
			BatteryStateInfo.createNewBatteryStateInfo(
				batteryStateInfo, state, 
				percent, degrees, voltage,
				usbCharge, acCharge);
	}
	public static BatteryStateInfo get() {
		return batteryStateInfo;
	}
	public static void reset() {
		batteryStateInfo = null;
	}
	public static void set(BatteryStateInfo batteryStateInfo) {
		BatteryStateInfo.batteryStateInfo = batteryStateInfo;
	}
	
	private State state;
	private Integer percent;
	private Integer degrees;
	private Double voltage;
	private Boolean usbCharge;
	private Boolean acCharge;
	
	private BatteryStateInfo() {
	}
	
	private BatteryStateInfo(State state,
		Integer percent, Integer degrees, Double voltage,
		Boolean usbCharge, Boolean acCharge) {
		this.state = state;
		this.percent = percent;
		this.degrees = degrees;
		this.voltage = voltage;
		this.usbCharge = usbCharge;
		this.acCharge = acCharge;
	}

	public static BatteryStateInfo createNewBatteryStateInfo(
		BatteryStateInfo currBatteryStateInfo, State state,
		Integer percent, Integer degrees, Double voltage,
		Boolean usbCharge, Boolean acCharge) {
		
		if (currBatteryStateInfo != null) {
			if (state == null) {
				state = currBatteryStateInfo.state;
			}
			if (percent == null) {
				percent = currBatteryStateInfo.percent;
			}
			if (degrees == null) {
				degrees = currBatteryStateInfo.degrees;
			}
			if (voltage == null) {
				voltage = currBatteryStateInfo.voltage;
			}
			if (usbCharge == null) {
				usbCharge = currBatteryStateInfo.usbCharge;
			}
			if (acCharge == null) {
				acCharge = currBatteryStateInfo.acCharge;
			}
		}
		
		return new BatteryStateInfo(state,
			percent, degrees, voltage, usbCharge, acCharge); 
	}
	
	public boolean isBatteryLow() {
		return this.percent <= 10.0d;
	}	
	
	@Override
	public String toString() {
		return "BatteryStateInfo [state=" + state + ", percent=" + percent
				+ ", degrees=" + degrees + ", voltage=" + voltage
				+ ", usbCharge=" + usbCharge + ", acCharge=" + acCharge + "]";
	}

	public State getState() {
		return state;
	}
	
	public boolean fullOrCharging() {
		return
			(state != null) && (	
				state.equals(State.Charging) || 
				state.equals(State.Full));
	}
	
	public Integer getPercent() {
		return percent;
	}
	public Integer getDegrees() {
		return degrees;
	}
	public Double getVoltage() {
		return voltage;
	}
	public Boolean getUsbCharge() {
		return usbCharge;
	}
	public Boolean getAcCharge() {
		return acCharge;
	}
}
