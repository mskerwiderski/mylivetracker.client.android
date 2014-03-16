package de.msk.mylivetracker.client.android.remoteaccess.commands;

import org.apache.commons.lang3.EnumUtils;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;

/**
 * classname: RemoteCmdConfig
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-06	origin.
 * 
 */
public class RemoteCmdConfig extends ARemoteCmdExecutor {

	public static String NAME = "cfg";
	public static enum Options {
		get;
	}
	public static String SYNTAX = 
		Options.get.name() + " [<section>]";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, SYNTAX, 2, 2, R.string.txRemoteCommand_Config);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return 
				(params.length == 1) || 
				((params.length == 2) &&
				 EnumUtils.isValidEnum(Options.class, params[0]) &&
				 PrefsRegistry.contains(params[1]));
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		Result result = null;
		if (params.length == 1) {
			result = new Result(true, PrefsRegistry.getSupportedPrefsAsPrettyStr());
		} else {
			result = new Result(true,
				PrefsRegistry.get(params[1]).getPrefsDumpAsStr(true));
		}
		return result;
	}
}
