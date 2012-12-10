package de.msk.mylivetracker.client.android.trackexport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxFileSizeException;
import com.dropbox.client2.exception.DropboxIOException;
import com.dropbox.client2.exception.DropboxParseException;
import com.dropbox.client2.exception.DropboxPartialFileException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.dropbox.DropboxUtils;
import de.msk.mylivetracker.client.android.mainview.AbstractActivity;
import de.msk.mylivetracker.client.android.pro.R;
import de.msk.mylivetracker.client.android.status.LogInfo;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.LogUtils;
import de.msk.mylivetracker.client.android.util.dialog.AbstractProgressDialog;
import de.msk.mylivetracker.client.android.util.dialog.AbstractYesNoDialog;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

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
public class TrackExportActivity extends AbstractActivity {

	private static final class OnClickButtonBackListener implements OnClickListener {
		private TrackExportActivity activity;
		
		private OnClickButtonBackListener(TrackExportActivity activity) {
			this.activity = activity;
		}
		@Override
		public void onClick(View v) {			
			this.activity.finish();		
		}		
	}
	
	public static final class UploadTrackProgressDialog extends AsyncTask<Void, Long, Boolean> {
		private Activity activity;
		private UploadRequest request;
	    private final ProgressDialog dialog;
	    private String fileName;
	    private long fileLength;
	    private int errorMsgId;

	    public UploadTrackProgressDialog(Activity activity, String fileName) {
	    	if (activity == null) {
	    		throw new IllegalArgumentException("activity must not be null.");
	    	}
	    	if (StringUtils.isEmpty(fileName)) {
	    		throw new IllegalArgumentException("fileName must not be empty.");
	    	}
	    	this.activity = activity;
	    	this.fileName = fileName;
	        dialog = new ProgressDialog(this.activity);
	        dialog.setMax(100);
	        dialog.setMessage(App.getCtx().getText(R.string.lbTrackExport_UploadProgressDialog));
	        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        dialog.setProgress(0);
	        dialog.setButton(App.getCtx().getText(R.string.btCancel), 
	        	new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		                request.abort();
		            }
	        	});
	        dialog.show();
	    }

	    @Override
	    protected Boolean doInBackground(Void... params) {
	    	Boolean result = false;
	    	FileInputStream fis = null;
	        try {
	        	LogInfo.createGpxFileOfCurrentTrack(fileName);
	        	this.fileLength = FileUtils.getFileLength(fileName);
	            fis = App.get().openFileInput(fileName);
	            this.request = DropboxUtils.getDropboxApi().putFileOverwriteRequest(
	            	"/" + fileName, fis, this.fileLength,
	                new ProgressListener() {
	            		@Override
	            		public long progressInterval() {
	            			return 30;
	            		}
	            		@Override
	            		public void onProgress(long bytes, long total) {
	            			publishProgress(bytes);
	            		}
	            	});
	            if (this.request != null) {
	            	this.request.upload();
	            }
	            result = true;
	        } catch (DropboxUnlinkedException e) {
	            errorMsgId = R.string.txTrackExport_ErrorAuthenticationFailed;
	        } catch (DropboxFileSizeException e) {
	        	errorMsgId = R.string.txTrackExport_ErrorFileTooLarge;
	        } catch (DropboxPartialFileException e) {
	        	errorMsgId = R.string.txTrackExport_ErrorUploadCanceled;
	        } catch (DropboxServerException e) {
	        	errorMsgId = R.string.txTrackExport_ErrorUploadFailed;
	        } catch (DropboxIOException e) {
	        	errorMsgId = R.string.txTrackExport_ErrorUploadFailed;
	        } catch (DropboxParseException e) {
	        	errorMsgId = R.string.txTrackExport_ErrorUploadFailed;
	        } catch (DropboxException e) {
	        	errorMsgId = R.string.txTrackExport_ErrorUploadFailed;
	        } catch (FileNotFoundException e) {
	        	throw new RuntimeException(e);
	        } finally {
	        	FileUtils.closeStream(fis);
	        }
	        return result;
	    }

	    @Override
	    protected void onProgressUpdate(Long... progress) {
	    	LogUtils.always("progress uploading:" + progress[0]);
	    	LogUtils.always("fileLength:" + fileLength);
	        int percent = (int)(100.0*(double)progress[0]/fileLength + 0.5);
	        LogUtils.always("percent:" + percent);
	        dialog.setProgress(percent);
	    }

	    @Override
	    protected void onPostExecute(Boolean result) {
	        dialog.dismiss();
	        int infoMsgId = R.string.txTrackExport_InfoUploadTrackDone;
	        if (!result) {
	            infoMsgId = errorMsgId;
	        }
	        SimpleInfoDialog dlg = new SimpleInfoDialog(
				this.activity, infoMsgId);
				dlg.show();
	    }
	}

	private static final class UploadToDropboxDialog extends AbstractYesNoDialog {
		private Activity activity;
				
		public UploadToDropboxDialog(Activity activity) {
			super(activity, R.string.txTrackExport_QuestionUploadTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			UploadTrackProgressDialog dlg = 
				new UploadTrackProgressDialog(activity, 
					LogInfo.createGpxFileNameOfCurrentTrack());
			dlg.execute();
		}	
	}
	
	private static final class OnClickButtonUploadToDropbox implements OnClickListener {
		private Activity activity;
		
		private OnClickButtonUploadToDropbox(Activity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick(View view) {	
			if (!LogInfo.logFileExists()) {
				SimpleInfoDialog dlg = new SimpleInfoDialog(
					this.activity, R.string.txTrackExport_NoTrackExists);
				dlg.show();
			} else {
				UploadToDropboxDialog dlg = new UploadToDropboxDialog(
					this.activity);
				dlg.show();
			}
		}		
	}

	private static class ExportTrackProgressDialog extends AbstractProgressDialog<TrackExportActivity> {
		@Override
		public void doTask(TrackExportActivity activity) {
			LogInfo.exportGpxFileOfCurrentTrackToExternalStorage();
		}
	}
	
	private static final class ExportToExternalStorageTrackDialog extends AbstractYesNoDialog {
		private TrackExportActivity activity;
				
		public ExportToExternalStorageTrackDialog(TrackExportActivity activity) {
			super(activity, R.string.txTrackExport_QuestionExportTrack);
			this.activity = activity;			
		}

		@Override
		public void onYes() {			
			ExportTrackProgressDialog dlg = new ExportTrackProgressDialog();
			dlg.run(this.activity, R.string.lbTrackExport_ExportProgressDialog);
		}	
	}
	
	private static final class OnClickButtonExportToExternalStorage implements OnClickListener {
		private TrackExportActivity activity;
		
		private OnClickButtonExportToExternalStorage(TrackExportActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick(View view) {	
			if (!LogInfo.logFileExists()) {
				SimpleInfoDialog dlg = new SimpleInfoDialog(
					this.activity, R.string.txTrackExport_NoTrackExists);
				dlg.show();
			} else if (!FileUtils.externalStorageUsable()) {
				SimpleInfoDialog dlg = new SimpleInfoDialog(
					this.activity, R.string.txTrackExport_NoExternalStorageAvailable);
				dlg.show();
			} else {
				ExportToExternalStorageTrackDialog dlg = new ExportToExternalStorageTrackDialog(
					this.activity);
				dlg.show();
			}
		}		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_export);
        this.setTitle(R.string.tiTrackExport);

        Button btTrackExport_UploadToDropbox = (Button)findViewById(R.id.btTrackExport_UploadToDropbox);
        Button btTrackExport_ExportToExternalStorage = (Button)findViewById(R.id.btTrackExport_ExportToExternalStorage);
        Button btTrackExport_Back = (Button)findViewById(R.id.btTrackExport_Back);
        
        btTrackExport_UploadToDropbox.setOnClickListener(
			new OnClickButtonUploadToDropbox(this));
        btTrackExport_ExportToExternalStorage.setOnClickListener(
			new OnClickButtonExportToExternalStorage(this));
        btTrackExport_Back.setOnClickListener(
			new OnClickButtonBackListener(this));
    }
}
