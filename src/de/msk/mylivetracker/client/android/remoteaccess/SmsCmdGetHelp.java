package de.msk.mylivetracker.client.android.remoteaccess;

import org.apache.commons.lang.StringUtils;


/**
 * classname: SmsCmdGetHelp
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-08	origin.
 * 
 */
public class SmsCmdGetHelp extends ASmsCmdExecutor {

	public static String NAME = "gethelp";
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
	
	public SmsCmdGetHelp(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	private static String addCommandSyntax(String str, String command, String syntax, boolean addSep) {
		str += command;
		if (!StringUtils.isEmpty(syntax)) {
			str += " " + syntax;
		}
		if (addSep) {
			str += ", ";
		}
		return str;
	}
	private static String getHelpStr() {
		String helpStr = "";
		helpStr = addCommandSyntax(helpStr, SmsCmdGetHelp.NAME, SmsCmdGetHelp.SYNTAX, true);
		helpStr = addCommandSyntax(helpStr, SmsCmdGetAppVersion.NAME, SmsCmdGetAppVersion.SYNTAX, true);
		helpStr = addCommandSyntax(helpStr, SmsCmdGetLocation.NAME, SmsCmdGetLocation.SYNTAX, true);
		helpStr = addCommandSyntax(helpStr, SmsCmdGetHeartrate.NAME, SmsCmdGetHeartrate.SYNTAX, true);
		helpStr = addCommandSyntax(helpStr, SmsCmdTrack.NAME, SmsCmdTrack.SYNTAX, true);
		helpStr = addCommandSyntax(helpStr, SmsCmdGetConfig.NAME, SmsCmdGetConfig.SYNTAX, true);
		helpStr = addCommandSyntax(helpStr, SmsCmdSetConfig.NAME, SmsCmdSetConfig.SYNTAX, true);
		helpStr = addCommandSyntax(helpStr, SmsCmdUploadConfig.NAME, SmsCmdUploadConfig.SYNTAX, true);
		helpStr = addCommandSyntax(helpStr, SmsCmdUploadTrack.NAME, SmsCmdUploadTrack.SYNTAX, false);
		return helpStr;
	}
	
	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return getHelpStr();
	}
}
