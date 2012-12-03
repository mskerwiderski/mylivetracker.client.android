package de.msk.mylivetracker.client.android.remoteaccess;

import de.msk.mylivetracker.client.android.App.VersionDsc;

/**
 * SmsCmdGetAppVersion.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history 
 * 000 2012-11-23 initial.
 * 
 */
public class SmsCmdGetAppVersion extends AbstractSmsCmdExecutor {

	public SmsCmdGetAppVersion(String cmdName, String sender, String... params) {
		super(cmdName, sender, params);
	}

	@Override
	public CmdDsc getCmdDsc() {
		return new CmdDsc("", 0,0);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return VersionDsc.getVersionStr();
	}
}
