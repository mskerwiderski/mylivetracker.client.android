package de.msk.mylivetracker.client.android.remoteaccess;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: ARemoteMessageCmdReceiver
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-15	origin.
 * 
 */
public abstract class ARemoteMessageCmdReceiver extends ARemoteCmdReceiver {

	public void onProcessRemoteMessageCmd(String sender, String message) {
		LogUtils.infoMethodIn(this.getClass(), "onProcessRemoteMessageCmd");
		if (StringUtils.isEmpty(sender)) {
			throw new IllegalArgumentException("sender must not be empty.");
		}
		if (StringUtils.isEmpty(message)) {
			throw new IllegalArgumentException("message must not be empty.");
		}
		if (StringUtils.startsWithIgnoreCase(message, "#" + App.getAppNameAbbr())) {
			RemoteAccessPrefs prefs = PrefsRegistry.get(RemoteAccessPrefs.class);
			String[] messageParts = StringUtils.split(message, ' ');
			if (messageParts.length < 3) {
				// do not process in case of an error before password has been checked.
				LogUtils.infoMethodState(this.getClass(), "onProcessCmd", "message check", "invalid syntax");
			} else if (!StringUtils.equals(messageParts[1], 
				prefs.getRemoteAccessPassword())) {
				// do not process in case of a wrong password.
				LogUtils.infoMethodState(this.getClass(), "onProcessCmd", "message check", "invalid password");
			} else {
				// remove indicator and password.
				messageParts = ArrayUtils.subarray(messageParts, 2, messageParts.length);
				super.onProcessRemoteCmd(sender, messageParts);
			}
		}
		LogUtils.infoMethodOut(this.getClass(), "onProcessRemoteMessageCmd");
	}
}
