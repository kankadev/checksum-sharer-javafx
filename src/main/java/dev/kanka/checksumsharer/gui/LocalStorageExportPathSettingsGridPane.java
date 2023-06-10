package dev.kanka.checksumsharer.gui;

import dev.kanka.checksumsharer.settings.PreferenceKeys;
import dev.kanka.checksumsharer.settings.Settings;

public class LocalStorageExportPathSettingsGridPane extends KnkSettingsGridPane {

    public LocalStorageExportPathSettingsGridPane() {
        super(".) Export Directory", PreferenceKeys.LOCAL_STORAGE_EXPORT_PATH, "localStorageDirectoryChooserButton", Settings.getInstance().localStoragePathsProperty(), null, null);
        this.setId("localStorageExportPathGridPane");
    }
}