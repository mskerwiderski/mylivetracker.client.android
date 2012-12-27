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
 * 001	2012-12-25 	revised for v1.5.x.
 * 000 	2012-11-23 	initial.
 * 
 */
public class SmsCmdGetAppVersion extends ASmsCmdExecutor {

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
