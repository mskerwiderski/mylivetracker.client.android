package de.msk.mylivetracker.client.android.remoteaccess;

/**
 * classname: SimpleCmdDsc
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-02-28	origin.
 * 
 */
public class SimpleCmdDsc extends ACmdDsc {

	public SimpleCmdDsc() {
		super("simple", "", 0, 0);
	}

	@Override
	public boolean matchesSyntax(String[] params) {
		return true;
	}

}
