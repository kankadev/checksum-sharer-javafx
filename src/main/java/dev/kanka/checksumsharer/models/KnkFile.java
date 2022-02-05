package dev.kanka.checksumsharer.models;

import dev.kanka.checksumsharer.utils.FormatUtil;
import javafx.beans.property.*;

import java.sql.Timestamp;
import java.util.Objects;

public class KnkFile extends java.io.File {
    private final ReadOnlyIntegerWrapper id;
    private final ReadOnlyObjectWrapper<Timestamp> timestamp;
    private final ReadOnlyStringWrapper fileName;
    private final ReadOnlyStringWrapper fullPath;
    private final ReadOnlyLongWrapper lastModified;
    private final ReadOnlyLongWrapper fileSize;
    private final ReadOnlyStringWrapper sha256;
    private final ReadOnlyStringWrapper sha512;
    private final ReadOnlyStringWrapper sha3384;
    private final ReadOnlyStringWrapper sha3512;

    public KnkFile(String pathname) {
        super(pathname);
        this.id = new ReadOnlyIntegerWrapper();
        this.timestamp = new ReadOnlyObjectWrapper<>(); // TODO type
        this.fileName = new ReadOnlyStringWrapper(super.getName());
        this.fullPath = new ReadOnlyStringWrapper(pathname);
        this.lastModified = new ReadOnlyLongWrapper(super.lastModified());
        this.fileSize = new ReadOnlyLongWrapper(super.length());
        this.sha256 = new ReadOnlyStringWrapper();
        this.sha512 = new ReadOnlyStringWrapper();
        this.sha3384 = new ReadOnlyStringWrapper();
        this.sha3512 = new ReadOnlyStringWrapper();
    }

    public KnkFile(int id, Timestamp ts, String fileName, String fullPath, Long lastModified, Long fileSize, String sha256, String sha512, String sha3384, String sha3512) {
        super(fullPath);
        this.timestamp = new ReadOnlyObjectWrapper<>(ts);
        this.fileName = new ReadOnlyStringWrapper(fileName);
        this.fullPath = new ReadOnlyStringWrapper(fullPath);
        this.lastModified = new ReadOnlyLongWrapper(lastModified);
        this.fileSize = new ReadOnlyLongWrapper(fileSize);
        this.id = new ReadOnlyIntegerWrapper(id);
        this.sha256 = new ReadOnlyStringWrapper(sha256);
        this.sha512 = new ReadOnlyStringWrapper(sha512);
        this.sha3384 = new ReadOnlyStringWrapper(sha3384);
        this.sha3512 = new ReadOnlyStringWrapper(sha3512);
    }

    public String formatForTextFile() {
        StringBuilder stringBuilder = new StringBuilder();
        String separator = System.getProperty("line.separator");

        stringBuilder.append("ID in SQLite database: ").append("\t").append(getId()).append(separator)
                .append("Timestamp (GMT): ").append("\t\t").append(getTimestamp()).append(separator)
                .append("File Name: ").append("\t\t\t\t").append(getFileName()).append(separator)
                .append("Full Path: ").append("\t\t\t\t").append(getFullPath()).append(separator)
                .append("Last Modified Date: ").append("\t").append(FormatUtil.getTimestamp(getLastModified())).append(separator)
                .append("File Size: ").append("\t\t\t\t").append(FormatUtil.getFileSize(getFileSize())).append(separator)
                .append("SHA-256: ").append("\t\t\t\t").append(getSha256()).append(separator)
                .append("SHA-512: ").append("\t\t\t\t").append(getSha512()).append(separator)
                .append("SHA3-384: ").append("\t\t\t\t").append(getSha3384()).append(separator)
                .append("SHA3-512: ").append("\t\t\t\t").append(getSha3512()).append(separator)
                .append(separator).append("----------------------------------------------------------------------------------------------").append(separator)
                .append("Checksums calculated with Checksum Sharer: https://github.com/kankadev/checksum-sharer-javafx/");

        return stringBuilder.toString();
    }

    // getter
    public int getId() {
        return id.get();
    }

    public Timestamp getTimestamp() {
        return timestamp.get();
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

    public String getSha256() {
        return sha256.get();
    }

    public String getSha512() {
        return sha512.get();
    }

    public String getSha3384() {
        return sha3384.get();
    }

    public String getSha3512() {
        return sha3512.get();
    }

    // setter
    public void setSha256(String s) {
        sha256.set(s);
    }

    public void setSha512(String s) {
        sha512.set(s);
    }

    public void setSha3384(String s) {
        this.sha3384.set(s);
    }

    public void setSha3512(String s) {
        this.sha3512.set(s);
    }

    // properties
    public ReadOnlyIntegerProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Timestamp> timestampProperty() {
        return timestamp.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty fileNameProperty() {
        return fileName.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty fullPathProperty() {
        return fullPath.getReadOnlyProperty();
    }

    public ReadOnlyLongProperty lastModifiedProperty() {
        return lastModified.getReadOnlyProperty();
    }

    public ReadOnlyLongProperty fileSizeProperty() {
        return fileSize.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty sha256Property() {
        return sha256.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty sha512Property() {
        return sha512.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty sha3384Property() {
        return sha3384.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty sha3512Property() {
        return sha3512.getReadOnlyProperty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        KnkFile knkFile = (KnkFile) o;
        return id.equals(knkFile.id) && timestamp.equals(knkFile.timestamp) &&
                fileName.equals(knkFile.fileName) && fullPath.equals(knkFile.fullPath) &&
                lastModified.equals(knkFile.lastModified) && fileSize.equals(knkFile.fileSize) &&
                sha256.equals(knkFile.sha256) && sha512.equals(knkFile.sha512) &&
                sha3384.equals(knkFile.sha3384) && sha3512.equals(knkFile.sha3512);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, timestamp, fileName, fullPath, lastModified, fileSize, sha256, sha512, sha3384, sha3512);
    }

    @Override
    public String toString() {
        return "KnkFile{" +
                "id=" + id +
                ", timestamp=" + timestamp +
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