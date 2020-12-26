package server;

import java.sql.SQLException;

public class MainServer {

    public static void main (String[] args) throws SQLException {
        new Server();

        System.out.println(SQLHandler.getNickOnLoginPass("login1", "pass1"));
    }
}
