package de.msk.mylivetracker.client.android.dropbox;

import android.app.Activity;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import de.msk.mylivetracker.client.android.R;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;

/**
 * classname: DropboxUtils
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
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
		DropboxPrefs prefs = PrefsRegistry.get(DropboxPrefs.class);
		dropboxAuthSession = 
			new AndroidAuthSession(appKeys, DROPBOX_ACCESS_TYPE);
		if (prefs.hasValidAccountAndTokens()) {
			String[] tokens = prefs.getTokens();
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
		PrefsRegistry.get(DropboxPrefs.class).resetAccountAndTokens();
		PrefsRegistry.save(DropboxPrefs.class);
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
				DropboxPrefs prefs = PrefsRegistry.get(DropboxPrefs.class);
		        try {
		        	dropboxAuthSession.finishAuthentication();
		        	AccessTokenPair dropboxTokenPair = dropboxAuthSession.getAccessTokenPair();
		        	prefs.setAccountAndTokens(
		        		dropboxApi.accountInfo().displayName, 
		        		dropboxTokenPair.key, dropboxTokenPair.secret);
		        	PrefsRegistry.save(DropboxPrefs.class);
		        	infoMsgId = R.string.txDropboxConnect_InfoConnectingDone;
		        } catch (DropboxException e) {
		        	infoMsgId = R.string.txDropboxConnect_InfoConnectingFailed;
		        } catch (IllegalStateException e) {
		        	infoMsgId = R.string.txDropboxConnect_InfoConnectingFailed;
		        }
		    } else {
		    	infoMsgId = R.string.txDropboxConnect_InfoConnectingFailed;
		    }
			authenticationStarted = false;
		}
		return infoMsgId;
	}
}
