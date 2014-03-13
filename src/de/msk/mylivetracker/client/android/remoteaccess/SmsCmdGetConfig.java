package de.msk.mylivetracker.client.android.remoteaccess;

import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;


/**
 * classname: SmsCmdGetConfig
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

	public static String NAME = "config";
	public static String SYNTAX = "<section>";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, SYNTAX, 1, 1);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return 
				(params.length == 1) && 
				(PrefsRegistry.get(params[0]) != null);
		}
	}
	
	public SmsCmdGetConfig(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return PrefsRegistry.get(params[0]).getPrefsDumpAsStr(true);
	}
}
