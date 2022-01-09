package dev.kanka.checksumsharer.models;

import javafx.beans.property.*;

import java.sql.Timestamp;

public class File {
    private final ReadOnlyObjectProperty date;
    private final ReadOnlyStringProperty fileName;
    private final ReadOnlyStringProperty fullPath;
    private final ReadOnlyLongProperty lastModified;
    private final ReadOnlyLongProperty fileSize;
    private final int id;

    public File(Timestamp ts, String fileName, String fullPath, Long lastModified, Long fileSize, int id) {
        this.date = new SimpleObjectProperty<>(ts);
        this.fileName = new SimpleStringProperty(fileName);
        this.fullPath = new SimpleStringProperty(fullPath);
        this.lastModified = new SimpleLongProperty(lastModified);
        this.fileSize = new SimpleLongProperty(fileSize);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Timestamp getDate() {
        return (Timestamp) date.get();
    }

    public String getFileName() {
        return fileName.get();
    }

    public String getFullPath() {
        return fullPath.get();
    }

    public Long getLastModified() {
        return lastModified.get();
    }

    public Long getFileSize() {
        return fileSize.get();
    }

    public ReadOnlyObjectProperty dateProperty() {
        return date;
    }

    public ReadOnlyStringProperty fileNameProperty() {
        return fileName;
    }

    public ReadOnlyStringProperty fullPathProperty() {
        return fullPath;
    }

    public ReadOnlyLongProperty lastModifiedProperty() {
        return lastModified;
    }

    public ReadOnlyLongProperty fileSizeProperty() {
        return fileSize;
    }

    @Override
    public String toString() {
        return "File [Saved: " + date.get() + ", " + fileName.get() + ", " + fullPath.get() + ", lastModified: " + lastModified.get() + ", fileSize: " + fileSize.get() + " with id " + id + "]";
    }
}