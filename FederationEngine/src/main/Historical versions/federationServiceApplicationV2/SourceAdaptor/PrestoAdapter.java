package edu.unisa.ILE.FSA.SourceAdaptor;

import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


/**
 * Created by wenhaoli on 6/7/17.
 */
public class PrestoAdapter implements Adapter {

    public HashMap getCredential(LinkedHashMap<String, Object> criteria) {
        return null;
    }

    public JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {
        return null;
    }

//    public static void main(String[] args){
//        try{
//            String url = "jdbc:presto://130.220.209.255:8080/postgresql?user=unisa&password=unisa";
//            Connection c = DriverManager.getConnection(url);
//            ResultSet rs = query(c,"SELECT * FROM promis.public.persons");
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }

    public static ResultSet query(Connection c, String sql) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        stmt.closeOnCompletion();
        return rs;
    }
}
