package dev.kanka.checksumsharer.models;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Settings {

    public static final String[] LANGUAGES = {"English", "German"};

    private ListProperty<String> language = new SimpleListProperty<>(FXCollections.observableArrayList( LANGUAGES));

    private ListProperty<String> dateFormat = new SimpleListProperty<>(FXCollections.observableArrayList("yyyy.MM.dd HH:mm:ss", "dd.MM.yyyy HH:mm:ss"));

    private SimpleStringProperty exportLocation = new SimpleStringProperty();

    public ObservableList<String> getLanguage() {
        return language.get();
    }

    public ListProperty<String> languageProperty() {
        return language;
    }

    public void setLanguage(ObservableList<String> language) {
        this.language.set(language);
    }

    public ObservableList<String> getDateFormat() {
        return dateFormat.get();
    }

    public ListProperty<String> dateFormatProperty() {
        return dateFormat;
    }

    public void setDateFormat(ObservableList<String> dateFormat) {
        this.dateFormat.set(dateFormat);
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
