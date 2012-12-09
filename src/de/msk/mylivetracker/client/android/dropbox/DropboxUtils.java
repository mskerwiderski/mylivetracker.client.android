package de.msk.mylivetracker.client.android.dropbox;

import android.app.Activity;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.pro.R;

/**
 * DropboxUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history
 * 000 	2012-12-05 initial. 
 * 
 */
public class DropboxUtils {

	final static private String DROPBOX_APP_KEY = "a73z7br0b53qrbj";
	final static private String DROPBOX_APP_SECRET = "vk6i3bpo6uv1ra6";
	final static private AccessType DROPBOX_ACCESS_TYPE = AccessType.APP_FOLDER;

	private static AppKeyPair appKeys = new AppKeyPair(
		DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
	
	private static boolean authenticationStarted = false;
	
	private static AndroidAuthSession dropboxAuthSession = null;
	static {
		Preferences prefs = Preferences.get();
		dropboxAuthSession = 
			new AndroidAuthSession(appKeys, DROPBOX_ACCESS_TYPE);
		if (prefs.hasValidDropboxDetails()) {
			String[] tokens = prefs.getDropboxTokens();
			dropboxAuthSession.setAccessTokenPair(
				new AccessTokenPair(tokens[0], tokens[1]));
		}
	}
	
	private static DropboxAPI<AndroidAuthSession> dropboxApi = null;
	static {
		dropboxApi = new DropboxAPI<AndroidAuthSession>(dropboxAuthSession);
	}
	
	public static DropboxAPI<AndroidAuthSession> getDropboxApi() {
		return dropboxApi;
	}
	
	public static void releaseConnection() {
		dropboxAuthSession.unlink();
		Preferences.get().resetDropboxTokens();
		Preferences.save();
	}
	
	public static void startAuthentication(Activity activity) {
		if (activity == null) {
			throw new IllegalArgumentException("activity must not be null.");
		}
		dropboxAuthSession.unlink();
		dropboxAuthSession.startAuthentication(activity);
		authenticationStarted = true;
	}
	
	public static Integer completeAuthenticationIfStarted() {
		Integer infoMsgId = null;
		if (authenticationStarted) {
			if (dropboxAuthSession.authenticationSuccessful()) {
				Preferences prefs = Preferences.get();
		        try {
		        	dropboxAuthSession.finishAuthentication();
		        	AccessTokenPair dropboxTokenPair = dropboxAuthSession.getAccessTokenPair();
		        	prefs.setDropboxDetails(
		        		dropboxApi.accountInfo().displayName, 
		        		dropboxTokenPair.key, dropboxTokenPair.secret);
		        	Preferences.save();
		        	infoMsgId = R.string.txConnectToDropbox_InfoConnectingDone;
		        } catch (DropboxException e) {
		        	infoMsgId = R.string.txConnectToDropbox_InfoConnectingFailed;
		        } catch (IllegalStateException e) {
		        	infoMsgId = R.string.txConnectToDropbox_InfoConnectingFailed;
		        }
		    } else {
		    	infoMsgId = R.string.txConnectToDropbox_InfoConnectingFailed;
		    }
			authenticationStarted = false;
		}
		return infoMsgId;
	}
}
