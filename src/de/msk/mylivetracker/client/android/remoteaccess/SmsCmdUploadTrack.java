package de.msk.mylivetracker.client.android.remoteaccess;


/**
 * classname: SmsCmdUploadTrack
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-06	origin.
 * 
 */
public class SmsCmdUploadTrack extends ASmsCmdExecutor {

	public static String NAME = "upltrack";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, "", 0, 0);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
		
	}
	
	public SmsCmdUploadTrack(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return "not supported";
	}
}
