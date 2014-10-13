package de.msk.mylivetracker.client.android.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import de.msk.mylivetracker.client.android.App;
import de.msk.mylivetracker.client.android.App.VersionDsc;
import de.msk.mylivetracker.client.android.account.AccountPrefs;
import de.msk.mylivetracker.client.android.auto.AutoPrefs;
import de.msk.mylivetracker.client.android.dropbox.DropboxPrefs;
import de.msk.mylivetracker.client.android.emergency.EmergencyPrefs;
import de.msk.mylivetracker.client.android.httpprotocolparams.HttpProtocolParamsPrefs;
import de.msk.mylivetracker.client.android.localization.LocalizationPrefs;
import de.msk.mylivetracker.client.android.message.MessagePrefs;
import de.msk.mylivetracker.client.android.other.OtherPrefs;
import de.msk.mylivetracker.client.android.pincodequery.PinCodeQueryPrefs;
import de.msk.mylivetracker.client.android.preferences.prefsv144.PrefsV144Updater;
import de.msk.mylivetracker.client.android.preferences.prefsv150.PrefsV150Updater;
import de.msk.mylivetracker.client.android.preferences.prefsv160.PrefsV160Updater;
import de.msk.mylivetracker.client.android.protocol.ProtocolPrefs;
import de.msk.mylivetracker.client.android.remoteaccess.RemoteAccessPrefs;
import de.msk.mylivetracker.client.android.server.ServerPrefs;
import de.msk.mylivetracker.client.android.status.TrackStatus;
import de.msk.mylivetracker.client.android.trackexport.TrackExportPrefs;
import de.msk.mylivetracker.client.android.trackingmode.TrackingModePrefs;
import de.msk.mylivetracker.client.android.util.FileUtils;
import de.msk.mylivetracker.client.android.util.FileUtils.PathType;
import de.msk.mylivetracker.client.android.util.LogUtils;

/**
 * classname: PrefsRegistry
 * 
 * @author michael skerwiderski, (c)2012
 * @version 000
 * @since 1.5.0
 * 
 * history:
 * 000	2012-12-29	revised for v1.5.x.
 * 
 */
@SuppressWarnings("deprecation")
public class PrefsRegistry {
	private static final String PREFS_VERSION_VAR_PREFIX = "version-";
	
	private static Map<Class<? extends APrefs>, APrefs> prefsReg = 
		new HashMap<Class<? extends APrefs>, APrefs>();
	
	public static Map<String, APrefs> prefsMap = 
		new HashMap<String, APrefs>();
	
	protected static class PrefsDsc {
		protected int id;
		protected Class<? extends APrefs> prefsClass;
		protected int version;
		public PrefsDsc(int id, Class<? extends APrefs> prefsClass, int version) {
			this.id = id;
			this.prefsClass = prefsClass;
			this.version = version;
		}
	}
	
	protected static PrefsDsc[] prefsDscArr = new PrefsDsc[] {
		new PrefsDsc(0, MainPrefs.class, MainPrefs.VERSION),
		new PrefsDsc(-1, AutoPrefs.class, AutoPrefs.VERSION),
		new PrefsDsc(2, AccountPrefs.class, AccountPrefs.VERSION),	
		new PrefsDsc(3, TrackingModePrefs.class, TrackingModePrefs.VERSION),
		new PrefsDsc(4, DropboxPrefs.class, DropboxPrefs.VERSION),
		new PrefsDsc(5, EmergencyPrefs.class, EmergencyPrefs.VERSION),
		new PrefsDsc(6, HttpProtocolParamsPrefs.class, HttpProtocolParamsPrefs.VERSION),
		new PrefsDsc(7, LocalizationPrefs.class, LocalizationPrefs.VERSION),
		new PrefsDsc(8, MessagePrefs.class, MessagePrefs.VERSION),
		new PrefsDsc(9, OtherPrefs.class, OtherPrefs.VERSION),
		new PrefsDsc(10, PinCodeQueryPrefs.class, PinCodeQueryPrefs.VERSION),
		new PrefsDsc(11, ProtocolPrefs.class, ProtocolPrefs.VERSION),
		new PrefsDsc(12, RemoteAccessPrefs.class, RemoteAccessPrefs.VERSION),
		new PrefsDsc(13, ServerPrefs.class, ServerPrefs.VERSION),
		new PrefsDsc(14, TrackExportPrefs.class, TrackExportPrefs.VERSION),
	};

	public enum InitResult {
		PrefsImportedFromV144,
		PrefsUpdatedFromV150,
		PrefsUpdatedFromV160,
		PrefsCreated, 
		PrefsUpdated, 
		PrefsLoaded, 
	}
	
	private static InitResult initResult = init();

	public static final InitResult getInitResult() {
		return initResult;
	}
	
	private static InitResult init() {
		LogUtils.infoMethodIn(PrefsRegistry.class, "init");
		prefsReg.clear();
		prefsMap.clear();
		InitResult initResult = null;
		
		if (!FileUtils.fileExists(App.getPrefsFileName(true), PathType.AppSharedPrefsDir)) {
			reset();
			initResult = InitResult.PrefsCreated;
			LogUtils.infoMethodState(PrefsRegistry.class, "init", "initResult", initResult);
			if (PrefsV144Updater.run()) {
				saveAllInternal();
				initResult = InitResult.PrefsImportedFromV144;
				LogUtils.infoMethodState(PrefsRegistry.class, "init", "initResult", initResult);
			}
		} else {
			SharedPreferences sharedPrefs = App.getCtx().
				getSharedPreferences(App.getPrefsFileName(false), 0);

			int mainPrefsVersion = sharedPrefs.getInt(getVersionVar(MainPrefs.class), -1);
			
			if (mainPrefsVersion == -1) {
				initResult = InitResult.PrefsUpdatedFromV150;
			} else if (mainPrefsVersion == 160) {
				initResult = InitResult.PrefsUpdatedFromV160;
			}
			
			for (PrefsDsc prefsDsc : prefsDscArr) {
				APrefs prefs = null;
				boolean doSave = false;
				int foundVersion = sharedPrefs.getInt(getVersionVar(prefsDsc.prefsClass), -1);
				if (foundVersion == -1) {
					prefs = create(prefsDsc.prefsClass);
					doSave = true;
				} else {
					String prefsStr = 
						sharedPrefs.getString(getPrefsVar(prefsDsc.prefsClass), null);
					if (foundVersion < prefsDsc.version) {
						prefs = create(prefsDsc.prefsClass);
						try {
							prefs.initWithValuesOfOldVersion(foundVersion, prefsStr);
						} catch (Exception e) {
							prefs = create(prefsDsc.prefsClass);
						}
						doSave = true;
					} else {
						prefs = deserialize(prefsDsc.prefsClass, prefsStr);
					}
					if (!prefs.checkIfValid()) {
						prefs.initWithDefaults();
					}
				}
				if (prefs == null) {
					throw new RuntimeException("prefs must not be null!");
				}
				prefsReg.put(prefsDsc.prefsClass, prefs);
				prefsMap.put(prefs.getShortName(), prefs);
				if (doSave) {
					save(prefsDsc.prefsClass);
					if (initResult == null) {
						initResult = InitResult.PrefsUpdated;
					}
				}
			}
			
			if (initResult == null) {
				initResult = InitResult.PrefsLoaded;
			} else if (initResult.equals(InitResult.PrefsUpdatedFromV150)) {
				PrefsV150Updater.run();
			} else if (initResult.equals(InitResult.PrefsUpdatedFromV160)) {
				PrefsV150Updater.run();
				PrefsV160Updater.run();
			}
			
			if (mainPrefsVersion < VersionDsc.getCode()) {
				TrackStatus.reset();
			}
		}
		
		LogUtils.infoMethodOut(PrefsRegistry.class, "init", initResult);
		return initResult;
	}
	
	private static void reset() {
		LogUtils.infoMethodIn(PrefsRegistry.class, "reset");
		prefsReg.clear();
		prefsMap.clear();
		FileUtils.fileDelete(
			App.getPrefsFileName(true),
			PathType.AppSharedPrefsDir);
		for (PrefsDsc prefsDsc : prefsDscArr) {
			APrefs prefs = create(prefsDsc.prefsClass);
			prefsReg.put(prefsDsc.prefsClass, prefs);
			prefsMap.put(prefs.getShortName(), prefs);
		}
		saveAllInternal();
		LogUtils.infoMethodOut(PrefsRegistry.class, "reset");
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends APrefs> T get(Class<T> prefsClass) {
		T prefs = null;
		if (prefsClass == null) {
			throw new IllegalArgumentException("prefsClass must not be null!");
		}
		if (!prefsReg.containsKey(prefsClass)) {
			throw new RuntimeException("prefs of " + prefsClass + " not found!");
		} 
		prefs = (T)prefsReg.get(prefsClass);
		return prefs;
	}

	@SuppressWarnings("unchecked")
	public static <T extends APrefs> T get(String prefsShortName) {
		if (StringUtils.isEmpty(prefsShortName)) {
			throw new IllegalArgumentException("prefsShortName must not be empty!");
		}
		return (T)prefsMap.get(prefsShortName);
	}
	
	public static boolean contains(String prefsShortName) {
		if (StringUtils.isEmpty(prefsShortName)) {
			throw new IllegalArgumentException("prefsShortName must not be empty!");
		}
		return prefsMap.containsKey(prefsShortName);
	}
	
	public static String getSupportedPrefsAsPrettyStr() {
		String res = "";
		for (Iterator<String> it=prefsMap.keySet().iterator(); it.hasNext(); ) {
			res += it.next();
			if (it.hasNext()) {
				res += ", ";
			}
		}
		return res;
	}
	
	public static <T extends APrefs> void save(Class<T> prefsClass) {
		if (prefsClass == null) {
			throw new IllegalArgumentException("prefsClass must not be null!");
		}
		LogUtils.infoMethodIn(PrefsRegistry.class, "save", prefsClass);
		SharedPreferences sharedPrefs = App.getCtx().
			getSharedPreferences(App.getPrefsFileName(false), Context.MODE_PRIVATE);
		SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
		saveInternal(sharedPrefsEditor, prefsClass);	
		sharedPrefsEditor.commit();
		LogUtils.infoMethodOut(PrefsRegistry.class, "save");
	}
	
	private static <T extends APrefs> T create(Class<T> prefsClass) {
		T prefs = null;
		try {
			prefs = prefsClass.newInstance();
			prefs.initWithDefaults();
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		return prefs;
	}
	
	private static String serialize(APrefs prefs) {
		if (prefs == null) {
			throw new IllegalArgumentException("prefs must not be null!");
		}
		Gson gson = new Gson();
		return gson.toJson(prefs);
	}

	private static <T extends APrefs> T deserialize(Class<T> prefsClass, String jsonStr) {
		if (prefsClass == null) {
			throw new IllegalArgumentException("prefsClass must not be null!");
		}
		if (StringUtils.isEmpty(jsonStr)) {
			throw new IllegalArgumentException("jsonStr must not be empty!");
		}
		Gson gson = new Gson();
		return gson.fromJson(jsonStr, prefsClass);
	}
	
	private static <T extends APrefs> String getVersionVar(Class<T> prefsClass) {
		if (prefsClass == null) {
			throw new IllegalArgumentException("prefsClass must not be null!");
		}
		return prefsClass.getName() + "_" + PREFS_VERSION_VAR_PREFIX;
	}
	
	private static <T extends APrefs> String getPrefsVar(Class<T> prefsClass) {
		if (prefsClass == null) {
			throw new IllegalArgumentException("prefsClass must not be null!");
		}
		return prefsClass.getName();
	}
	
	private static <T extends APrefs> void saveInternal(SharedPreferences.Editor sharedPrefsEditor, Class<T> prefsClass) {
		if (sharedPrefsEditor == null) {
			throw new IllegalArgumentException("sharedPrefsEditor must not be null!");
		}
		if (prefsClass == null) {
			throw new IllegalArgumentException("prefsClass must not be null!");
		}
		LogUtils.infoMethodIn(PrefsRegistry.class, "saveInternal", prefsClass);
		T prefs = get(prefsClass);
		prefs.onSave();
		sharedPrefsEditor.putInt(getVersionVar(prefsClass), prefs.getVersion());
		sharedPrefsEditor.putString(getPrefsVar(prefsClass), serialize(prefs));		
		LogUtils.infoMethodOut(PrefsRegistry.class, "saveInternal");
	}
	
	private static <T extends APrefs> void saveAllInternal() {
		LogUtils.infoMethodIn(PrefsRegistry.class, "saveAllInternal");
		SharedPreferences sharedPrefs = App.getCtx().
			getSharedPreferences(App.getPrefsFileName(false), Context.MODE_PRIVATE);
		SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
		for (PrefsDsc prefsDsc : prefsDscArr) {
			saveInternal(sharedPrefsEditor, prefsDsc.prefsClass);
		}
		sharedPrefsEditor.commit();
		LogUtils.infoMethodOut(PrefsRegistry.class, "saveAllInternal");
	}
}
