package dev.kanka.checksumsharer.gui;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.Constants;
import dev.kanka.checksumsharer.settings.PreferenceKeys;
import javafx.beans.property.SimpleMapProperty;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

public abstract class KnkSettingsGridPane extends GridPane {

    static final Logger LOGGER = LogManager.getLogger();

    String labelText;
    String textFieldIdPrefix;
    String btnId;
    SimpleMapProperty<String, String> pathMapProperty;
    String checkBoxLabel;
    SimpleMapProperty<String, Boolean> checkBoxMapProperty;

    KnkSettingsGridPane(String labelText, String textFieldIdPrefix, String btnId, SimpleMapProperty<String, String> pathMapProperty, String checkBoxLabel, SimpleMapProperty<String, Boolean> checkBoxMapProperty) {
        super();
        this.setVgap(Constants.GAP);
        this.setHgap(Constants.GAP);
        this.labelText = labelText;
        this.textFieldIdPrefix = textFieldIdPrefix;
        this.btnId = btnId;
        this.pathMapProperty = pathMapProperty;
        this.checkBoxLabel = checkBoxLabel;
        this.checkBoxMapProperty = checkBoxMapProperty;
    }

    public void createRow(String predefinedValue, int row, boolean isCheckBoxChecked) {
        Label label = new Label(row + labelText);

        TextField textField = new TextField(predefinedValue);
        textField.setId(textFieldIdPrefix + row);
        textField.setPrefWidth(350);

        Button directoryChooserButton = new Button();
        directoryChooserButton.setId(this.btnId + row);
        directoryChooserButton.getStyleClass().addAll("btn", "btn-primary");
        FontIcon fontIcon = new FontIcon("far-folder-open");
        fontIcon.setIconColor(Paint.valueOf("white"));
        fontIcon.setIconSize(16);
        directoryChooserButton.setGraphic(fontIcon);

        pathMapProperty.put(textField.getId(), textField.getText());

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            pathMapProperty.put(textField.getId(), textField.getText());
        });

        directoryChooserButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(ChecksumSharerApplication.getPrimaryStage());

            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                LOGGER.debug("Selected directory: " + selectedDirectory.getAbsolutePath());
                textField.setText(selectedDirectory.getAbsolutePath());
            }
        });


        HBox hBox = new HBox();
        hBox.getChildren().addAll(textField, directoryChooserButton);
        add(label, 0, row - 1);
        add(hBox, 1, row - 1);

        if (this.checkBoxLabel != null) {
            CheckBox checkBox = new CheckBox(checkBoxLabel);
            checkBox.setId(PreferenceKeys.WATCH_DIR_RECURSIVE + row);
            checkBox.setSelected(isCheckBoxChecked);

            checkBoxMapProperty.put(checkBox.getId(), checkBox.isSelected());

            checkBox.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                checkBoxMapProperty.put(checkBox.getId(), t1);
            });

            add(checkBox, 2, row - 1);
        }
    }
}
