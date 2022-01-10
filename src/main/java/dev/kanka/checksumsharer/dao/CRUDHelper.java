package dev.kanka.checksumsharer.dao;

import org.apache.logging.log4j.LogManager;

import java.sql.*;

public class CRUDHelper {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    public static Object read(String tableName, String fieldName, int fieldDataType,
                              String indexFieldName, int indexDataType, Object index) {

        StringBuilder queryBuilder = new StringBuilder("Select ");
        queryBuilder.append(fieldName);
        queryBuilder.append(" from ");
        queryBuilder.append(tableName);
        queryBuilder.append(" where ");
        queryBuilder.append(indexFieldName);
        queryBuilder.append(" = ");
        queryBuilder.append(convertObjectToSQLField(index, indexDataType));

        try (Connection connection = Database.connect()) {

            PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

            try (ResultSet rs = statement.executeQuery()) {
                rs.next();

                switch (fieldDataType) {
                    case Types.INTEGER:
                        return rs.getInt(fieldName);
                    case Types.VARCHAR:
                        return rs.getString(fieldName);
                    default:
                        throw new IllegalArgumentException("Index type " + indexDataType + " from sql.Types is not yet supported.");
                }
            }
        } catch (SQLException e) {
            logger.error("Could not fetch from " + tableName + " by index " + index + " and column " + fieldName, e);

            return null;
        }
    }

    /**
     * @param tableName
     * @param columns
     * @param values
     * @param types
     * @param indexFieldName
     * @param indexDataType
     * @param index
     * @return number of affected rows
     */
    public static int update(String tableName, String[] columns, Object[] values, int[] types,
                             String indexFieldName, int indexDataType, Object index) {

        int number = Math.min(Math.min(columns.length, values.length), types.length);

        StringBuilder queryBuilder = new StringBuilder("UPDATE " + tableName + " SET ");

        for (int i = 0; i < number; i++) {
            queryBuilder.append(columns[i]);
            queryBuilder.append(" = ");
            queryBuilder.append(convertObjectToSQLField(values[i], types[i]));
            if (i < number - 1) queryBuilder.append(", ");
        }
        queryBuilder.append(" WHERE ");
        queryBuilder.append(indexFieldName);
        queryBuilder.append(" = ");
        queryBuilder.append(convertObjectToSQLField(index, indexDataType));

        try (Connection conn = Database.connect()) {

            PreparedStatement preparedStatement = conn.prepareStatement(queryBuilder.toString());
            return preparedStatement.executeUpdate(); //number of affected rows

        } catch (SQLException e) {
            logger.error("Could not add file to database", e);

            return -1;
        }
    }

    public static long create(String tableName, String[] columns, Object[] values, int[] types) {

        int number = Math.min(Math.min(columns.length, values.length), types.length);

        StringBuilder queryBuilder = new StringBuilder("INSERT INTO " + tableName + " (");

        for (int i = 0; i < number; i++) {
            queryBuilder.append(columns[i]);
            if (i < number - 1) queryBuilder.append(", ");
        }

        queryBuilder.append(") ");
        queryBuilder.append(" VALUES (");

        for (int i = 0; i < number; i++) {
            queryBuilder.append("?");
            if (i < number - 1) queryBuilder.append(", ");
        }

        queryBuilder.append(");");

        logger.debug("queryBuilder: " + queryBuilder);

        try (Connection conn = Database.connect(); PreparedStatement preparedStatement = conn.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < number; i++) {
                logger.debug(i);

                switch (types[i]) {
                    case Types.VARCHAR:
                        preparedStatement.setString(i + 1, (String) values[i]);
                        break;
                    case Types.INTEGER:
                        preparedStatement.setLong(i + 1, Long.parseLong(values[i].toString()));
                }
                logger.debug(i + " " + values[i]);
            }

            logger.debug("preparedStatement: " + preparedStatement.toString());

            int affectedRows = preparedStatement.executeUpdate();
            // check the affected rows
            if (affectedRows > 0) {
                // get the ID back
                try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Could not add file to database", e);
            return -1;
        }
        return -1;
    }

    /**
     * @param tableName
     * @param id
     * @return affected row(s) count
     */
    public static int delete(String tableName, int id) {

        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = Database.connect()) {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Could not delete from " + tableName + " by id " + id + "  because " + e.getCause(), e);

            return -1;
        }
    }

    private static String convertObjectToSQLField(Object value, int type) {

        StringBuilder queryBuilder = new StringBuilder();
        switch (type) {
            case Types.VARCHAR:
                queryBuilder.append("'");
                queryBuilder.append(value);
                queryBuilder.append("'");
                break;
            case Types.INTEGER:
                queryBuilder.append(value);
                break;
            default:
                throw new IllegalArgumentException("Index type " + type + " from sql.Types is not yet supported.");
        }
        return queryBuilder.toString();
    }
}