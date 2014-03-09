package de.msk.mylivetracker.client.android.trackexport;

import java.io.Serializable;

import org.apache.commons.lang.BooleanUtils;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: TrackExportPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2013-01-01	revised for v1.5.x.
 * 
 */
public class TrackExportPrefs extends APrefs implements Serializable {
	
	private static final long serialVersionUID = -7140763631915081783L;

	public static final int VERSION = 1;
	
	private String filenamePrefix;
	private boolean filenameAppendTrackName;
	private boolean filenameAppendTimestampOfFirstPosition;
	private boolean filenameAppendTimestampOfLastPosition;
	private boolean filenameAppendSequenceNumber;
	private int filenameNextSequenceNumber;
	
	@Override
	public int getVersion() {
		return VERSION;
	}	
	@Override
	public void initWithDefaults() {
		this.filenamePrefix = "Export";
		this.filenameAppendTrackName = true;
		this.filenameAppendTimestampOfFirstPosition = false;
		this.filenameAppendTimestampOfLastPosition = false;
		this.filenameAppendSequenceNumber = true;
		this.filenameNextSequenceNumber = 0;
	}
	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}
	
	public void incNextSequenceNumber() {
		this.filenameNextSequenceNumber++;
		if (this.filenameNextSequenceNumber > 
			this.getMaxSequenceNumber()) {
			this.filenameNextSequenceNumber = 0;
		}
	}
	
	public int getMaxDigitsOfSequenceNumber() {
		return 6;
	}
	
	public int getMaxSequenceNumber() {
		return (int)999999;
	}
	
	public String getFilenamePrefix() {
		return filenamePrefix;
	}
	public void setFilenamePrefix(String filenamePrefix) {
		this.filenamePrefix = filenamePrefix;
	}
	public boolean isFilenameAppendTrackName() {
		return filenameAppendTrackName;
	}
	public void setFilenameAppendTrackName(boolean filenameAppendTrackName) {
		this.filenameAppendTrackName = filenameAppendTrackName;
	}
	public boolean isFilenameAppendTimestampOfFirstPosition() {
		return filenameAppendTimestampOfFirstPosition;
	}
	public void setFilenameAppendTimestampOfFirstPosition(
			boolean filenameAppendTimestampOfFirstPosition) {
		this.filenameAppendTimestampOfFirstPosition = filenameAppendTimestampOfFirstPosition;
	}
	public boolean isFilenameAppendTimestampOfLastPosition() {
		return filenameAppendTimestampOfLastPosition;
	}
	public void setFilenameAppendTimestampOfLastPosition(
			boolean filenameAppendTimestampOfLastPosition) {
		this.filenameAppendTimestampOfLastPosition = filenameAppendTimestampOfLastPosition;
	}
	public boolean isFilenameAppendSequenceNumber() {
		return filenameAppendSequenceNumber;
	}
	public void setFilenameAppendSequenceNumber(boolean filenameAppendSequenceNumber) {
		this.filenameAppendSequenceNumber = filenameAppendSequenceNumber;
	}
	public int getFilenameNextSequenceNumber() {
		return filenameNextSequenceNumber;
	}
	public void setFilenameNextSequenceNumber(int filenameNextSequenceNumber) {
		this.filenameNextSequenceNumber = filenameNextSequenceNumber;
	}
	@Override
	public String getShortName() {
		return "trackexport";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("TrackExportPrefs", 
			new ConfigPair[] {
				new ConfigPair("filenamePrefix", this.filenamePrefix),
				new ConfigPair("filenameAppendTrackName", 
					BooleanUtils.toStringTrueFalse(this.filenameAppendTrackName)),
				new ConfigPair("filenameAppendTimestampOfFirstPosition", 
					BooleanUtils.toStringTrueFalse(this.filenameAppendTimestampOfFirstPosition)),
				new ConfigPair("filenameAppendTimestampOfLastPosition", 
					BooleanUtils.toStringTrueFalse(this.filenameAppendTimestampOfLastPosition)),
				new ConfigPair("filenameAppendSequenceNumber", 
					BooleanUtils.toStringTrueFalse(this.filenameAppendSequenceNumber)),
				new ConfigPair("filenameNextSequenceNumber", 
					String.valueOf(this.filenameNextSequenceNumber)),
		});
	}
	@Override
	public String toString() {
		return "TrackExportPrefs [filenamePrefix=" + filenamePrefix
			+ ", filenameAppendTrackName=" + filenameAppendTrackName
			+ ", filenameAppendTimestampOfFirstPosition="
			+ filenameAppendTimestampOfFirstPosition
			+ ", filenameAppendTimestampOfLastPosition="
			+ filenameAppendTimestampOfLastPosition
			+ ", filenameAppendSequenceNumber="
			+ filenameAppendSequenceNumber
			+ ", filenameNextSequenceNumber=" + filenameNextSequenceNumber
			+ "]";
	}
}
