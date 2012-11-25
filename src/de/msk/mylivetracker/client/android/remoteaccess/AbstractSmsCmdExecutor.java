package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.util.LogUtils;

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
	
	private String cmdName;
	private String sender;
	private String[] params;

	public AbstractSmsCmdExecutor(String cmdName, String sender, String... params) {
		if (StringUtils.isEmpty(cmdName)) {
			throw new IllegalArgumentException("cmdName must not be null.");
		}
		if (StringUtils.isEmpty(sender)) {
			throw new IllegalArgumentException("sender must not be null.");
		}
		this.cmdName = cmdName;
		this.sender = sender;
		this.params = params;
	}
	
	@Override
	public void run() {
		LogUtils.infoMethodIn(this.getClass(), "run");
		try {
			LogUtils.infoMethodState(this.getClass(), "run", "params", Arrays.toString(this.params));
			String smsResponse = this.executeCmdAndCreateSmsResponse(this.params);
			SmsCmdReceiver.sendSms(this.sender, smsResponse);
		} catch (Exception e) {
			LogUtils.infoMethodState(this.getClass(), "run", "run failed", e.toString());
		}
		LogUtils.infoMethodOut(this.getClass(), "run");
	}
	
	public static class CmdDsc {
		private String paramDsc;
		private int minParams;
		private int maxParams;
		public CmdDsc(String paramDsc, int minParams, int maxParams) {
			if (paramDsc == null) {
				throw new IllegalArgumentException("paramDsc must not be null.");
			}
			if ((minParams < 0) || (maxParams < 0)) {
				throw new IllegalArgumentException("minParams and maxParams must not be less than or equals 0.");
			}
			if (maxParams < minParams) {
				throw new IllegalArgumentException("maxParams must not be less than minParams.");
			}
			this.paramDsc = paramDsc;
			this.minParams = minParams;
			this.maxParams = maxParams;
		}
		public String getParamSyntax() {
			return paramDsc;
		}
		public int getMinParams() {
			return minParams;
		}
		public int getMaxParams() {
			return maxParams;
		}
	}

	public String getCmdName() {
		return cmdName;
	}

	public abstract CmdDsc getCmdDsc();
	public abstract String executeCmdAndCreateSmsResponse(String... params);
}
