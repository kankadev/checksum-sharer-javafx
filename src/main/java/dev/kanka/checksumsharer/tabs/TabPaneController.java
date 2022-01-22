package dev.kanka.checksumsharer.tabs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class TabPaneController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    @FXML
    TabPane tabPane;

    @FXML
    Tab detailsTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.detailsTab.setDisable(true);

        this.tabPane.getTabs().remove(detailsTab);
    }
}