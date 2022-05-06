package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.Constants;
import dev.kanka.checksumsharer.enums.ResourceBundles;
import dev.kanka.checksumsharer.gui.LocalStorageExportPathGridPane;
import dev.kanka.checksumsharer.gui.WatchDirSettingsGridPane;
import dev.kanka.checksumsharer.settings.PreferenceKeys;
import dev.kanka.checksumsharer.settings.Settings;
import dev.kanka.checksumsharer.watchdir.WatchDirService;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingsTabController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Preferences preferences = Preferences.userRoot().node(Settings.class.getName());
    private final Settings settings = Settings.getInstance();
    LocalStorageExportPathGridPane localStorageExportPathGridPane = new LocalStorageExportPathGridPane();
    WatchDirSettingsGridPane watchDirSettingsGridPane = new WatchDirSettingsGridPane();

    // Languages
    private final ResourceBundle lBundle = ResourceBundle.getBundle(ResourceBundles.GENERAL.getBundleName());

    @FXML
    VBox rootPane;
    @FXML
    Accordion settingsAccordion;
    @FXML
    TitledPane generalPane;
    @FXML
    TitledPane viewPane;
    @FXML
    ComboBox<String> dateFormatComboBox;
    @FXML
    TitledPane cloudPane;
    @FXML
    TitledPane localStoragePane;
    @FXML
    AnchorPane localStorageSettingsRootPane;
    @FXML
    TitledPane watchDirPane;
    @FXML
    AnchorPane watchDirSettingsRootPane;
    @FXML
    Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        layout();
        loadPreferences();
        registerListeners();
    }

    public void layout() {
        // Date Format
        Tooltip dateFormatTooltip = new Tooltip(lBundle.getString("date_format.desc"));
        dateFormatComboBox.setItems(settings.getAllDateFormats());
        dateFormatComboBox.getSelectionModel().select(0);
        dateFormatComboBox.setTooltip(dateFormatTooltip);

        // Local Storage GridPane
        AnchorPane.setBottomAnchor(localStorageExportPathGridPane, 0.0);
        AnchorPane.setTopAnchor(localStorageExportPathGridPane, 0.0);
        AnchorPane.setLeftAnchor(localStorageExportPathGridPane, 0.0);
        AnchorPane.setRightAnchor(localStorageExportPathGridPane, 0.0);
        localStorageSettingsRootPane.getChildren().add(localStorageExportPathGridPane);

        // Watch Dir GridPane
        AnchorPane.setBottomAnchor(watchDirSettingsGridPane, 0.0);
        AnchorPane.setTopAnchor(watchDirSettingsGridPane, 0.0);
        AnchorPane.setLeftAnchor(watchDirSettingsGridPane, 0.0);
        AnchorPane.setRightAnchor(watchDirSettingsGridPane, 0.0);
        watchDirSettingsRootPane.getChildren().add(watchDirSettingsGridPane);

        // Action Buttons
        saveButton.getStyleClass().addAll("btn", "btn-primary");
    }


    private void loadPreferences() {
        settings.languageProperty().set(preferences.get(PreferenceKeys.LANGUAGE, Settings.LANGUAGES[0]));
        settings.dateFormatProperty().set(preferences.get(PreferenceKeys.DATE_FORMAT, Settings.DATE_FORMATS[0]));
        dateFormatComboBox.valueProperty().bindBidirectional(settings.dateFormatProperty());

        // Local Storage
        for (int i = 1; i <= Constants.NUMBER_LOCAL_STORAGE_PATHS; i++) {
            String path = preferences.get(PreferenceKeys.LOCAL_STORAGE_EXPORT_PATH + i, null);
            localStorageExportPathGridPane.createRow(path, i);
            settings.getLocalStoragePaths().put(PreferenceKeys.LOCAL_STORAGE_EXPORT_PATH + i, path);
        }

        // Watch Dir
        for (int i = 1; i <= Constants.NUMBER_WATCH_DIR_PATHS; i++) {
            String path = preferences.get(PreferenceKeys.WATCH_DIR_PATH + i, null);
            watchDirSettingsGridPane.createRow(path, i);
            settings.getWatchDirPaths().put(PreferenceKeys.WATCH_DIR_PATH + i, path);
            try {
                if (path != null) {
                    WatchDirService.getInstance().register(Paths.get(path));
                }
            } catch (IOException e) {
                LOGGER.error(e);
                // TODO
            }
        }

        WatchDirService.getInstance().processEvents();
    }

    public void savePreferences() {
        LOGGER.debug("save preferences");

        preferences.put(PreferenceKeys.LANGUAGE, settings.getLanguage());
        preferences.put(PreferenceKeys.DATE_FORMAT, settings.getDateFormat());

        ObservableMap<String, String> localStoragePathsMap = settings.localStoragePathsProperty().get();
        localStoragePathsMap.forEach((s, s2) -> {
            if (s != null && s2 != null) {
                preferences.put(s, s2);
            }
        });

        ObservableMap<String, String> watchDirPathsMap = settings.watchDirPathsProperty().get();
        watchDirPathsMap.forEach((s, s2) -> {
            if (s != null && s2 != null) {
                preferences.put(s, s2);
            }
        });
    }

    private void registerListeners() {
        saveButton.setOnAction(event -> {
            savePreferences();
        });
    }
}
