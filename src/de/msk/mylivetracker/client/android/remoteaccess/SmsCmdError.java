package de.msk.mylivetracker.client.android.remoteaccess;


/**
 * SmsCmdError.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2012-11-24 	initial.
 * 
 */
public class SmsCmdError extends ASmsCmdExecutor {

	public SmsCmdError(String sender, String... params) {
		super("dummy", sender, params);
	}

	@Override
	public CmdDsc getCmdDsc() {
		return null;
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		String res = "<reason unknown>";
		if (params.length > 0) {
			res = "";
			for (String param : params) {
				res += param;
			}
		}
		return res;
	}
}
