/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;

/**
 *
 * @author c0687631
 */

@ApplicationScoped
public class DBUtils {
   private final static String studentNumber = "c0690651";

    /**
     * Utility method used to create a Database Connection
     *
     * @return the Connection object
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        String server = "ipro.lambton.on.ca";
        String username = studentNumber + "-java";
        String password = studentNumber;
        String database = username;
        String jdbc = String.format("jdbc:mysql://%s/%s", server, database);
        return DriverManager.getConnection(jdbc, username, password);
    }
}
