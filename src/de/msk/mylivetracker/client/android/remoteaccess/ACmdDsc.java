package de.msk.mylivetracker.client.android.remoteaccess;

import org.apache.commons.lang.StringUtils;

/**
 * classname: ACmdDsc
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-28	origin.
 * 
 */
public abstract class ACmdDsc {

	private String name;
	private String paramDsc;
	private int minParams;
	private int maxParams;
	
	public ACmdDsc(String name, String paramDsc, int minParams, int maxParams) {
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("name must not be empty.");
		}
		if (paramDsc == null) {
			throw new IllegalArgumentException("paramDsc must not be null.");
		}
		if ((minParams < 0) || (maxParams < 0)) {
			throw new IllegalArgumentException("minParams and maxParams must not be less than or equals 0.");
		}
		if (maxParams < minParams) {
			throw new IllegalArgumentException("maxParams must not be less than minParams.");
		}
		this.name = name;
		this.paramDsc = paramDsc;
		this.minParams = minParams;
		this.maxParams = maxParams;
	}
	
	public abstract boolean matchesSyntax(String[] params);
	
	public String getSyntax() {
		return name + " " + paramDsc;
	}
	public String getName() {
		return name;
	}
	public String getParamDsc() {
		return paramDsc;
	}
	public int getMinParams() {
		return minParams;
	}
	public int getMaxParams() {
		return maxParams;
	}
}
