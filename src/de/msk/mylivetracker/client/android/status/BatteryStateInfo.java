package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;


/**
 * BatteryStateInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class BatteryStateInfo extends AbstractInfo implements Serializable {
	private static final long serialVersionUID = 1017373978215679750L;

	public enum State {
		Charging("CHG"), Discharging("DCHG"), NotCharging("NCHG"), Full("FULL"), Unknown("UKWN");
		String abbr;
		private State(String abbr) {
			this.abbr = abbr;
		}
		public String getAbbr() {
			return abbr;
		}
	};
	
	private static BatteryStateInfo batteryStateInfo = null;
	public static void update(State state,
		Integer percent, Integer degrees, Double voltage) {
		batteryStateInfo = 
			BatteryStateInfo.createNewBatteryStateInfo(
				batteryStateInfo, state, 
				percent, degrees, voltage);
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
	
	private BatteryStateInfo() {
	}
	
	private BatteryStateInfo(State state,
		Integer percent, Integer degrees, Double voltage) {
		this.state = state;
		this.percent = percent;
		this.degrees = degrees;
		this.voltage = voltage;
	}

	public static BatteryStateInfo createNewBatteryStateInfo(
		BatteryStateInfo currBatteryStateInfo, State state,
		Integer percent, Integer degrees, Double voltage) {
		
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
		}
		
		return new BatteryStateInfo(state,
			percent, degrees, voltage); 
	}
	
	public boolean isBatteryLow() {
		return this.percent <= 10.0d;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BatteryStateInfo [state=").append(state)
			.append(", percent=").append(percent).append(", degrees=")
			.append(degrees).append(", voltage=").append(voltage)
			.append("]");
		return builder.toString();
	}
	
	public State getState() {
		return state;
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
}
