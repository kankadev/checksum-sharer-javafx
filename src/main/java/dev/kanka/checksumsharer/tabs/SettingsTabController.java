package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.enums.ResourceBundles;
import dev.kanka.checksumsharer.settings.Settings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingsTabController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int SPACING = 20;

    private final Preferences preferences = Preferences.userRoot().node(this.getClass().getName());
    private final Settings settings = new Settings();

    // Languages
    private final ResourceBundle lBundle = ResourceBundle.getBundle(ResourceBundles.SETTINGS.getBundleName());

    @FXML
    VBox vBox;

    ComboBox<String> dateFormatComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        layout();
        loadPreference();
        registerListeners();
    }


    public void layout() {
        List<HBox> allBoxes = new ArrayList<>();

        // Date Format
        Tooltip dateFormatTooltip = new Tooltip(lBundle.getString("date_format.label"));
        final Label dateFormatLabel = new Label(lBundle.getString("date_format.label"));
        dateFormatLabel.setTooltip(dateFormatTooltip);
        dateFormatComboBox = new ComboBox(settings.getAllDateFormats());
        dateFormatComboBox.getSelectionModel().select(0);
        dateFormatComboBox.setTooltip(dateFormatTooltip);
        dateFormatLabel.setLabelFor(dateFormatComboBox);
        final HBox dateFormatBox = new HBox();
        dateFormatBox.getChildren().addAll(dateFormatLabel, dateFormatComboBox);
        allBoxes.add(dateFormatBox);

        // Action Buttons
        final HBox actionButtonsBox = new HBox();
        Button saveButton = new Button(lBundle.getString("save_button.text"));
        saveButton.setOnAction(event -> {
            savePreference();
        });
        saveButton.getStyleClass().addAll("btn", "btn-primary");
        actionButtonsBox.getChildren().add(saveButton);
        allBoxes.add(actionButtonsBox);


        for (HBox box : allBoxes) {
            box.setSpacing(SPACING);
            box.setAlignment(Pos.CENTER_LEFT);
            box.getStyleClass().add("settings-box");
        }

        vBox.getChildren().addAll(dateFormatBox, actionButtonsBox);
        vBox.setSpacing(SPACING);
    }


    private void loadPreference() {
        settings.languageProperty().set(preferences.get(String.valueOf(settings.languageProperty().hashCode()), Settings.LANGUAGES[0]));
        settings.dateFormatProperty().set(preferences.get(String.valueOf(settings.allDateFormatsProperty().hashCode()), Settings.DATE_FORMATS[0]));

        dateFormatComboBox.valueProperty().bindBidirectional(settings.dateFormatProperty());
    }

    public void savePreference() {
        LOGGER.debug("save preferences");

        preferences.put(String.valueOf(settings.languageProperty().hashCode()), settings.getLanguage());
        preferences.put(String.valueOf(settings.allDateFormatsProperty().hashCode()), settings.getDateFormat());

        handleSettings();
    }

    private void registerListeners() {
    }

    private void handleSettings() {
    }

}
