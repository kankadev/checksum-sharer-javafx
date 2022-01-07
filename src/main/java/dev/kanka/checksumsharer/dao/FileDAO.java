package dev.kanka.checksumsharer.dao;

import dev.kanka.checksumsharer.models.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Optional;


public class FileDAO {

    private static final Logger logger = LogManager.getLogger();

    private static final String tableName = DAOConstants.TABLE_NAME_FILES;
    private static final String fileNameColumn = "fileName";
    private static final String lastNameColumn = "lastName";
    private static final String ageColumn = "Age";
    private static final String idColumn = "id";

    private static final ObservableList<File> files;

    static {
        files = FXCollections.observableArrayList();
        updateFilesFromDB();
    }

    public static ObservableList<File> getFiles() {
        return FXCollections.unmodifiableObservableList(files);
    }

    private static void updateFilesFromDB() {

        String query = "SELECT * FROM " + tableName;

        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            files.clear();
            while (rs.next()) {
                files.add(new File(
                        rs.getString(fileNameColumn),
                        rs.getString(lastNameColumn),
                        rs.getInt(ageColumn),
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
                tableName,
                new String[]{fileNameColumn, lastNameColumn, ageColumn},
                new Object[]{newFile.getFileName(), newFile.getLastName(), newFile.getAge()},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER},
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

    public static void insertFile(String firstName, String lastName, int age) {
        //update database
        int id = (int) CRUDHelper.create(
                tableName,
                new String[]{"LastName", "FirstName", "Age"},
                new Object[]{lastName, firstName, age},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.INTEGER});

        //update cache
        files.add(new File(
                firstName,
                lastName,
                age,
                id
        ));
    }

    public static void delete(int id) {
        //update database
        CRUDHelper.delete(tableName, id);

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