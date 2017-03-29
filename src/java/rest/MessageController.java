/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import static rest.DBUtils.getConnection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author c0687631
 */
@ApplicationScoped
public class MessageController {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private List<Message> messages;

    public MessageController() {
        
        messages = new ArrayList<>();
        refresh();
    }
    
    public void refresh(){
        
        try {
            Connection conn = DBUtils.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Products");
            while (rs.next()) {
                Message m = new Message();
                m.setId(rs.getInt("id"));
                m.setTitle(rs.getString("title"));
                m.setContents(rs.getString("contents"));
                m.setAuthor(rs.getString("author"));
                m.setSenttime(rs.getTime("senttime"));
               
                messages.add(m);
                
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JsonArray getAllJson() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Message m : messages) {
            json.add(m.toJson());
        }
        return json.build();
    }


    public Message getById(int id) {
        for (Message m : messages) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    public JsonObject getByIdJson(int id) {
        Message m = getById(id);
        if (m != null) {
            return getById(id).toJson();
        } else {
            return null;
        }
    }

    public JsonArray getByDateJson(Date from, Date to) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        for (Message m : messages) {
            if ((m.getSenttime().after(from) && m.getSenttime().before(to))
                    || m.getSenttime().equals(from) || m.getSenttime().equals(to)) {
                json.add(m.toJson());
            }
        }
        return json.build();
    }

    public JsonObject addJson(JsonObject json) {
        Message m = new Message(json);
        messages.add(m);
        return m.toJson();
    }

    public JsonObject editJson(int id, JsonObject json) {
        Message m = getById(id);
        m.setTitle(json.getString("title", ""));
        m.setContents(json.getString("contents", ""));
        m.setAuthor(json.getString("author", ""));
        String timeStr = json.getString("senttime", "");
        try {
            m.setSenttime(sdf.parse(timeStr));
        } catch (ParseException ex) {
            // This sets the time to NOW if there's a failure parsing
            m.setSenttime(new Date());
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, "Failed Parsing Date: " + timeStr);
        }
        return m.toJson();
    }

    public boolean deleteById(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM messages WHERE id=?");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            refresh();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(MessageController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            conn.close();
        }
    }

}
