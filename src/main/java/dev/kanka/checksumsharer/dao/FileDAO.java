package dev.kanka.checksumsharer.dao;

import dev.kanka.checksumsharer.models.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Optional;


public class FileDAO {

    private static final Logger logger = LogManager.getLogger();
    public static final String TABLE_NAME = "knkFiles";
    public static final String idColumn = "id";
    public static final String dateColumn = "date";
    public static final String fileNameColumn = "fileName";
    public static final String fullPathColumn = "fullPath";
    public static final String lastModifiedColumn = "lastModified";
    public static final String fileSizeColumn = "fileSize";

    private static final ObservableList<File> files = FXCollections.observableArrayList();

    static {
        // TODO double call of Database.isOK
        if (Database.isOK())
            updateFilesFromDB();
    }

    public static ObservableList<File> getFiles() {
        return FXCollections.unmodifiableObservableList(files);
    }


    public static boolean createTable() {
        logger.debug("createTable()");

        try (Connection connection = Database.connect()) {

            String query = "CREATE TABLE IF NOT EXISTS '" + TABLE_NAME + "' ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + dateColumn + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + fileNameColumn + " TEXT NOT NULL, "
                    + fullPathColumn + " TEXT NOT NULL, "
                    + lastModifiedColumn + " INTEGER NOT NULL, "
                    + fileSizeColumn + " INTEGER NOT NULL"
                    + ")";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.execute();

            return true;
        } catch (SQLException e) {
            logger.error(e);
            return false;
        }
    }

    private static void updateFilesFromDB() {
        logger.debug("updateFilesFromDB()");

        String query = "SELECT * FROM " + TABLE_NAME;

        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            files.clear();
            while (rs.next()) {
                files.add(new File(
                        rs.getTimestamp(dateColumn),
                        rs.getString(fileNameColumn),
                        rs.getString(fullPathColumn),
                        rs.getLong(lastModifiedColumn),
                        rs.getLong(fileSizeColumn),
                        rs.getInt(idColumn)));
            }
        } catch (SQLException e) {
            logger.error("Could not load files from database.", e);
            files.clear();
        }
    }

    public static void update(File newFile) {
        //update database
        int rows = CRUDHelper.update(
                TABLE_NAME,
                new String[]{fileNameColumn, fullPathColumn, lastModifiedColumn},
                new Object[]{newFile.getFileName(), newFile.getFullPath(), newFile.getLastModified(), newFile.getFileSize()},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER},
                idColumn,
                Types.INTEGER,
                newFile.getId()
        );

        if (rows == 0)
            throw new IllegalStateException("File to be updated with id " + newFile.getId() + " didn't exist in database.");

        //update cache
        Optional<File> optionalFile = getFile(newFile.getId());
        optionalFile.ifPresentOrElse((oldFile) -> {
            files.remove(oldFile);
            files.add(newFile);
        }, () -> {
            throw new IllegalStateException("File to be updated with id " + newFile.getId() + " didn't exist in database");
        });
    }

    public static void insertFile(String fileName, String fullPath, Long lastModified, Long fileSize) {
        //update database
        int id = (int) CRUDHelper.create(
                TABLE_NAME,
                new String[]{fileNameColumn, fullPathColumn, lastModifiedColumn, fileSizeColumn},
                new Object[]{fileName, fullPath, lastModified, fileSize},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER});

        //update cache
//        files.add(new File(null, fileName, fullPath, lastModified, fileSize, id));
        updateFilesFromDB();
    }

    public static void insertFilesIntoDB(List<java.io.File> files) {
        for (java.io.File file: files) {
            FileDAO.insertFile(file.getName(), file.getAbsolutePath(), file.lastModified(), file.length());
        }
    }

    public static void delete(int id) {
        //update database
        CRUDHelper.delete(TABLE_NAME, id);

        //update cache
        Optional<File> file = getFile(id);
        file.ifPresent(files::remove);

    }

    public static Optional<File> getFile(int id) {
        for (File file : files) {
            if (file.getId() == id) return Optional.of(file);
        }
        return Optional.empty();
    }

}