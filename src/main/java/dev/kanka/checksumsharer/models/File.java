package dev.kanka.checksumsharer.models;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class File {
    private final ReadOnlyStringProperty fileName;
    private final ReadOnlyStringProperty lastName;
    private final ReadOnlyIntegerProperty age;
    private final int id;

    public File(String fileName, String lastName, Integer age, int id) {
        this.fileName = new SimpleStringProperty(fileName);
        this.lastName = new SimpleStringProperty(lastName);
        this.age = new SimpleIntegerProperty(age);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getFileName() {
        return fileName.get();
    }

    public ReadOnlyStringProperty fileNameProperty() {
        return fileName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public ReadOnlyStringProperty lastNameProperty() {
        return lastName;
    }

    public int getAge() {
        return age.get();
    }

    public ReadOnlyIntegerProperty ageProperty() {
        return age;
    }

    @Override
    public String toString() {
        return "File [" + fileName.get() + " " + lastName.get() + ", aged " + age.get() + " with id " + id + "]";
    }
}