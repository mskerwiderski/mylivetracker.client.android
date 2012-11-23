package de.msk.mylivetracker.client.android.remoteaccess;

import org.apache.commons.lang.StringUtils;

/**
 * AbstractSmsCmdExecutor.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 000 2012-11-23 initial.
 * 
 */
public abstract class AbstractSmsCmdExecutor implements Runnable {
	
	private String sender;
	private String[] params;

	public AbstractSmsCmdExecutor(String sender, String... params) {
		if (StringUtils.isEmpty(sender)) {
			throw new IllegalArgumentException("sender must not be null.");
		}
		this.sender = sender;
		this.params = params;
	}
	
	@Override
	public void run() {
		String smsResponse = this.executeCmdAndCreateSmsResponse(this.params);
		SmsReceiver.sendSms(this.sender, smsResponse);
	}
	
	public static class ParamsDsc {
		private int minParams;
		private int maxParams;
		public ParamsDsc(int minParams, int maxParams) {
			if ((minParams < 0) || (maxParams < 0)) {
				throw new IllegalArgumentException("minParams and maxParams must not be less than or equals 0.");
			}
			if (maxParams < minParams) {
				throw new IllegalArgumentException("maxParams must not be less than minParams.");
			}
			this.minParams = minParams;
			this.maxParams = maxParams;
		}
		public int getMinParams() {
			return minParams;
		}
		public int getMaxParams() {
			return maxParams;
		}
	}
	public abstract ParamsDsc getParamsDsc();
	public abstract String executeCmdAndCreateSmsResponse(String... params);
}
