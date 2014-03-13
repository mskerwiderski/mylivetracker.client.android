package de.msk.mylivetracker.client.android.auto;

import java.io.Serializable;

import org.apache.commons.lang3.BooleanUtils;

import de.msk.mylivetracker.client.android.preferences.APrefs;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.ConfigPair;
import de.msk.mylivetracker.client.android.preferences.PrefsDumper.PrefsDump;

/**
 * classname: AutoPrefs
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
@Deprecated
public class AutoPrefs extends APrefs implements Serializable {

	private static final long serialVersionUID = 5728625066768301415L;

	public static final int VERSION = 1;

	public enum AutoModeResetTrackMode {
		Never("never", 0), 
		NextDay("next day", -1),
		Hour1("1 hour", 1),
		Hours2("2 hours", 2), 
		Hours4("4 hours", 4), 
		Hours8("8 hours", 8), 
		Hours24("24 hours", 24);

		private String dsc;
		private int val;

		private AutoModeResetTrackMode(String dsc, int val) {
			this.dsc = dsc;
			this.val = val;
		}

		public String getDsc() {
			return dsc;
		}

		public int getVal() {
			return val;
		}
	};

	private boolean autoModeEnabled;
	private AutoModeResetTrackMode autoModeResetTrackMode;
	private boolean autoStartEnabled;

	@Override
	public int getVersion() {
		return VERSION;
	}

	@Override
	public void initWithDefaults() {
		this.autoModeEnabled = false;
		this.autoModeResetTrackMode = AutoModeResetTrackMode.NextDay;
		this.autoStartEnabled = false;
	}

	@Override
	public void initWithValuesOfOldVersion(int foundVersion, String foundGsonStr) {
		// noop.
	}

	public boolean isAutoModeEnabled() {
		return autoModeEnabled;
	}

	public void setAutoModeEnabled(boolean autoModeEnabled) {
		this.autoModeEnabled = autoModeEnabled;
	}

	public AutoModeResetTrackMode getAutoModeResetTrackMode() {
		return autoModeResetTrackMode;
	}

	public void setAutoModeResetTrackMode(
			AutoModeResetTrackMode autoModeResetTrackMode) {
		this.autoModeResetTrackMode = autoModeResetTrackMode;
	}

	public boolean isAutoStartEnabled() {
		return autoStartEnabled;
	}

	public void setAutoStartEnabled(boolean autoStartEnabled) {
		this.autoStartEnabled = autoStartEnabled;
	}
	@Override
	public String getShortName() {
		return "auto";
	}
	@Override
	public PrefsDump getPrefsDump() {
		return new PrefsDump("AutoPrefs", 
			new ConfigPair[] {
				new ConfigPair("autoStartEnabled", 
					BooleanUtils.toStringTrueFalse(this.autoStartEnabled)),
				new ConfigPair("autoModeEnabled", 
					BooleanUtils.toStringTrueFalse(this.autoModeEnabled)),
				new ConfigPair("autoModeResetTrackMode", 
					this.autoModeResetTrackMode.name()),
		});
	}
	@Override
	public String toString() {
		return "AutoPrefs [autoModeEnabled=" + autoModeEnabled
			+ ", autoModeResetTrackMode=" + autoModeResetTrackMode
			+ ", autoStartEnabled=" + autoStartEnabled + "]";
	}
}
