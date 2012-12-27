package de.msk.mylivetracker.client.android.status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang.StringUtils;

import de.msk.mylivetracker.client.android.preferences.PrefsRegistry;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;

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
public class PositionBufferInfo extends AbstractInfo implements Serializable {
		
	private static final long serialVersionUID = -3238245644001859556L;
	
	private static PositionBufferInfo positionBufferInfo = null;
	
	public static PositionBufferInfo get() {
		if (positionBufferInfo == null) {
			positionBufferInfo = new PositionBufferInfo();
		}
		return positionBufferInfo;
	}
	public static void reset() {
		positionBufferInfo = null;
	}
	public static void set(PositionBufferInfo positionBufferInfo) {
		PositionBufferInfo.positionBufferInfo = positionBufferInfo;
	}
	
	private Queue<String> internalBuffer = null;	
	
	private PositionBufferInfo() {
		this.internalBuffer = 
			new ConcurrentLinkedQueue<String>();
	}
		
	public static boolean isEnabled() {
		return !PrefsRegistry.get(ProtocolPrefs.class).
			getUplPositionBufferSize().isDisabled();
	}
	
	private static Integer getBufferSize() {
		Integer bufferSize = 
			PrefsRegistry.get(ProtocolPrefs.class).
			getUplPositionBufferSize().getSize();
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
		
	public List<String> getAsList() {
		return new ArrayList<String>(this.internalBuffer);
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
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PositionBufferInfo [internalBuffer=")
			.append(internalBuffer).append("]");
		return builder.toString();
	}			
}
