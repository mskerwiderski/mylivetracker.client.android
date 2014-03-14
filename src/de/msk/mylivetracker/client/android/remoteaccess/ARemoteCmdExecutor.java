package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.sms.SmsSendUtils;

/**
 * classname: ARemoteCmdExecutor
 * 
 * @author michael skerwiderski, (c)2012
 * @version 001
 * @since 1.5.0
 * 
 * history:
 * 001	2014-02-28	revised for v1.6.0.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public abstract class ARemoteCmdExecutor implements Runnable {
	
	private ARemoteCmdDsc cmdDsc;
	private String sender;
	private String[] params;

	public void init(ARemoteCmdDsc cmdDsc, String sender, String... params) {
		if (cmdDsc == null) {
			throw new IllegalArgumentException("cmdDsc must not be null.");
		}
		if (StringUtils.isEmpty(sender)) {
			throw new IllegalArgumentException("sender must not be empty.");
		}
		this.cmdDsc = cmdDsc;
		this.sender = sender;
		this.params = params;
	}
	
	@Override
	public void run() {
		LogUtils.infoMethodIn(this.getClass(), "run");
		String smsResponse = "";
		try {
			LogUtils.infoMethodState(this.getClass(), "run", "params", Arrays.toString(this.params));
			if (!this.cmdDsc.matchesSyntax(params)) {
				smsResponse = ResponseCreator.getResultOfError( 
					"command does not match syntax '" + this.cmdDsc.getSyntax() + "'");
			} else {
				smsResponse = this.executeCmdAndCreateSmsResponse(this.params);
			}
		} catch (Exception e) {
			smsResponse = ResponseCreator.getResultOfError(e.getMessage());
			LogUtils.infoMethodState(this.getClass(), "run", "run failed", e.toString());
		} finally {
			try {
				smsResponse = "[" + this.getCmdDsc().getName() + "]:" + smsResponse;
				SmsSendUtils.sendSms(this.sender, smsResponse);
				LogUtils.infoMethodState(this.getClass(), "run", "sms response", smsResponse);
			} catch (Exception e) {
				LogUtils.infoMethodState(this.getClass(), "run", "send sms", e.toString());
			}
		}
		LogUtils.infoMethodOut(this.getClass(), "run");
	}
	
	public ARemoteCmdDsc getCmdDsc() {
		return this.cmdDsc;
	}
	
	public abstract String executeCmdAndCreateSmsResponse(String... params);

	@Override
	public String toString() {
		return "ARemoteCmdExecutor [cmdDsc=" + cmdDsc + ", sender=" + sender
			+ ", params=" + Arrays.toString(params) + "]";
	}
}
