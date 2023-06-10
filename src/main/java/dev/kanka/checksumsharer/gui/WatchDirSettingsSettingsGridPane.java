package dev.kanka.checksumsharer.gui;

import dev.kanka.checksumsharer.settings.PreferenceKeys;
import dev.kanka.checksumsharer.settings.Settings;

public class WatchDirSettingsSettingsGridPane extends KnkSettingsGridPane {

    public WatchDirSettingsSettingsGridPane() {
        super(".) Watch Directory", PreferenceKeys.WATCH_DIR_PATH, "watchDirDirectoryChooserButton", Settings.getInstance().watchDirPathsProperty(),
                "Recursive", Settings.getInstance().watchDirPathsRecursiveProperty());
        this.setId("watchDirSettingsGridPane");
    }
}
