package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.dao.FileDAO;
import dev.kanka.checksumsharer.models.File;
import dev.kanka.checksumsharer.utils.FileUtil;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    TextField searchTextField;

    @FXML
    TableView<File> historyTableView;

    @FXML
    TableColumn<File, Integer> idColumn;

    @FXML
    TableColumn<File, Timestamp> dateColumn;

    @FXML
    TableColumn<File, String> fileNameColumn;

    @FXML
    TableColumn<File, String> fullPathColumn;

    @FXML
    TableColumn<File, Long> lastModifiedColumn;

    @FXML
    TableColumn<File, Number> fileSizeColumn;

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

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(40);
        idColumn.setMaxWidth(50);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        fullPathColumn.setCellValueFactory(new PropertyValueFactory<>("fullPath"));
        lastModifiedColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));

        fileSizeColumn.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        fileSizeColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (value == null || empty) {
                    setText("");
                } else {
                    setText(FileUtil.humanReadableByteCountBin((Long) value) + " (" + value + " Bytes)");
                }
            }
        });

        ObservableList<File> filesList = FileDAO.getFiles();
        FilteredList<File> filteredList = new FilteredList<>(filesList, b -> true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(file -> {

            // if filter is empty, display all files.
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            String searchString = newValue.toLowerCase();

            if (String.valueOf(file.getId()).contains(searchString)) {
                return true;
            }

            if (file.getFileName().contains(searchString)) {
                return true;
            }

            if (file.getFullPath().contains(searchString)) {
                return true;
            }

            if (String.valueOf(file.getFileSize()).contains(searchString)) {
                return true;
            }

            if (String.valueOf(file.getLastModified()).contains(searchString)) {
                return true;
            }

            return false;
        }));

        SortedList<File> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(historyTableView.comparatorProperty());

        historyTableView.setItems(sortedList);
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
