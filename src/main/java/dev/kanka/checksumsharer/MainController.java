package dev.kanka.checksumsharer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    private static MainController instance;

    @FXML
    ProgressIndicator progressIndicator;

    @FXML
    Text progressIndicatorText;

    private MainController() {

    }

    public static MainController getInstance() {
        if (MainController.instance == null) {
            MainController.instance = new MainController();
        }
        return MainController.instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setProgressIndicatorVisible(false);
    }

    public void setProgressIndicatorVisible(boolean b) {
        this.progressIndicator.setVisible(b);
        this.progressIndicatorText.setVisible(b);
    }

}