package dev.kanka.checksumsharer;

import dev.kanka.checksumsharer.dao.Database;
import dev.kanka.checksumsharer.utils.Alerts;
import dev.kanka.checksumsharer.utils.FileUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class ChecksumSharerApplication extends Application {

    private static final Logger logger = LogManager.getLogger();
    private static Stage primaryStage;


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        if (Database.isOK()) {
            FXMLLoader fxmlLoader = new FXMLLoader(ChecksumSharerApplication.class.getResource("fxml/app.fxml"));
            fxmlLoader.setController(MainController.getInstance());
            Scene scene = new Scene(fxmlLoader.load(), 1200, 900);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            scene.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());
            stage.setTitle("Checksum Sharer");
            stage.setMaximized(true);
            stage.setScene(scene);
            stage.show();

            initDragAndDrop(scene);

        } else {
            Alerts.error(
                    "Database error",
                    "Could not load database",
                    "Error loading SQLite database. See log. Quitting..."
            ).showAndWait();
            Platform.exit();
        }
    }

    private void initDragAndDrop(Scene scene) {
        scene.setOnDragOver((event -> {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
        }));

        scene.setOnDragDropped((event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                handleDragAndDropFiles(dragboard);
            }
        }));
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private void handleDragAndDropFiles(Dragboard dragboard) {
        List<File> newFiles = dragboard.getFiles();
        logger.info("Dropped files: " + newFiles);
        FileUtil.handleNewFiles(newFiles);
    }
}