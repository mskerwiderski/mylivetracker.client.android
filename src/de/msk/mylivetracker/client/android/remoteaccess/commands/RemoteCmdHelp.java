package de.msk.mylivetracker.client.android.remoteaccess.commands;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.RemoteCmdReceiver;


/**
 * classname: RemoteCmdHelp
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-08	origin.
 * 
 */
public class RemoteCmdHelp extends ARemoteCmdExecutor {

	public static String NAME = "help";
	public static String SYNTAX = "[<command>]";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, SYNTAX, 0, 1, 
				R.string.txRemoteCommand_Help);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			boolean matches =
				(params.length == 0) || (params.length == 1);
			if (params.length == 1) {
				matches = matches && 
					RemoteCmdReceiver.containsCommand(params[0]);
			}
			return matches;
		}
	}
	
	public RemoteCmdHelp(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		String response = null;
		if (params.length == 0) {
			response = RemoteCmdReceiver.getCommandsAsStr();
		} else {
			response = RemoteCmdReceiver.getCommandSyntax(params[0]);
		}
		return response;
	}
}
