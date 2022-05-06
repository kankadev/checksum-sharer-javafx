package dev.kanka.checksumsharer.gui;

import dev.kanka.checksumsharer.settings.PreferenceKeys;
import dev.kanka.checksumsharer.settings.Settings;

public class WatchDirSettingsGridPane extends KnkGridPane {
    
    public WatchDirSettingsGridPane() {
        super(".) Watch Directory", PreferenceKeys.WATCH_DIR_PATH, "watchDirDirectoryChooserButton", Settings.getInstance().watchDirPathsProperty());
        this.setId("watchDirSettingsGridPane");
    }
}
