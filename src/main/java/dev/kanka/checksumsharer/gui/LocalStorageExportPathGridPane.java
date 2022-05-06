package dev.kanka.checksumsharer.gui;

import dev.kanka.checksumsharer.settings.PreferenceKeys;
import dev.kanka.checksumsharer.settings.Settings;

public class LocalStorageExportPathGridPane extends KnkGridPane {

    public LocalStorageExportPathGridPane() {
        super(".) Export Directory", PreferenceKeys.LOCAL_STORAGE_EXPORT_PATH, "localStorageDirectoryChooserButton", Settings.getInstance().localStoragePathsProperty());
        this.setId("localStorageExportPathGridPane");
    }
}