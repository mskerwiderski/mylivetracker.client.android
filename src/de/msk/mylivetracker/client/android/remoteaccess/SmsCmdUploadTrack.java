package de.msk.mylivetracker.client.android.remoteaccess;

import de.msk.mylivetracker.client.android.dropbox.DropboxPrefs;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils.UploadFileResult;
import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.status.LogInfo;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.FileUtils.PathType;


/**
 * classname: SmsCmdUploadTrack
 * 
 * @author michael skerwiderski, (c)2014
 * @version 000
 * @since 1.6.0
 * 
 * history:
 * 000	2014-03-06	origin.
 * 
 */
public class SmsCmdUploadTrack extends ASmsCmdExecutor {

	public static String NAME = "upltrack";
	public static String SYNTAX = "";
	
	public static class CmdDsc extends ACmdDsc {

		public CmdDsc() {
			super(NAME, SYNTAX, 0, 0);
		}

		@Override
		public boolean matchesSyntax(String[] params) {
			return (params.length == 0);
		}
		
	}
	
	public SmsCmdUploadTrack(String sender, String... params) {
		super(new CmdDsc(), sender, params);
	}

	@Override
	public String executeCmdAndCreateSmsResponse(String... params) {
		String result = "";
		if (!PrefsRegistry.get(DropboxPrefs.class).hasValidAccountAndToken()) {
			result = ResponseCreator.getResultOfNotConnectedToDropbox();
		} else if (!LogInfo.logFileExists()) {
			result = ResponseCreator.getResultOfError("no track file exists");
		} else {
			String gpxFileName = LogInfo.createGpxFileNameOfCurrentTrack();
			LogInfo.createGpxFileOfCurrentTrack(gpxFileName);
			UploadFileResult uploadFileResult = DropboxUtils.uploadFile(gpxFileName);
			result = ResponseCreator.getResultOfUploadFile(uploadFileResult);
			FileUtils.fileDelete(gpxFileName, PathType.AppDataDir);
		}
		return result;
	}
}
