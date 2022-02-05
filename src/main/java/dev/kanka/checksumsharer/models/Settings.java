package dev.kanka.checksumsharer.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Settings {

    private static final String[] LANGUAGES = {"English", "Deutsch"};
    private static final String[] DATE_FORMATS = {"yyyy.MM.dd HH:mm:ss", "dd.MM.yyyy HH:mm:ss"};

    private ObjectProperty<String> language = new SimpleObjectProperty<>(LANGUAGES[0]);
    private ObjectProperty<String> dateFormat = new SimpleObjectProperty<>(DATE_FORMATS[0]);

    private ListProperty<String> allLanguages = new SimpleListProperty<>(FXCollections.observableArrayList(LANGUAGES));

    private ListProperty<String> allDateFormats = new SimpleListProperty<>(FXCollections.observableArrayList(DATE_FORMATS));

    // TODO
    private SimpleListProperty<String> exportLocations = new SimpleListProperty<>();

    private SimpleStringProperty exportLocation = new SimpleStringProperty("");

    public String getLanguage() {
        return language.get();
    }

    public ObjectProperty<String> languageProperty() {
        return language;
    }

    public void setLanguage(String language) {
        this.language.set(language);
    }

    public String getDateFormat() {
        return dateFormat.get();
    }

    public ObjectProperty<String> dateFormatProperty() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat.set(dateFormat);
    }

    public ObservableList<String> getAllLanguages() {
        return allLanguages.get();
    }

    public ListProperty<String> allLanguagesProperty() {
        return allLanguages;
    }

    public void setAllLanguages(ObservableList<String> allLanguages) {
        this.allLanguages.set(allLanguages);
    }

    public ObservableList<String> getAllDateFormats() {
        return allDateFormats.get();
    }

    public ListProperty<String> allDateFormatsProperty() {
        return allDateFormats;
    }

    public void setAllDateFormats(ObservableList<String> allDateFormats) {
        this.allDateFormats.set(allDateFormats);
    }

    public String getExportLocation() {
        return exportLocation.get();
    }

    public SimpleStringProperty exportLocationProperty() {
        return exportLocation;
    }

    public void setExportLocation(String exportLocation) {
        this.exportLocation.set(exportLocation);
    }
}
