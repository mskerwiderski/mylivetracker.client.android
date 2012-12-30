package de.msk.mylivetracker.client.android.remoteaccess;

import de.msk.mylivetracker.client.android.App.VersionDsc;

/**
 * classname: SmsCmdGetAppVersion
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
