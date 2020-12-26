package server;

import java.sql.*;

public class SQLHandler {
    private static Connection connection;
    private static Statement statement;

    public static void connect () {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:server/database.db");
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void disconnect () {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getNickOnLoginPass (String login, String pass) throws SQLException {
        try {
            ResultSet rs = statement.executeQuery("SELECT nickname FROM users WHERE login ='" + login + "' AND password = '" + pass + "'");
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean tryToRegistNewUser (String login, String pass, String nick) {
        try {
            statement.executeUpdate("INSERT INTO users login='" + login + "' password='" + pass + "' nickname='" + nick + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isNickInDb (String nick) {
        try {
            ResultSet rs = statement.executeQuery("SELECT nickname FROM users WHERE nickname='" + nick + "'");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean tryToUpdateNickInDb (String nickNew, String nickOld) {
        try {
            statement.executeUpdate("UPDATE users SET nickname='" + nickNew + "' WHERE nickname='" + nickOld + "'");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
