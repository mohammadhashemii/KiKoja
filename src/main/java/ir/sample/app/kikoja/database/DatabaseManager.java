package ir.sample.app.kikoja.database;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    public static String host = "162.55.105.142";
    public static String port = "55432";
    public static String dbname = "KiKojaDB";
    public static String user = "m_hashemi";
    public static String pass = "fsd5u8a8aa4c1kk";
    public static String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname + "?user=" + user + "&password=" + pass;

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Can not connect to the database:\n" + e.getMessage());
        }

        return null;
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}