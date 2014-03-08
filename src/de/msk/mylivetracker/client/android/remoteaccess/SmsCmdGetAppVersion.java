package de.msk.mylivetracker.client.android.remoteaccess;

import de.msk.mylivetracker.client.android.App.VersionDsc;

/**
 * classname: SmsCmdGetAppVersion
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
public class SmsCmdGetAppVersion extends ASmsCmdExecutor {

	public static String NAME = "getversion";
	public static String SYNTAX = "";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 0, 0);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
		
	}
	
	public SmsCmdGetAppVersion(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return VersionDsc.getVersionStr();
	}
}
