package dev.kanka.checksumsharer;

import dev.kanka.checksumsharer.dao.Database;
import dev.kanka.checksumsharer.utils.Alerts;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;


public class ChecksumSharerApplication extends Application {

    private static final Logger logger = LogManager.getLogger();
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        if (Database.isOK()) {
            FXMLLoader fxmlLoader = new FXMLLoader(ChecksumSharerApplication.class.getResource("app.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 900);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(getClass().getResource("/dev/kanka/checksumsharer/css/main.css").toExternalForm());
            stage.setTitle("Checksum Sharer");
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();
            primaryStage = stage;
        } else {
            Alerts.error(
                    "Database error",
                    "Could not load database",
                    "Error loading SQLite database. See log. Quitting..."
            ).showAndWait();
            Platform.exit();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}