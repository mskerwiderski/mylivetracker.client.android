package de.msk.mylivetracker.client.android.status;

import java.util.Date;

/**
 * AbstractInfo.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public abstract class AbstractInfo {
	
	private Date timestamp = new Date();

	/**
	 * @return the id
	 */
	public long getId() {
		return this.timestamp.getTime();
	}

	public boolean isUpToDate(AbstractInfo info) {
		if (info == null) return true;
		return this.getId() > info.getId();
	}
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}	
}
