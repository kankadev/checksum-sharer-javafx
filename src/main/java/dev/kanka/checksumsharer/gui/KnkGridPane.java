package dev.kanka.checksumsharer.gui;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.Constants;
import javafx.beans.property.SimpleMapProperty;
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

public abstract class KnkGridPane extends GridPane {

    static final Logger LOGGER = LogManager.getLogger();

    String labelText;
    String textFieldIdPrefix;
    String btnId;
    SimpleMapProperty<String, String> mapProperty;

    KnkGridPane(String labelText, String textFieldIdPrefix, String btnId, SimpleMapProperty<String, String> mapProperty) {
        super();
        this.setVgap(Constants.GAP);
        this.setHgap(Constants.GAP);
        this.labelText = labelText;
        this.textFieldIdPrefix = textFieldIdPrefix;
        this.btnId = btnId;
        this.mapProperty = mapProperty;
    }

    public void createRow(String predefinedValue, int row) {
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

//        Settings.getInstance().localStoragePathsProperty().put(textField.getId(), textField.getText());
        mapProperty.put(textField.getId(), textField.getText());

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
//            Settings.getInstance().localStoragePathsProperty().put(textField.getId(), textField.getText());
            mapProperty.put(textField.getId(), textField.getText());
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
    }
}
