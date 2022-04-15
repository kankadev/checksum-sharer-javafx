package dev.kanka.checksumsharer.dao;

import dev.kanka.checksumsharer.models.KnkFile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Optional;


public class FileDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String TABLE_NAME = "knkFiles";
    public static final String idColumn = "id";
    public static final String dateColumn = "date";
    public static final String fileNameColumn = "fileName";
    public static final String fullPathColumn = "fullPath";
    public static final String lastModifiedColumn = "lastModified";
    public static final String fileSizeColumn = "fileSize";
    public static final String sha256Column = "sha256";
    public static final String sha512Column = "sha512";
    public static final String sha3384Column = "sha3384";
    public static final String sha3512Column = "sha3512";

    private static final ObservableList<KnkFile> KNK_FILES = FXCollections.observableArrayList();

    static {
        // TODO double call of Database.isOK
        if (Database.isOK())
            updateFilesFromDB();
    }

    public static ObservableList<KnkFile> getFiles() {
        return FXCollections.unmodifiableObservableList(KNK_FILES);
    }


    public static boolean createTable() {
        try (Connection connection = Database.connect()) {

            String query = "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + dateColumn + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + fileNameColumn + " TEXT NOT NULL, "
                    + fullPathColumn + " TEXT NOT NULL, "
                    + lastModifiedColumn + " INTEGER NOT NULL, "
                    + fileSizeColumn + " INTEGER NOT NULL, "
                    + sha256Column + " TEXT NOT NULL, "
                    + sha512Column + " TEXT NOT NULL, "
                    + sha3384Column + " TEXT NOT NULL, "
                    + sha3512Column + " TEXT NOT NULL"
                    + ")";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.execute();

            return true;
        } catch (SQLException e) {
            LOGGER.error(e);
            return false;
        }
    }

    private static void updateFilesFromDB() {
        LOGGER.debug("updateFilesFromDB()");

        String query = "SELECT * FROM " + TABLE_NAME;

        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            KNK_FILES.clear();
            while (rs.next()) {
                KnkFile knkFile = new KnkFile(
                        rs.getInt(idColumn),
                        rs.getTimestamp(dateColumn),
                        rs.getString(fileNameColumn),
                        rs.getString(fullPathColumn),
                        rs.getLong(lastModifiedColumn),
                        rs.getLong(fileSizeColumn),
                        rs.getString(sha256Column),
                        rs.getString(sha512Column),
                        rs.getString(sha3384Column),
                        rs.getString(sha3512Column)
                );
                LOGGER.debug(knkFile);
                KNK_FILES.add(knkFile);
            }
        } catch (SQLException e) {
            LOGGER.error("Could not load KNK_FILES from database.", e);
            KNK_FILES.clear();
        }
    }

    public static void update(KnkFile newKnkFile) {
        //update database
        int rows = CRUDHelper.update(
                TABLE_NAME,
                new String[]{fileNameColumn, fullPathColumn, lastModifiedColumn, lastModifiedColumn,
                        sha256Column, sha512Column, sha3384Column, sha3512Column},
                new Object[]{newKnkFile.getFileName(), newKnkFile.getFullPath(), newKnkFile.getLastModified(),
                        newKnkFile.getFileSize(), newKnkFile.getSha256(), newKnkFile.getSha512(),
                        newKnkFile.getSha3384(), newKnkFile.getSha3512()},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER,
                        Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR},
                idColumn,
                Types.INTEGER,
                newKnkFile.getId()
        );

        if (rows == 0)
            throw new IllegalStateException("KnkFile to be updated with id " + newKnkFile.getId() + " didn't exist in database.");

        //update cache
        Optional<KnkFile> optionalFile = getFile(newKnkFile.getId());
        optionalFile.ifPresentOrElse((oldKnkFile) -> {
            KNK_FILES.remove(oldKnkFile);
            KNK_FILES.add(newKnkFile);
        }, () -> {
            throw new IllegalStateException("KnkFile to be updated with id " + newKnkFile.getId() + " didn't exist in database");
        });
    }

    public static int insertFile(KnkFile file) {
        return insertFile(
                file.getFileName(),
                file.getAbsolutePath(),
                file.lastModified(),
                file.length(),
                file.getSha256(),
                file.getSha512(),
                file.getSha3384(),
                file.getSha3512());
    }

    public static int insertFile(String fileName, String fullPath, Long lastModified, Long fileSize,
                                 String sha256, String sha512, String sha3384, String sha3512) {
        //update database
        int id = (int) CRUDHelper.create(
                TABLE_NAME,
                new String[]{fileNameColumn, fullPathColumn, lastModifiedColumn, fileSizeColumn,
                        sha256Column, sha512Column, sha3384Column, sha3512Column},
                new Object[]{fileName, fullPath, lastModified, fileSize, sha256, sha512, sha3384, sha3512},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER,
                        Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR});

        updateFilesFromDB();
        return id;
    }

    public static void delete(int id) {
        //update database
        CRUDHelper.delete(TABLE_NAME, id);

        //update cache
        Optional<KnkFile> file = getFile(id);
        file.ifPresent(KNK_FILES::remove);

    }

    public static void delete(List<KnkFile> files) {
        for (KnkFile knkFile : files) {
            CRUDHelper.delete(TABLE_NAME, knkFile.getId());
        }
        updateFilesFromDB();
    }

    public static Optional<KnkFile> getFile(int id) {
        for (KnkFile knkFile : KNK_FILES) {
            if (knkFile.getId() == id) return Optional.of(knkFile);
        }
        return Optional.empty();
    }

}