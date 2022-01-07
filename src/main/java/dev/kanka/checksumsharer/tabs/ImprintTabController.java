package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class ImprintTabController implements Initializable  {

    @FXML
    Text versionText;

    @FXML
    Button donateButton;

    public ImprintTabController() {
        // TODO check this in release
        Platform.runLater(() ->
                versionText.setText(ChecksumSharerApplication.class.getPackage().getImplementationVersion())
        );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
