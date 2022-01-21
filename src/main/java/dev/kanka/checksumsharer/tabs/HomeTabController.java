package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.dao.FileDAO;
import dev.kanka.checksumsharer.models.KnkFile;
import dev.kanka.checksumsharer.utils.FileUtil;
import javafx.application.Platform;
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

import java.io.File;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class HomeTabController implements Initializable {

    private static final Logger logger = LogManager.getLogger();

    @FXML
    TextField searchTextField;

    @FXML
    TableView<KnkFile> historyTableView;

    @FXML
    TableColumn<KnkFile, Integer> idColumn;

    @FXML
    TableColumn<KnkFile, Timestamp> dateColumn;

    @FXML
    TableColumn<KnkFile, String> fileNameColumn;

    @FXML
    TableColumn<KnkFile, String> fullPathColumn;

    @FXML
    TableColumn<KnkFile, Long> lastModifiedColumn;

    @FXML
    TableColumn<KnkFile, Number> fileSizeColumn;

    @FXML
    Button fileChooserButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("initialize()");

        initHistoryTableView();
        registerEventHandler();

        // unfocus
        Platform.runLater(() -> historyTableView.getParent().requestFocus());
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
        lastModifiedColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Long value, boolean empty) {
                super.updateItem(value, empty);

                if (value == null || empty) {
                    setText("");
                } else {
                    Date date = new Date(value);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                    setText(dateFormat.format(date));
                    // setGraphic(); // TODO check difference between this and setText()
                }
            }
        });

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

        ObservableList<KnkFile> filesList = FileDAO.getFiles();
        FilteredList<KnkFile> filteredList = new FilteredList<>(filesList, b -> true);

        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(knkFile -> {

            // if filter is empty, display all files.
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            String searchString = newValue.toLowerCase();

            if (String.valueOf(knkFile.getId()).contains(searchString)) {
                return true;
            }

            if (knkFile.getFileName().contains(searchString)) {
                return true;
            }

            if (knkFile.getFullPath().contains(searchString)) {
                return true;
            }

            if (String.valueOf(knkFile.getFileSize()).contains(searchString)) {
                return true;
            }

            if (String.valueOf(knkFile.getLastModified()).contains(searchString)) {
                return true;
            }

            return false;
        }));

        SortedList<KnkFile> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(historyTableView.comparatorProperty());

        historyTableView.setItems(sortedList);

        idColumn.setSortType(TableColumn.SortType.DESCENDING);
        historyTableView.getSortOrder().addAll(idColumn);
    }

    private void registerEventHandler() {
        fileChooserButton.setOnAction((event) -> {
            List<File> files = openFileChooser();
            FileUtil.handleNewFiles(files);
        });
    }

    private List<File> openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(ChecksumSharerApplication.getPrimaryStage());
        logger.info("Chosen file(s): " + files);
        return files;
    }
}
