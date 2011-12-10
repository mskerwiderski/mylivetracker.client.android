package de.msk.mylivetracker.client.android.status;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.Preferences;

/**
 * PositionBuffer.
 * 
 * @author michael skerwiderski, (c)2011
 * 
 * @version 000
 * 
 * history
 * 000 initial 2011-08-11
 * 
 */
public class PositionBuffer {
		
	private static PositionBuffer positionBuffer = null;
	
	public static PositionBuffer get() {
		checkIfEnabled();
		if (positionBuffer == null) {
			positionBuffer = new PositionBuffer();
		}
		return positionBuffer;
	}
	public static void reset() {
		positionBuffer = null;
	}
	
	private Queue<String> internalBuffer = null;	
	
	private PositionBuffer() {
		this.internalBuffer = 
			new ConcurrentLinkedQueue<String>();
	}
		
	public static boolean isEnabled() {
		return !Preferences.get().getUplPositionBufferSize().isDisabled();
	}
	
	public static void checkIfEnabled() {
		if (!isEnabled()) {
			throw new RuntimeException("PositionBuffer is NOT enabled.");
		}
	}
	
	private static Integer getBufferSize() {
		Integer bufferSize = 
			Preferences.get().getUplPositionBufferSize().getSize();
		if ((bufferSize == null) || (bufferSize <= 0)) {
			throw new RuntimeException("invalid buffer size.");
		}
		return bufferSize;
	}
		
	public void add(String dataStr) {
		if (!StringUtils.isEmpty(dataStr)) {
			this.internalBuffer.add(dataStr);
			Integer bufferSize = getBufferSize();
			while (this.internalBuffer.size() > bufferSize) {
				this.internalBuffer.poll();
			}
		}		
	}
	
	public void clear() {
		this.internalBuffer.clear();
	}
	
	public int size() {
		return this.internalBuffer.size();
	}
		
	public String getAll(String lineSep) {
		if (this.internalBuffer.isEmpty()) return "";
		if (StringUtils.isEmpty(lineSep)) {
			lineSep = "\n";
		}
		String[] strs = (String[])
			this.internalBuffer.toArray(new String[0]);
		String res = "";
		if (strs.length == 1) {
			res = strs[0];
		} else {
			StringBuilder builder = new StringBuilder();
			for (int i=0; i < strs.length; i++) {
				builder.append(strs[i]);
				if (i < (strs.length -1)) {
					builder.append(lineSep);
				}
			}	
			res = builder.toString();
		}
		return res;
	}		
}
