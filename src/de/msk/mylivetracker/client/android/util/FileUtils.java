package de.msk.mylivetracker.client.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;

import android.os.Environment;
import de.msk.mylivetracker.client.android.App;

/**
 * FileUtils.
 * 
 * @author michael skerwiderski, (c)2012
 * 
 * @version 000
 * 
 * history 
 * 000	2012-12-06 initial.
 * 
 */
public class FileUtils {
	
	public static boolean externalStorageUsable() {
		boolean externalStorageAvailable = false;
		boolean externalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		    externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
		    externalStorageAvailable = true;
		    externalStorageWriteable = false;
		} else {
		    externalStorageAvailable = externalStorageWriteable = false;
		}
		return externalStorageAvailable && externalStorageWriteable;
	}

	public enum PathType {
		AppDataDir,
		AppSharedPrefsDir,
		ExternalStorage
	}
	
	private static String getPathFileName(String fileName, PathType pathType) {
		if (StringUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("fileName must not be empty!");
		}
		if (pathType == null) {
			throw new IllegalArgumentException("pathType must not be null!");
		}
		if (pathType.equals(PathType.ExternalStorage) && !externalStorageUsable()) {
			throw new IllegalArgumentException("external storage is not available!");
		}
		String pathFileName = App.get().getFilesDir().getAbsolutePath();
		if (pathType.equals(PathType.ExternalStorage)) {
			String mltDir = Environment.getExternalStorageDirectory().getAbsolutePath();
			if (!StringUtils.endsWith(mltDir, "/")) {
				mltDir += "/";
			}
			mltDir += "data/" + App.getAppName() + "/";
			new File(mltDir).mkdirs();
			pathFileName = mltDir;
		} else if (pathType.equals(PathType.AppSharedPrefsDir)) {
			pathFileName = StringUtils.replace(pathFileName, "files", "shared_prefs");
		}
		if (!StringUtils.endsWith(pathFileName, "/")) {
			pathFileName += "/";
		}
		pathFileName += fileName;
		return pathFileName;
	}
	
	public static boolean fileExists(String fileName, PathType srcPathType) {
		if (StringUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("fileName must not be empty!");
		}
		if (srcPathType == null) {
			throw new IllegalArgumentException("srcPathType must not be null!");
		}
		File file = new File(getPathFileName(fileName, srcPathType));
		return file.exists();
	}
	
	public static void fileDelete(String fileName, PathType srcPathType) {
		if (StringUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("fileName must not be empty!");
		}
		if (srcPathType == null) {
			throw new IllegalArgumentException("srcPathType must not be null!");
		}
		File file = new File(getPathFileName(fileName, srcPathType));
		if (file.exists()) {
			file.delete();
		}
		if (file.exists()) {
			throw new RuntimeException("deleting file failed!");
		}
	}
	
	public static long getFileLength(String fileName, PathType srcPathType) {
		if (StringUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("fileName must not be empty!");
		}
		if (srcPathType == null) {
			throw new IllegalArgumentException("srcPathType must not be null!");
		}
		File file = new File(getPathFileName(fileName, srcPathType));
		return file.length();
	}
	
	public static void copy(String srcFileName, PathType srcPathType, 
		String destFileName, PathType destPathType) {
		if (StringUtils.isEmpty(srcFileName)) {
			throw new IllegalArgumentException("srcFileName must not be empty!");
		}
		if (srcPathType == null) {
			throw new IllegalArgumentException("srcPathType must not be null!");
		}
		if (StringUtils.isEmpty(destFileName)) {
			throw new IllegalArgumentException("destFileName must not be empty!");
		}
		if (destPathType == null) {
			throw new IllegalArgumentException("destPathType must not be null!");
		}
		copyAux(
			new File(getPathFileName(srcFileName, srcPathType)), 
			new File(getPathFileName(destFileName, destPathType)));
	}
	
	private static void copyAux(File src, File dest) {
		if (src == null) {
			throw new IllegalArgumentException("src must not be null!");
		}
		if (dest == null) {
			throw new IllegalArgumentException("dest must not be null!");
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dest);
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			closeStream(out);
			closeStream(in);
		}
	}
	
	public static void closeStream(InputStream is) {
		if (is == null) return;
		try {
			is.close();
		} catch (IOException e) {
			// noop.
		}
	}
	public static void closeStream(OutputStream os) {
		if (os == null) return;
		try {
			os.close();
		} catch (IOException e) {
			// noop.
		}
	}
}
