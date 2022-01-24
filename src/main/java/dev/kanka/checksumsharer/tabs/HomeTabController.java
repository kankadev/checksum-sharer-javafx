package dev.kanka.checksumsharer.tabs;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import dev.kanka.checksumsharer.dao.FileDAO;
import dev.kanka.checksumsharer.models.KnkFile;
import dev.kanka.checksumsharer.utils.FileUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.MasterDetailPane;
import org.kordamp.ikonli.javafx.FontIcon;

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
    MasterDetailPane masterDetailPane;

    @FXML
    AnchorPane rootPane;

    @FXML
    TextField searchTextField;

    @FXML
    Button fileChooserButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("initialize()");

        initHistoryTableView();
        registerEventHandler();
    }

    private void initHistoryTableView() {
        TableView<KnkFile> historyTableView = new TableView<>();

        // unfocus
        Platform.runLater(() -> historyTableView.getParent().requestFocus());

        // ID Column
        TableColumn<KnkFile, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMinWidth(40);
        idColumn.setMaxWidth(50);

        // Date Column
        TableColumn<KnkFile, Timestamp> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Filename Column
        TableColumn<KnkFile, String> fileNameColumn = new TableColumn<>("Filename");
        fileNameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));


        // Full Path Column
        TableColumn<KnkFile, String> fullPathColumn = new TableColumn<>("Full Path");
        fullPathColumn.setCellValueFactory(new PropertyValueFactory<>("fullPath"));


        // Last Modified Date Column
        TableColumn<KnkFile, Long> lastModifiedColumn = new TableColumn<>("Last Modified Date");
        lastModifiedColumn.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        lastModifiedColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Long value, boolean empty) {
                super.updateItem(value, empty);

                if (value == null || empty) {
                    setText("");
                } else {
                    Date date = new Date(value);
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    setText(dateFormat.format(date));
                    // setGraphic(); // TODO check difference between this and setText()
                }
            }
        });


        // File Size Column
        TableColumn<KnkFile, Number> fileSizeColumn = new TableColumn<>("File Size");

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


        historyTableView.getColumns().addAll(idColumn, dateColumn, fileNameColumn, fullPathColumn, fileSizeColumn, lastModifiedColumn);

        historyTableView.setPlaceholder(new Label("No files for which the checksums have been calculated yet."));


        // Sort files and init TextField for search / filter.
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
        historyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        historyTableView.getSelectionModel().selectedItemProperty().addListener((observableValue) -> {
            KnkFile selectedItem = historyTableView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                masterDetailPane.setDetailNode(new DetailPane(selectedItem));
                masterDetailPane.setShowDetailNode(true);
            } else {
                masterDetailPane.setShowDetailNode(false);
            }
        });

        // MasterDetailPane
        masterDetailPane.setMasterNode(historyTableView);
        masterDetailPane.setShowDetailNode(false);
        masterDetailPane.setDetailNode(new Text("CHILD"));
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

    private static class DetailPane extends GridPane {

        private static KnkFile knkFile = null;

        DetailPane(KnkFile knkFile) {
            DetailPane.knkFile = knkFile;

            if (knkFile != null) {
                createGui();
            }
        }

        private void createGui() {
            this.setHgap(10);
            this.setVgap(10);
            this.setPadding(new Insets(10));

            Text idText = new Text("ID");
            Text dateText = new Text("Date");
            Text filenameText = new Text("File Name");
            Text fullPathText = new Text("Full Path");
            Text fileSizeText = new Text("File Size (binary prefix)");
            Text lastModifiedDateText = new Text("Last Modified Date");
            Text sha256Text = new Text("SHA-256");
            Text sha512Text = new Text("SHA-512");
            Text sha3384Text = new Text("SHA3-384");
            Text sha3512Text = new Text("SHA3-512");

            // Labels
            this.add(idText, 0, 0);
            this.add(dateText, 0, 1);
            this.add(filenameText, 0, 2);
            this.add(fullPathText, 0, 3);
            this.add(fileSizeText, 0, 4);
            this.add(lastModifiedDateText, 0, 5);
            this.add(sha256Text, 0, 6);
            this.add(sha512Text, 0, 7);
            this.add(sha3384Text, 0, 8);
            this.add(sha3512Text, 0, 9);

            // Values
            createTextField(knkFile.idProperty().asString(), 1, 0);
            createTextField(knkFile.dateProperty().asString(), 1, 1);
            createTextField(knkFile.fileNameProperty(), 1, 2);
            createTextField(knkFile.fullPathProperty(), 1, 3);

            long fileSize = knkFile.fileSizeProperty().get();
            String humanReadableByteCountBin = FileUtil.humanReadableByteCountBin(fileSize) + " (" + fileSize + " Bytes)";
            createTextField(new SimpleStringProperty(humanReadableByteCountBin), 1, 4);

            long lastModifiedDate = knkFile.lastModifiedProperty().get();
            Date date = new Date(lastModifiedDate);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            createTextField(new SimpleStringProperty(dateFormat.format(date)), 1, 5);

            createTextField(knkFile.sha256Property(), 1, 6);
            createTextField(knkFile.sha512Property(), 1, 7);
            createTextField(knkFile.sha3384Property(), 1, 8);
            createTextField(knkFile.sha3512Property(), 1, 9);

            // Delete Button
            Button deleteButton = new Button("Delete entry");
            deleteButton.getStyleClass().addAll("btn", "btn-danger");
            FontIcon fontIcon = new FontIcon();
            fontIcon.setIconLiteral("far-trash-alt");
            fontIcon.setIconSize(16);
            fontIcon.setIconColor(Color.WHITE);
            deleteButton.setGraphic(fontIcon);
            deleteButton.setTooltip(new Tooltip("Delete the entry only from this software, not from disk."));
            deleteButton.setOnAction(event -> {
                FileDAO.delete(knkFile.getId());
            });

            this.add(deleteButton, 0, 11);

            // Export to text file Button
            Button exportTextFileButton = new Button("Export to text file");
            exportTextFileButton.getStyleClass().addAll("btn", "btn-primary");
            FontIcon icon = new FontIcon();
            icon.setIconLiteral("fas-file-export");
            icon.setIconColor(Color.WHITE);
            icon.setIconSize(16);
            exportTextFileButton.setGraphic(fontIcon);
            exportTextFileButton.setTooltip(new Tooltip("Exports all details into a text file."));
            exportTextFileButton.setOnAction(event -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(knkFile.getId() + " - " + knkFile.getName() + ".txt");

                //Set extension filter for text files
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);

                //Show save file dialog
                File file = fileChooser.showSaveDialog(ChecksumSharerApplication.getPrimaryStage());

                if (file != null) {
                    FileUtil.saveTextToFile(knkFile.formatForTextFile(), file);
                }
            });

            this.add(exportTextFileButton, 1, 11);
        }

        private void createTextField(ObservableValue property, int column, int row) {
            TextField textField = new TextField();
            textField.textProperty().bind(property);
            textField.setEditable(false);
            textField.setMinWidth(900);
            this.add(textField, column, row);

            Button copyButton = new Button("Copy");
            copyButton.getStyleClass().addAll("btn", "btn-primary");

            FontIcon fontIcon = new FontIcon();
            fontIcon.setIconLiteral("far-copy");
            fontIcon.setIconSize(16);
            fontIcon.setIconColor(Color.WHITE);
            copyButton.setGraphic(fontIcon);

            copyButton.setOnAction(event -> {
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(textField.getText());
                clipboard.setContent(clipboardContent);
            });

            this.add(copyButton, column + 1, row);
        }
    }
}
