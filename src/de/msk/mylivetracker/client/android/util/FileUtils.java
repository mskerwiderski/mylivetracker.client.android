package de.msk.mylivetracker.client.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;

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
	
	private static String getPathFileName(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("fileName must not be empty.");
		}
		String pathFileName = App.get().getFilesDir().getAbsolutePath();
		if (!StringUtils.endsWith(pathFileName, "/")) {
			pathFileName += "/";
		}
		return pathFileName + fileName;
	}
	
	public static boolean fileExists(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("fileName must not be empty.");
		}
		File file = new File(getPathFileName(fileName));
		return file.exists();
	}
	
	public static long getFileLength(String fileName) {
		if (StringUtils.isEmpty(fileName)) {
			throw new IllegalArgumentException("fileName must not be empty.");
		}
		File file = new File(getPathFileName(fileName));
		return file.length();
	}
	
	public static void copy(String srcFileName, String destFileName) {
		if (StringUtils.isEmpty(srcFileName)) {
			throw new IllegalArgumentException("srcFileName must not be empty.");
		}
		if (StringUtils.isEmpty(destFileName)) {
			throw new IllegalArgumentException("destFileName must not be empty.");
		}
		File src = new File(getPathFileName(srcFileName));
		File dst = new File(getPathFileName(destFileName));
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dst);
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
