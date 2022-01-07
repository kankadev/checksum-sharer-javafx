package dev.kanka.checksumsharer.tabs;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class HomeTabController implements Initializable {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    TableView historyTableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("debug initialize()"); // TODO
        logger.info("info initialize()");

        historyTableView.setPlaceholder(new Label("No files for which the checksums have been calculated yet."));
    }
}
