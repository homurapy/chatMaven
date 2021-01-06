package server.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionService {
    private ConnectionService () {
    }

    public static Connection connectSQLite () {
        try {
            return DriverManager.getConnection("jdbc:sqlite:server/database.db");
        } catch (SQLException throwables) {
            throw new RuntimeException("SWW", throwables);
        }
    }

    public static void rollback (Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close (Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
