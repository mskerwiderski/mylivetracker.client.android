package de.msk.mylivetracker.client.android.remoteaccess;

/**
 * classname: SmsCmdError
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
public class SmsCmdError extends ASmsCmdExecutor {

	public static String NAME = "error";
	public static String SYNTAX = "";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 0, 0);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return true;
		}
		
	}
	
	public SmsCmdError(String sender, String... params) {
		super(new CmdDsc(), sender, params);
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
