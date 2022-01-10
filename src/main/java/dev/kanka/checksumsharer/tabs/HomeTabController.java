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
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

import static dev.kanka.checksumsharer.dao.FileDAO.insertFilesIntoDB;

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
    TableColumn<File, Integer> idColumn;

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
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    }

    private void registerEventHandler() {
        fileChooserButton.setOnAction((event) -> {
            openFileChooser();
        });
    }

    private List<java.io.File> openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        List<java.io.File> files = fileChooser.showOpenMultipleDialog(ChecksumSharerApplication.getPrimaryStage());
        logger.info("Chosen file(s): " + files);
        insertFilesIntoDB(files);
        return files;
    }
}
