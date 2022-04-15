package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.enums.ResourceBundles;
import dev.kanka.checksumsharer.settings.PreferenceKeys;
import dev.kanka.checksumsharer.settings.Settings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingsTabController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int SPACING = 20;

    private final Preferences preferences = Preferences.userRoot().node(Settings.class.getName());
    private final Settings settings = new Settings();

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
    TextField localStorageExportPath;
    @FXML
    Button localStorageDirectoryChooserButton;
    @FXML
    Button saveButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        layout();
        loadPreference();
        registerListeners();
        LOGGER.debug(preferences);
    }

    public void layout() {
        // Date Format
        Tooltip dateFormatTooltip = new Tooltip(lBundle.getString("date_format.desc"));
        dateFormatComboBox.setItems(settings.getAllDateFormats());
        dateFormatComboBox.getSelectionModel().select(0);
        dateFormatComboBox.setTooltip(dateFormatTooltip);

        // Action Buttons

        saveButton.getStyleClass().addAll("btn", "btn-primary");
    }


    private void loadPreference() {
        settings.languageProperty().set(preferences.get(PreferenceKeys.LANGUAGE, Settings.LANGUAGES[0]));
        settings.dateFormatProperty().set(preferences.get(PreferenceKeys.DATE_FORMAT, Settings.DATE_FORMATS[0]));
        settings.localStorageExportPathProperty().set(preferences.get(PreferenceKeys.LOCAL_STORAGE_EXPORT_PATH, null));
        localStorageExportPath.setText(settings.getLocalStorageExportPath()); // TODO: use Bindings

        dateFormatComboBox.valueProperty().bindBidirectional(settings.dateFormatProperty());
    }

    public void savePreference() {
        LOGGER.debug("save preferences");

        preferences.put(PreferenceKeys.LANGUAGE, settings.getLanguage());
        preferences.put(PreferenceKeys.DATE_FORMAT, settings.getDateFormat());
        preferences.put(PreferenceKeys.LOCAL_STORAGE_EXPORT_PATH, settings.getLocalStorageExportPath());

        handleSettings();
    }

    private void registerListeners() {
        saveButton.setOnAction(event -> {
            savePreference();
        });
        localStorageDirectoryChooserButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(ChecksumSharerApplication.getPrimaryStage());

            if (selectedDirectory.isDirectory()) {
                LOGGER.debug("Selected export directory: " + selectedDirectory.getAbsolutePath());
                settings.setLocalStorageExportPath(selectedDirectory.getAbsolutePath());
                localStorageExportPath.setText(selectedDirectory.getAbsolutePath());
            }
        });
    }

    private void handleSettings() {
    }

}
