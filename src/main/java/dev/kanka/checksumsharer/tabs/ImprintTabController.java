package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.utils.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Desktop;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class ImprintTabController implements Initializable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String KANKA_DEV_URL = "https://kanka.dev?ref=checksumsharer-javafx";

    @FXML
    Text versionText;

    @FXML
    Button donateButton;

    @FXML
    Hyperlink kankaDevLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO check this in release
        Platform.runLater(() -> {
            versionText.setText(ChecksumSharerApplication.class.getPackage().getImplementationVersion());
        });

        Platform.runLater(() -> {
            kankaDevLink.setTooltip(new Tooltip(KANKA_DEV_URL));

            kankaDevLink.setOnAction(actionEvent -> {
                try {
                    Desktop.getDesktop().browse(new URL(KANKA_DEV_URL).toURI());
                } catch (IOException | URISyntaxException e) {
                    LOGGER.error(e);
                    Alerts.error("Error", "Can't open browser", e.getLocalizedMessage()).show();
                }
            });
        });
    }
}
