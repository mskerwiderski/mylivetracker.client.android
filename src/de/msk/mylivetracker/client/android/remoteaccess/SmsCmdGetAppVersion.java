package de.msk.mylivetracker.client.android.remoteaccess;

import de.msk.mylivetracker.client.android.util.VersionUtils;

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

	public SmsCmdGetAppVersion(String sender, String[] params) {
		super(sender, params);
	}

	@Override
	public ParamsDsc getParamsDsc() {
		return new ParamsDsc(0, 0);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		return VersionUtils.get().getVersionStr();
	}
}
