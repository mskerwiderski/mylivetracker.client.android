package de.msk.mylivetracker.client.android.remoteaccess;


/**
 * classname: SmsCmdSetConfig
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-28	origin.
 * 
 */
public class SmsCmdSetConfig extends ASmsCmdExecutor {

	public static String NAME = "setconfig";
	public static String SYNTAX = "<section> <param> <value>";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 3, 3);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return true;
		}
		
	}
	
	public SmsCmdSetConfig(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return ResponseCreator.getResultNotSupported();
	}
}
