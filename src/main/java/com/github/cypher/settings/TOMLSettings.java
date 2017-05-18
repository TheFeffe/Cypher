package com.github.cypher.settings;

import com.github.cypher.DebugLogger;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

//import static com.github.cypher.Main.USER_DATA_DIRECTORY;

public class TOMLSettings implements Settings {

	// Application specific constants
	private static final String FILE_NAME = "config.toml";

	// Instance variables
	private final File settingsFile;
	private final SettingsData settingsData;
	private final String userDataDirectory;

	// Class representing all settings
	private static class SettingsData{
		// All variables are initiated to default values
		String languageTag = Locale.getDefault().toLanguageTag();
		boolean saveSession = false;
		boolean useSystemTray = true;
		boolean controlEnterToSendMessage = true;
		int SDKTimeout = 30000; // In ms
		int modelTickInterval = 500; // In ms
	}

	public TOMLSettings(String userDataDirectory) {
		this.userDataDirectory = userDataDirectory;
		settingsFile = createOrLoadFile();
		settingsData = load(settingsFile);
		save();
	}

	private File createOrLoadFile() {
		try {
			// Create folder if it doesn't exist
			new File(userDataDirectory).mkdir();

			// Load File
			File file = new File(userDataDirectory + File.separator + FILE_NAME);

			// Create file if it doesn't exist
			file.createNewFile();

			return file;

		} catch (IOException e) {
			DebugLogger.log("Could not create settings file");
			return null;
		}
	}

	private static synchronized SettingsData load(File settingsFile) {
		// Make sure settingsFile is set before loading settings
		if (settingsFile != null) {
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Reading settings from: " + settingsFile);
			}
			return new Toml().read(settingsFile).to(SettingsData.class);
		} else {
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Could not access settings file, defaults will be loaded.");
			}
			return new SettingsData();
		}
	}

	private synchronized void save() {
		// Make sure settingsFile is set before saving settings
		if (settingsFile != null) {
			try {
				new TomlWriter().write(settingsData, settingsFile);
				if (DebugLogger.ENABLED) {
					DebugLogger.log("Settings saved to: " + settingsFile);
				}
			} catch (IOException e) {
				if (DebugLogger.ENABLED) {
					DebugLogger.log("Could not access settings file, settings won't be saved.");
				}
			}
		} else {
			if (DebugLogger.ENABLED) {
				DebugLogger.log("Could not access settings file, settings won't be saved.");
			}
		}
	}

	// Language setting
	@Override
	public synchronized Locale getLanguage() {
		return Locale.forLanguageTag(settingsData.languageTag);
	}

	@Override
	public synchronized void setLanguage(Locale language) {
		settingsData.languageTag = language.toLanguageTag();
		save();
	}

	// Save session ("keep me logged in") settings
	@Override
	public synchronized boolean getSaveSession() {
		return settingsData.saveSession;
	}

	@Override
	public synchronized void setSaveSession(boolean saveSession) {
		settingsData.saveSession = saveSession;
		save();
	}

	@Override
	public synchronized boolean setUseSystemTray() {
		return settingsData.useSystemTray;
	}

	@Override
	public synchronized void setUseSystemTray(boolean useSystemTray) {
		settingsData.useSystemTray = useSystemTray;
		save();
	}

	// If control + enter should be used for sending messages (if false only enter is needed)
	@Override
	public synchronized boolean getControlEnterToSendMessage() {
		return settingsData.controlEnterToSendMessage;
	}

	@Override
	public synchronized void setControlEnterToSendMessage(boolean controlEnterToSendMessage) {
		settingsData.controlEnterToSendMessage = controlEnterToSendMessage;
		save();
	}

	// Timeout is maximum time to poll in milliseconds before returning a request
	@Override
	public synchronized int getSDKTimeout() {
		return settingsData.SDKTimeout;
	}

	@Override
	public synchronized void setSDKTimeout(int timeout) {
		settingsData.SDKTimeout = timeout;
		save();
	}

	// The time between each tick in the model in ms
	@Override
	public synchronized int getModelTickInterval() {
		return settingsData.modelTickInterval;
	}

	@Override
	public synchronized void setModelTickInterval(int interval) {
		settingsData.modelTickInterval = interval;
		save();
	}
}