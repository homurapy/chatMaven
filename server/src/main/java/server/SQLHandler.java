package server;

import javax.lang.model.type.NullType;
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
        connect();

    try(PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login, password,nickname) VALUES (?, ?, ?)")) {
            connection.setAutoCommit(false);
            statement.setString(1, login);
            statement.setString(2, pass);
            statement.setString(3, nick);
            int row = statement.executeUpdate();
            System.out.println(row);
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }finally {
            disconnect();
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
    public static boolean isLoginInDb (String login) {
        try {
            ResultSet rs = statement.executeQuery("SELECT login FROM users WHERE nickname='" + login + "'");
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean tryToUpdateNickInDb (String nickNew, String nickOld) {
        connect();
        try {
            connection.setAutoCommit(false);
            statement.executeUpdate("UPDATE users SET nickname='" + nickNew + "' WHERE nickname='" + nickOld + "'");
            connection.commit();
            return true;
        } catch (SQLException e) {
            rollback(connection);
            e.printStackTrace();
            return false;
        }finally {
            disconnect();
        }
    }
    public static void rollback (Connection connection){
        try {
            connection.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
