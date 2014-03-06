package de.msk.mylivetracker.client.android.remoteaccess;


/**
 * classname: SmsCmdSetConfig
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-06	origin.
 * 
 */
public class SmsCmdGetConfig extends ASmsCmdExecutor {

	public static String NAME = "getconfig";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, "<param> <value>", 2, 2);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 2);
		}
		
	}
	
	public SmsCmdGetConfig(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return "not supported";
	}
}
