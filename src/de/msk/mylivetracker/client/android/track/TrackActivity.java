package de.msk.mylivetracker.client.android.track;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.google.gson.Gson;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.preferences.Preferences;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.status.StatusDeSerializer;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;

/**
 * TrackActivity.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 001
 * 
 * history
 * 000 	2012-12-04 initial.
 * 
 */
public class TrackActivity extends AbstractActivity {

	final static private String DROPBOX_APP_KEY = "a73z7br0b53qrbj";
	final static private String DROPBOX_APP_SECRET = "vk6i3bpo6uv1ra6";
	final static private AccessType DROPBOX_ACCESS_TYPE = AccessType.APP_FOLDER;
	private static AppKeyPair appKeys = new AppKeyPair(DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
	private static AndroidAuthSession session = new AndroidAuthSession(appKeys, DROPBOX_ACCESS_TYPE);
	private static DropboxAPI<AndroidAuthSession> dropboxApi = new DropboxAPI<AndroidAuthSession>(session);
	
	private static final class OnClickButtonCancelListener implements OnClickListener {
		private TrackActivity activity;
		
		private OnClickButtonCancelListener(TrackActivity activity) {
			this.activity = activity;
		}
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	private static final class LinkToDropboxDialog extends AbstractYesNoDialog {
		private Activity activity;
		private Preferences preferences;
				
		public LinkToDropboxDialog(Activity activity,
			Preferences preferences) {
			super(activity, R.string.txPrefsOther_QuestionResetToFactoryDefaults);
			this.activity = activity;
			this.preferences = preferences;
		}

		@Override
		public void onYes() {			
			if (!this.preferences.hasValidDropboxTokens()) {
				dropboxApi.getSession().startAuthentication(this.activity);
			}
		}	
	}
	
	private static final class OnClickButtonLinkToDropbox implements OnClickListener {
		private Activity activity;
		private Preferences preferences;

		private OnClickButtonLinkToDropbox(Activity activity,
			Preferences preferences) {
			this.activity = activity;
			this.preferences = preferences;
		}
		
		@Override
		public void onClick(View view) {		
			LinkToDropboxDialog dlg = new LinkToDropboxDialog(
				this.activity, this.preferences);
			dlg.show();			
		}		
	}

	private static final class UploadToDropboxDialog extends AbstractYesNoDialog {
		
		@SuppressWarnings("unused")
		private Activity activity;
		private Preferences preferences;
		
		public UploadToDropboxDialog(Activity activity, Preferences preferences) {
			super(activity, R.string.txPrefsOther_QuestionResetToFactoryDefaults);
			this.activity = activity;			
			this.preferences = preferences;
		}

		@Override
		public void onYes() {			
			if ((dropboxApi != null) && this.preferences.hasValidDropboxTokens()) {
				String[] tokens = this.preferences.getDropboxTokens();
				AccessTokenPair accessTokenPair = new AccessTokenPair(tokens[0], tokens[1]);
				dropboxApi.getSession().setAccessTokenPair(accessTokenPair);
				// Uploading content.
				FileInputStream inputStream = null;
				try {
					String pathFilename = App.get().getFilesDir().getAbsolutePath();
					if (!StringUtils.endsWith(pathFilename, "/")) {
						pathFilename += "/";
					}
					pathFilename += FILENAME;
					File file = new File(pathFilename);
				    inputStream = App.get().openFileInput(FILENAME);
				    @SuppressWarnings("unused")
					Entry newEntry = dropboxApi.putFile("/" + FILENAME, inputStream,
						file.length(), null, null);
				} catch (DropboxUnlinkedException e) {
					throw new RuntimeException(e);
				} catch (DropboxException e) {
					throw new RuntimeException(e);
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				} finally {
				    if (inputStream != null) {
				        try {
				            inputStream.close();
				        } catch (IOException e) {}
				    }
				}
			}
		}	
	}
	
	private static final class OnClickButtonUploadToDropbox implements OnClickListener {
		private Activity activity;
		private Preferences preferences;
		
		private OnClickButtonUploadToDropbox(Activity activity,
			Preferences preferences) {
			this.activity = activity;
			this.preferences = preferences;
		}
		
		@Override
		public void onClick(View view) {		
			UploadToDropboxDialog dlg = new UploadToDropboxDialog(
				this.activity, this.preferences);
			dlg.show();			
		}		
	}

	@Override
	protected void onResume() {
		super.onResume();
		Preferences prefs = Preferences.get();
		if (!prefs.hasValidDropboxTokens() && dropboxApi.getSession().authenticationSuccessful()) {
	        try {
	            // MANDATORY call to complete auth.
	            // Sets the access token on the session
	        	dropboxApi.getSession().finishAuthentication();
	        	AccessTokenPair dropboxTokenPair = dropboxApi.getSession().getAccessTokenPair();
	        	prefs.setDropboxTokens(dropboxTokenPair.key, dropboxTokenPair.secret);
	        	LogUtils.always("KEY:" + dropboxTokenPair.key);
	        	LogUtils.always("SECRET:" + dropboxTokenPair.secret);
	        	Preferences.save();
	        } catch (IllegalStateException e) {
	        	throw new RuntimeException(e);
	        }
	    }
	}

	private static final String FILENAME = "mylivetracker-test.txt";
	private static String FILE_CONTENT = "UNKNOWN";
	private static void createFile() {
		FileOutputStream fos;
		try {
			App.get().deleteFile(FILENAME);
			fos = App.get().openFileOutput(FILENAME, Context.MODE_PRIVATE);
			Gson gson = StatusDeSerializer.get();
			FILE_CONTENT = gson.toJson(TrackStatus.get());
			fos.write(FILE_CONTENT.getBytes());
			fos.close();
		} catch (Exception e) {
			LogUtils.always("createFile failed: " + e.toString());
		}
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track);
        this.setTitle(R.string.tiTrack);

        Preferences prefs = Preferences.get();
        
        createFile();
        
        Button btTrack_LinkToDropbox = (Button)findViewById(R.id.btTrack_LinkToDropbox);
        Button btTrack_UploadToDropbox = (Button)findViewById(R.id.btTrack_UploadToDropbox);
        Button btTrack_Cancel = (Button)findViewById(R.id.btTrack_Cancel);
        
        btTrack_LinkToDropbox.setOnClickListener(
			new OnClickButtonLinkToDropbox(this, prefs));
        btTrack_UploadToDropbox.setOnClickListener(
			new OnClickButtonUploadToDropbox(this, prefs));
        btTrack_Cancel.setOnClickListener(
			new OnClickButtonCancelListener(this));
    }
}
