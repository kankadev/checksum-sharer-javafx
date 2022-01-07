package dev.kanka.checksumsharer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    @FXML
    TabPane tabPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}