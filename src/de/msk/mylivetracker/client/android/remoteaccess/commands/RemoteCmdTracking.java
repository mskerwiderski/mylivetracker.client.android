package de.msk.mylivetracker.client.android.remoteaccess.commands;

import java.util.Arrays;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.appcontrol.AppControl;
import de.msk.mylivetracker.client.android.ontrackphonetracker.R;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;
import de.msk.mylivetracker.client.android.remoteaccess.ResponseCreator;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: RemoteCmdTracking
 * 
 * @author michael skerwiderski, (c)2012
 * @version 001
 * @since 1.5.0
 * 
 * history:
 * 001	2014-02-28	revised for v1.6.0.
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class RemoteCmdTracking extends ARemoteCmdExecutor {

	public static final String NAME = "track";
	public static enum Options {
		reset, start, stop, info;
	}
	public static final String SYNTAX = 
		CmdDsc.createSyntaxStr(Options.class); 
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, 
				SYNTAX, 1, 1, 
				R.string.txRemoteCommand_Tracking, 
				false);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			boolean matches = (params.length == 1);
			matches = EnumUtils.isValidEnum(
				Options.class, params[0]);
			return matches;
		}
		
	}

	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		LogUtils.infoMethodIn(this.getClass(), "executeCmdAndCreateSmsResponse", Arrays.toString(params));
		String cmd = params[0];
		Result result = null; 
		if (StringUtils.equals(cmd, Options.reset.name())) {
			AppControl.resetTrack();
			result = new Result(true, "track resetted");
		} else if (StringUtils.equals(cmd, Options.start.name())) {
			boolean running = AppControl.trackIsRunning();
			if (!running) {
				AppControl.startTrack();
			}
			result = new Result(true, (!running ? "track started" : "track already running"));
		} else if (StringUtils.equals(cmd, Options.stop.name())) {
			boolean running = AppControl.trackIsRunning();
			if (running) {
				AppControl.stopTrack();
			}
			result = new Result(true, (running ? "track stopped" : "track not running"));
		}  else if (StringUtils.equals(cmd, Options.info.name())) {
			result = ResponseCreator.getResultOfTrackInfo();
		}
		LogUtils.infoMethodOut(this.getClass(), "executeCmdAndCreateSmsResponse", result);
		return result;
	}
}
