package dev.kanka.checksumsharer.models;

import javafx.beans.property.*;

import java.sql.Timestamp;

public class KnkFile extends java.io.File {
    private final ReadOnlyIntegerProperty id;
    private final ReadOnlyObjectProperty date;
    private final ReadOnlyStringProperty fileName;
    private final ReadOnlyStringProperty fullPath;
    private final ReadOnlyLongProperty lastModified;
    private final ReadOnlyLongProperty fileSize;
    private final StringProperty sha256;
    private final StringProperty sha512;
    private final StringProperty sha3384;
    private final StringProperty sha3512;

    public KnkFile(String pathname) {
        super(pathname);
        this.id = new SimpleIntegerProperty();
        this.date = new SimpleObjectProperty<>();
        this.fileName = new SimpleStringProperty(super.getName());
        this.fullPath = new SimpleStringProperty(pathname);
        this.lastModified = new SimpleLongProperty(super.lastModified());
        this.fileSize = new SimpleLongProperty(super.length());
        this.sha256 = new SimpleStringProperty();
        this.sha512 = new SimpleStringProperty();
        this.sha3384 = new SimpleStringProperty();
        this.sha3512 = new SimpleStringProperty();
    }

    public KnkFile(Timestamp ts, String fileName, String fullPath, Long lastModified, Long fileSize, String sha256, String sha512, String sha3384, String sha3512, int id) {
        super(fullPath);
        this.date = new SimpleObjectProperty<>(ts);
        this.fileName = new SimpleStringProperty(fileName);
        this.fullPath = new SimpleStringProperty(fullPath);
        this.lastModified = new SimpleLongProperty(lastModified);
        this.fileSize = new SimpleLongProperty(fileSize);
        this.id = new SimpleIntegerProperty(id);
        this.sha256 = new SimpleStringProperty(sha256);
        this.sha512 = new SimpleStringProperty(sha512);
        this.sha3384 = new SimpleStringProperty(sha3384);
        this.sha3512 = new SimpleStringProperty(sha3512);
    }

    public int getId() {
        return id.get();
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

    public String getSha256() {
        return sha256.get();
    }

    public StringProperty sha256Property() {
        return sha256;
    }

    public void setSha256(String s) {
        sha256.set(s);
    }

    public String getSha512() {
        return sha512.get();
    }

    public StringProperty sha512Property() {
        return sha512;
    }

    public void setSha512(String s) {
        sha512.set(s);
    }

    public String getSha3384() {
        return sha3384.get();
    }

    public StringProperty sha3384() {
        return sha3384;
    }

    public void setSha3384(String s) {
        this.sha3384.set(s);
    }

    public String getSha3512() {
        return sha3512.get();
    }

    public StringProperty sha3512() {
        return sha3512;
    }

    public void setSha3512(String s) {
        this.sha3512.set(s);
    }

    @Override
    public String toString() {
        return "KnkFile{" +
                "id=" + id +
                ", date=" + date +
                ", fileName=" + fileName +
                ", fullPath=" + fullPath +
                ", lastModified=" + lastModified +
                ", fileSize=" + fileSize +
                ", sha256=" + sha256 +
                ", sha512=" + sha512 +
                ", sha3384=" + sha3384 +
                ", sha3512=" + sha3512 +
                '}';
    }
}