package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: ASmsCmdExecutor
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class ASmsCmdExecutor implements Runnable {
	
	private String cmdName;
	private String sender;
	private String[] params;

	public ASmsCmdExecutor(String cmdName, String sender, String... params) {
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
		String smsResponse = SmsCmdReceiver.SMS_CMD_ERROR_PREFIX + "internal command error: ";
		try {
			LogUtils.infoMethodState(this.getClass(), "run", "params", Arrays.toString(this.params));
			smsResponse = this.executeCmdAndCreateSmsResponse(this.params);
		} catch (Exception e) {
			smsResponse += e.toString();
			LogUtils.infoMethodState(this.getClass(), "run", "run failed", e.toString());
		} finally {
			try {
				SmsSendUtils.sendSms(this.sender, smsResponse);
			} catch (Exception e) {
				LogUtils.infoMethodState(this.getClass(), "run", "send sms", e.toString());
			}
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
