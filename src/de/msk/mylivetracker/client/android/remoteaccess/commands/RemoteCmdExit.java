package de.msk.mylivetracker.client.android.remoteaccess.commands;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.mainview.MainActivity;
import de.msk.mylivetracker.client.android.mainview.MainDetailsActivity;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdDsc;
import de.msk.mylivetracker.client.android.remoteaccess.ARemoteCmdExecutor;


/**
 * classname: RemoteCmdExit
 * 
 * @author michael skerwiderski, (c)2014
 * @version 001
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-18	origin.
 * 
 */
public class RemoteCmdExit extends ARemoteCmdExecutor {

	public static String NAME = "exit";
	public static String SYNTAX = "";
	
	public static class CmdDsc extends ARemoteCmdDsc {
		public CmdDsc() {
			super(NAME, SYNTAX, 0, 0, 
				R.string.txRemoteCommand_Exit);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
	}

	private static class ExitTask implements Runnable {
		@Override
		public void run() {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
			if (MainDetailsActivity.isActive()) {
				MainDetailsActivity.close();
				while (MainDetailsActivity.isActive()) {
					try { Thread.sleep(50); } catch(Exception e) {};
				}
			}
			MainActivity.exit();
		}
	}
	
	@Override
	public Result executeCmdAndCreateResponse(String... params) {
		MainActivity.get().runOnUiThread(new ExitTask());
		return new Result(true, "application exited");
	}
}
