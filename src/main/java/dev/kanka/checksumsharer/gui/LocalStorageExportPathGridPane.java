package dev.kanka.checksumsharer.gui;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.settings.PreferenceKeys;
import dev.kanka.checksumsharer.settings.Settings;
import javafx.scene.control.Button;
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

public class LocalStorageExportPathGridPane extends GridPane {

    private static final Logger LOGGER = LogManager.getLogger();

    public LocalStorageExportPathGridPane() {
        super();
        this.setVgap(20.0);
        this.setHgap(20.0);
    }

    public void createRow(String path, int row) {
        Label label = new Label(row + ".) Export Directory");

        HBox hBox = new HBox();

        TextField textField = new TextField(path);
        textField.setId(PreferenceKeys.LOCAL_STORAGE_EXPORT_PATH + row);
        textField.setPrefWidth(350);

        Button directoryChooserButton = new Button();
        directoryChooserButton.setId("localStorageDirectoryChooserButton" + row);
        directoryChooserButton.getStyleClass().addAll("btn", "btn-primary");
        FontIcon fontIcon = new FontIcon("far-folder-open");
        fontIcon.setIconColor(Paint.valueOf("white"));
        fontIcon.setIconSize(16);
        directoryChooserButton.setGraphic(fontIcon);

        Settings.getInstance().localStoragePathsProperty().put(textField.getId(), textField.getText());

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Settings.getInstance().localStoragePathsProperty().put(textField.getId(), textField.getText());
        });

        directoryChooserButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(ChecksumSharerApplication.getPrimaryStage());

            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                LOGGER.debug("Selected export directory: " + selectedDirectory.getAbsolutePath());
                textField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        hBox.getChildren().addAll(textField, directoryChooserButton);
        add(label, 0, row - 1);
        add(hBox, 1, row - 1);
    }
}