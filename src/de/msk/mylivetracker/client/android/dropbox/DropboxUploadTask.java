package de.msk.mylivetracker.client.android.dropbox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.lang.StringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

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
import de.msk.mylivetracker.client.android.liontrack.R;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.FileUtils.PathType;
import de.msk.mylivetracker.client.android.util.dialog.SimpleInfoDialog;

/**
 * classname: DropboxUploadTask
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
public class DropboxUploadTask extends AsyncTask<Void, Long, Boolean> {
	private Activity activity;
	private LabelDsc labelDsc;
	private DoInBackgroundInitializer doInBackgroundInitializer;
	private UploadRequest request;
    private ProgressDialog dialog;
    private String fileName;
    private long fileLength;
    private int resultMsgId;
    private String addResultInfo;

    public static class LabelDsc {
    	public int labelIdUploadInProgress = R.string.lbDropbox_UploadInProgress;
    	public int labelIdUploadDone = R.string.lbDropbox_UploadDone;
    	public int labelIdUploadCanceled = R.string.lbDropbox_UploadCanceled;
    	public int labelIdUploadRejected = R.string.lbDropbox_UploadRejected;
    	public int labelIdAuthFailed = R.string.lbDropbox_AuthFailed;
    	public int labelIdFileTooLargeToUpload = R.string.lbDropbox_FileTooLargeToUpload;
    	public int labelIdNetworkError = R.string.lbDropbox_NetworkError;
    	public int labelIdDropboxError = R.string.lbDropbox_DropboxError;
    	public int labelIdUnknownError = R.string.lbDropbox_UnknownError;
    	public LabelDsc() {
    	}
		public LabelDsc(
			int labelIdUploadInProgress, int labelIdUploadDone,
			int labelIdUploadCanceled, int labelIdUploadRejected,
			int labelIdAuthFailed, int labelIdFileTooLargeToUpload,
			int labelIdNetworkError, int labelIdDropboxError,
			int labelIdUnknownError) {
			this.labelIdUploadInProgress = labelIdUploadInProgress;
			this.labelIdUploadDone = labelIdUploadDone;
			this.labelIdUploadCanceled = labelIdUploadCanceled;
			this.labelIdUploadRejected = labelIdUploadRejected;
			this.labelIdAuthFailed = labelIdAuthFailed;
			this.labelIdFileTooLargeToUpload = labelIdFileTooLargeToUpload;
			this.labelIdNetworkError = labelIdNetworkError;
			this.labelIdDropboxError = labelIdDropboxError;
			this.labelIdUnknownError = labelIdUnknownError;
		}
    }

    public interface DoInBackgroundInitializer {
    	public void init(String fileName);
    };
    
    public static void execute(Activity activity, LabelDsc labelDsc, String fileName,
    	DoInBackgroundInitializer doInBackgroundInitializer) {
		DropboxUploadTask dlg = new DropboxUploadTask(
			activity, labelDsc, fileName, doInBackgroundInitializer);
		dlg.execute();
	}
    
    public DropboxUploadTask(
    	Activity activity, LabelDsc labelDsc, String fileName,
    	DoInBackgroundInitializer doInBackgroundInitializer) {
    	this.init(activity, labelDsc, fileName, doInBackgroundInitializer);
    }
    
    private void init(Activity activity, LabelDsc labelDsc, String fileName,
    	DoInBackgroundInitializer doInBackgroundInitializer) {
    	if (activity == null) {
    		throw new IllegalArgumentException("activity must not be null.");
    	}
    	if (StringUtils.isEmpty(fileName)) {
    		throw new IllegalArgumentException("fileName must not be empty.");
    	}
    	this.activity = activity;
    	if (labelDsc != null) {
    		this.labelDsc = labelDsc;
    	} else {
    		this.labelDsc = new LabelDsc();
    	}
    	this.fileName = fileName;
    	this.doInBackgroundInitializer = doInBackgroundInitializer;
        dialog = new ProgressDialog(this.activity);
        dialog.setMax(100);
        dialog.setMessage(App.getCtx().getText(this.labelDsc.labelIdUploadInProgress));
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
        	if (this.doInBackgroundInitializer != null) {
        		this.doInBackgroundInitializer.init(fileName);
        	}
        	this.fileLength = FileUtils.getFileLength(fileName, PathType.AppDataDir);
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
            resultMsgId = labelDsc.labelIdAuthFailed;
        } catch (DropboxFileSizeException e) {
        	resultMsgId = labelDsc.labelIdFileTooLargeToUpload;
        } catch (DropboxPartialFileException e) {
        	resultMsgId = labelDsc.labelIdUploadCanceled;
        } catch (DropboxServerException e) {
        	resultMsgId = labelDsc.labelIdUploadRejected;
        	String errorMsg = e.body.userError;
            if (errorMsg == null) {
                errorMsg = e.body.error;
            }
            this.addResultInfo = errorMsg;
        } catch (DropboxIOException e) {
        	resultMsgId = labelDsc.labelIdNetworkError;
        } catch (DropboxParseException e) {
        	resultMsgId = labelDsc.labelIdDropboxError;
        } catch (DropboxException e) {
        	resultMsgId = labelDsc.labelIdUnknownError;
        } catch (FileNotFoundException e) {
        	throw new RuntimeException(e);
        } finally {
        	FileUtils.closeStream(fis);
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int percent = (int)(100.0*(double)progress[0]/fileLength + 0.5);
        dialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
        String msg = App.getCtx().getText(
        	labelDsc.labelIdUploadDone).toString();
        if (!result) {
            msg = App.getCtx().getText(
            	this.resultMsgId).toString();
            if (!StringUtils.isEmpty(this.addResultInfo)) {
            	msg += " " + this.addResultInfo;
            }
        }
        SimpleInfoDialog.show(this.activity, msg);
    }
}
