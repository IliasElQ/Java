package com.example.javfx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Login_database {

    static String user = "root";
    static String password = "myroot2468";
    static String url = "jdbc:mysql://localhost/login";
    static String driver = "com.mysql.cj.jdbc.Driver";

    public static Connection getCon(){
        Connection con = null;
        try {
            Class.forName(driver);
            try {
                con = DriverManager.getConnection(url,user,password);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return con;
    }
}