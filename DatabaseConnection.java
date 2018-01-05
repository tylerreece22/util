package 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_DRIVER = "org.postgresql.Driver";
    protected static String username;
    protected static String password;

    public static void getUserAndPass(String connection) {
    }

    public static Connection getDBConnection(String connection) {

        getUserAndPass(connection);
        Connection dbConnection = null;

        try {

            Class.forName(DB_DRIVER);

        } catch (ClassNotFoundException e) {

            System.out.println(e.getMessage());

        }

        try {

            dbConnection = DriverManager.getConnection(
                    connection, getUsername(), getPassword());
            return dbConnection;

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        }

        return dbConnection;

    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        DatabaseConnection.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        DatabaseConnection.password = password;
    }
}
