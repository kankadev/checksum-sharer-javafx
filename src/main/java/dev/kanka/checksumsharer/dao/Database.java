package dev.kanka.checksumsharer.dao;

import dev.kanka.checksumsharer.ChecksumSharerApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class Database {
    private static final Logger logger = LogManager.getLogger();

    private static final String[] requiredTables = {FileDAO.TABLE_NAME};
    public static final String DB_RESOURCE = ChecksumSharerApplication.class.getResource("databases/checksumsharer.db").toExternalForm();

    public static boolean isOK() {
        if (!checkDrivers()) return false;

        if (!checkConnection()) return false;

        if (!FileDAO.createTable()) return false;

        return checkTables();
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
            if (connection != null) {
                DatabaseMetaData metaData = connection.getMetaData();
                logger.debug(metaData.toString());
            }
            return connection != null;
        } catch (SQLException e) {
            logger.error("Could not connect to database", e);
            return false;
        }
    }

    private static boolean checkTables() {
        boolean isOk = false;

        try (Connection connection = connect()) {
            if (connection != null) {
                for (String table : requiredTables) {
                    String statement = "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + table + "'";

                    PreparedStatement preparedStatement = connection.prepareStatement(statement);
                    ResultSet rs = preparedStatement.executeQuery();

                    while (rs.next()) {
                        if (rs.getString("tbl_name").equals(table)) {
                            isOk = true;
                        }
                    }
                }
            } else {
                logger.error("Connection is null.");
                isOk = false;
            }

            return isOk;

        } catch (SQLException e) {
            logger.error("Could not find tables in database", e);
            return false;
        }
    }

    protected static Connection connect() {
        String dbPrefix = "jdbc:sqlite:";
        Connection connection;
        try {
            connection = DriverManager.getConnection(dbPrefix + DB_RESOURCE);
        } catch (SQLException e) {
            logger.error("Could not connect to SQLite DB at " + DB_RESOURCE, e);
            return null;
        }
        return connection;
    }

}