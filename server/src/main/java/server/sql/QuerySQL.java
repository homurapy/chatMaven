package server.sql;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuerySQL {
    public String tryNickFromLoginPass(String login, String pass){
        Connection connection = ConnectionService.connectSQLite();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT nickname FROM users WHERE login = ? AND password = ?");
            statement.setString(1, login);
            statement.setString(2, pass);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
        ConnectionService.close(connection);
        }
        return null;
    }
    public boolean tryToRegistNewUser (String login, String pass, String nick) {
        Connection connection = ConnectionService.connectSQLite();
        try(PreparedStatement statement = connection.prepareStatement("INSERT INTO users (login, password,nickname) VALUES (?, ?, ?)")) {
            connection.setAutoCommit(false);
            statement.setString(1, login);
            statement.setString(2, pass);
            statement.setString(3, nick);
            int row = statement.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            ConnectionService.rollback(connection);
            return false;
        }finally {
            ConnectionService.close(connection);
        }
    }
    public boolean isNickInDb (String nick) {
        Connection connection= ConnectionService.connectSQLite();
        try (PreparedStatement statement = connection.prepareStatement("SELECT nickname FROM users WHERE nickname = ? "))
        {
            statement.setString(1, nick);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionService.close(connection);
        }
        return false;
    }
    public boolean isLoginInDb (String login) {
        Connection connection= ConnectionService.connectSQLite();
        try (PreparedStatement statement = connection.prepareStatement("SELECT login FROM users WHERE login = ? "))
        {
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            ConnectionService.close(connection);
        }
        return false;
    }

    public boolean tryToUpdateNickInDb (String nickNew, String nickOld) {
        Connection connection = ConnectionService.connectSQLite();
        try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET nickname = ? WHERE nickname = ? ")) {
            connection.setAutoCommit(false);
            statement.setString(1, nickNew);
            statement.setString(2, nickOld);
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            ConnectionService.rollback(connection);
            e.printStackTrace();
            return false;
        }finally {
            ConnectionService.close(connection);
        }
    }
}
