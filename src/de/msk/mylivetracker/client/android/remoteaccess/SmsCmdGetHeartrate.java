package de.msk.mylivetracker.client.android.remoteaccess;

import de.msk.mylivetracker.client.android.status.HeartrateInfo;

/**
 * classname: SmsCmdGetHeartrate
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.6.0
 * 
 * history:
 * 001	2014-03-09	origin.
 * 
 */
public class SmsCmdGetHeartrate extends ASmsCmdExecutor {

	public static String NAME = "getheartrate";
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
	
	public SmsCmdGetHeartrate(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}
	
	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return ResponseCreator.getResultOfGetHeartrate(HeartrateInfo.get());
	}
}
