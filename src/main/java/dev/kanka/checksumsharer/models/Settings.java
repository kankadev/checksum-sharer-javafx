package dev.kanka.checksumsharer.models;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Settings {

    private ListProperty<String> dateFormatProperty = new SimpleListProperty<>(FXCollections.observableArrayList("yyyy.MM.dd HH:mm:ss", "dd.MM.yyyy HH:mm:ss"));


    public ObservableList<String> getDateFormat() {
        return dateFormatProperty.get();
    }

    public ListProperty<String> getDateFormatProperty() {
        return dateFormatProperty;
    }

}
