package de.msk.mylivetracker.client.android.remoteaccess.commands;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdReceiver;


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
					ARemoteCmdReceiver.containsCommand(params[0]);
			}
			return matches;
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		String response = null;
		if (params.length == 0) {
			response = ARemoteCmdReceiver.getCommandsAsStr();
		} else {
			response = ARemoteCmdReceiver.getCommandSyntax(params[0]);
		}
		return new Result(true, response);
	}
}
