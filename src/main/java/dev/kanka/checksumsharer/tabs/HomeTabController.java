package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.dao.FileDAO;
import dev.kanka.checksumsharer.models.File;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class HomeTabController implements Initializable {
    private static final Logger logger = LogManager.getLogger();

    @FXML
    TableView<File> historyTableView;

    @FXML
    TableColumn<File, Timestamp> dateColumn;

    @FXML
    TableColumn<File, String> fileNameColumn;

    @FXML
    TableColumn<File, String> fullPathColumn;

    @FXML
    TableColumn<File, Long> lastModifiedColumn;

    @FXML
    TableColumn<File, Long> fileSizeColumn;

    @FXML
    Button fileChooserButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("initialize()");

        initHistoryTableView();
        registerEventHandler();
    }

    private void initHistoryTableView() {
        historyTableView.setPlaceholder(new Label("No files for which the checksums have been calculated yet."));

        historyTableView.setItems(FileDAO.getFiles());

        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fullPathColumn.setCellValueFactory(new PropertyValueFactory<>("fullPath"));
        lastModifiedColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
    }

    private void registerEventHandler() {
        fileChooserButton.setOnAction((event) -> {
            openFileChooser();
        });

        fileChooserButton.setOnDragOver((event -> {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
        }));

        fileChooserButton.setOnDragDropped((event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasFiles()) {
                handleDragAndDropFiles(dragboard);
            }
        }));
    }

    private List<java.io.File> openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        List<java.io.File> files = fileChooser.showOpenMultipleDialog(ChecksumSharerApplication.getPrimaryStage());
        logger.info("Chosen file(s): " + files);
        insertFilesIntoDB(files);
        return files;
    }

    private List<java.io.File> handleDragAndDropFiles(Dragboard dragboard) {
        List<java.io.File> files = dragboard.getFiles();
        logger.info("Dropped files: " + files);
        insertFilesIntoDB(files);
        return files;
    }

    private void getMetaDataOfFile(java.io.File file) {

    }

    private void insertFilesIntoDB(List<java.io.File> files) {
        for (java.io.File file: files) {
            FileDAO.insertFile(file.getName(), file.getAbsolutePath(), file.lastModified(), file.length());
        }
    }
}
