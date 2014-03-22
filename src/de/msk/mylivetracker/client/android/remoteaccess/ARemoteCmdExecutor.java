package de.msk.mylivetracker.client.android.remoteaccess;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;

import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.util.LogUtils;

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
	private IResponseSender responseSender;
	private ExecutorService executorService;

	public void init(ARemoteCmdDsc cmdDsc, String sender, String param, 
		IResponseSender responseSender, ExecutorService executorService) {
		init(cmdDsc, sender, new String[]{param}, 
			responseSender, executorService);
	}
	
	public void init(ARemoteCmdDsc cmdDsc, String sender, String[] params, 
		IResponseSender responseSender, ExecutorService executorService) {
		if (cmdDsc == null) {
			throw new IllegalArgumentException("cmdDsc must not be null.");
		}
		if (StringUtils.isEmpty(sender)) {
			throw new IllegalArgumentException("sender must not be empty.");
		}
		if (responseSender == null) {
			throw new IllegalArgumentException("responseSender must not be null.");
		}
		this.cmdDsc = cmdDsc;
		this.sender = sender;
		this.params = params;
		this.responseSender = responseSender;
		this.executorService = executorService;
	}
	
	@Override
	public void run() {
		LogUtils.infoMethodIn(this.getClass(), "run");
		String response = "";
		try {
			LogUtils.infoMethodState(this.getClass(), "run", "params", Arrays.toString(this.params));
			if (!this.cmdDsc.matchesSyntax(params)) {
				response = "failed:command does not match syntax '" + this.cmdDsc.getSyntax() + "'";
			} else {
				Result result = this.executeCmdAndCreateResponse(this.params);
				response += result.isSuccess() ? "ok" : "failed";
				if (!StringUtils.isEmpty(result.getResponse())) {
					response += ":" + result.getResponse();
				}
			}
		} catch (Exception e) {
			response = "failed:internal error";
			LogUtils.infoMethodState(this.getClass(), "run", "run failed", (Object[])e.getStackTrace());
		} finally {
			try {
				response = "[" + this.getCmdDsc().getName() + "]:" + response;
				this.responseSender.sendResponse(sender, response);
				LogUtils.infoMethodState(this.getClass(), "run", "sendResponse", response);
			} catch (Exception e) {
				LogUtils.infoMethodState(this.getClass(), "run", "exception", e.toString());
			}
		}
		LogUtils.infoMethodOut(this.getClass(), "run");
	}
	
	public ARemoteCmdDsc getCmdDsc() {
		return this.cmdDsc;
	}
	
	public static class Result {
		private boolean success;
		private String response;
		public Result(boolean success, String response) {
			this.success = success;
			this.response = response;
		}
		public boolean isSuccess() {
			return success;
		}
		public String getResponse() {
			return response;
		}
		@Override
		public String toString() {
			return "Result [success=" + success + ", response=" + response
				+ "]";
		}
	}
	
	protected ExecutorService getExecutorService() {
		return executorService;
	}

	public abstract Result executeCmdAndCreateResponse(String... params);

	@Override
	public String toString() {
		return "ARemoteCmdExecutor [cmdDsc=" + cmdDsc + ", sender=" + sender
			+ ", params=" + Arrays.toString(params) + "]";
	}
}
