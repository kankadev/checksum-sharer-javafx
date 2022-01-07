package dev.kanka.checksumsharer.dao;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;


public class Database {
    private static final Logger logger = LogManager.getLogger();

    private static final String location = ChecksumSharerApplication.class.getResource("databases/" + DAOConstants.DB_NAME_FILES).toExternalForm();

    private static final String requiredTable = DAOConstants.TABLE_NAME_FILES;

    public static boolean isOK() {
        if (!checkDrivers()) return false; //driver errors

        if (!checkConnection()) return false; //can't connect to db

        return checkTables(); //tables didn't exist
    }

    private static boolean checkDrivers() {
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Could not start SQLite Drivers", e);
            return false;
        }
    }

    private static boolean checkConnection() {
        try (Connection connection = connect()) {
            return connection != null;
        } catch (SQLException e) {
            logger.error("Could not connect to database", e);
            return false;
        }
    }

    private static boolean checkTables() {
        String checkTables =
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + requiredTable + "'";

        try (Connection connection = Database.connect()) {
            PreparedStatement statement = connection.prepareStatement(checkTables);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                if (rs.getString("tbl_name").equals(requiredTable)) return true;
            }
        } catch (SQLException e) {
            logger.error("Could not find tables in database", e);
            return false;
        }
        return false;
    }

    protected static Connection connect() {
        String dbPrefix = "jdbc:sqlite:";
        Connection connection;
        try {
            connection = DriverManager.getConnection(dbPrefix + location);
        } catch (SQLException e) {
            logger.error("Could not connect to SQLite DB at " + location, e);
            return null;
        }
        return connection;
    }

}