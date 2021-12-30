package edu.unisa.ILE.FSA.SourceAdaptor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import edu.unisa.ILE.FSA.EnginePortal.FDEApplication;
import edu.unisa.ILE.FSA.EnginePortal.JSONFunctions;
import edu.unisa.ILE.FSA.InternalDataStructure.RCO;

/**
 * Created by wenhaoli on 22/06/2017.
 */
public class PromisAdapter implements Adapter {

    public JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {
        System.out.println("criteria: " + criteria);
        String operation = criteria.get("operation").toString();
        JSONObject result = new JSONObject();

        switch (operation) {
            case "findEntities":
                result = findEntities(types, criteria);
                break;
            case "findEntitiesByKeyword":
                break;
            case "getBinaryContent":
                break;
            case "getLinks":
                break;
            case "getAdjacentEntities":
                break;
        }

        return result;
    }

    public JSONObject findEntities(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {
        ArrayList<String> sourceTypes;
        if (!types.isEmpty()) {
            sourceTypes = types;
        } else {
            sourceTypes = Application.getAllEntityTypes();
        }

        JSONObject payload = new JSONObject();
        JSONArray payloadArray = new JSONArray();
        System.out.println(sourceTypes);

        HashMap<String, String> credential = getCredential(criteria);
        String username = credential.get("username");
        String password = credential.get("password");

        try {
            Connection c = connect(Application.PromisJDBC, username, password);
            for (String type : sourceTypes) {
                try {
                    JSONArray a = findEntities(type, criteria, c);
                    payloadArray = JSONFunctions.simpleMerge(payloadArray, a);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
            c.close();
        } catch (SQLException e) {
            JSONArray error = new JSONArray();
            JSONObject responseBody = new JSONObject();
            responseBody.put("request_status", e.getMessage());
            responseBody.put("status_code", e.getSQLState());
            error.add(responseBody);
            payload.put("fail", error);
            return payload;
        } catch (Exception e) {
            e.printStackTrace();
        }

        payload.put("success", payloadArray);
        return payload;
    }

    public JSONArray findEntities(String type, LinkedHashMap<String, Object> criteria, Connection c)
        throws SQLException {
        String sourceType = convertToSourceType(type);
        String sql;
        LinkedHashMap<String, String> projection_spec = (LinkedHashMap<String, String>) criteria.get("projection_spec");

        String columns = "";
        if (projection_spec != null) {
            Set<String> keys = projection_spec.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = projection_spec.get(key);
                if (value.toLowerCase().equals("true")) {
                    key = convertToSourceColumn(key);
                    columns += key;
                    if (iterator.hasNext()) {
                        columns += ",";
                    }
                }
            }
        }
        if (columns.equals("")) {
            columns = "*";
        }

        JSONArray payload = new JSONArray();
        if (sourceType != null) {
            sql = "select " + columns + " from " + sourceType + " where " + toSQLScript(criteria).toUpperCase();
            System.out.println(sql);
            ResultSet rs = query(c, sql);
            payload = extractPayload(rs);
            rs.close();
        }

        return payload;
    }

    //hard coded version
    public String convertToSourceType(String input) {
        String result = null;
        switch (input) {
            case "person":
                result = "persons";
                break;
            default:
                result = input;
        }
        return result;
    }

    //hard coded version
    public String convertToSourceColumn(String input) {
        String result = null;
        switch (input) {
            case "id":
                result = "person_id";
                break;
            case "first_name":
                result = "given_name1";
                break;
            case "_all":
                result = "given_name1";
                break;
            default:
                result = input;
        }
        return result;
    }

    public String convertToGenericColumn(String input) {
        String result = null;
        switch (input) {
            case "person_id":
                result = "id";
                break;
            case "given_name1":
                result = "first_name";
                break;
            default:
                result = input;
        }
        return result;
    }

    public Connection connect(String url, String user, String password) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection(url, user, password);
        System.out.println("Opened database successfully");
        return c;
    }

    public void update(Connection c, String sql) throws Exception {
        Statement stmt = c.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        System.out.println("update successful!");
    }

    public ResultSet query(Connection c, String sql) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        stmt.closeOnCompletion();
        return rs;
    }

    public JSONArray extractPayload(ResultSet rs) throws SQLException {
        JSONArray payload_spec = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            JSONObject object_payload = new JSONObject();
            for (int i = 1; i <= columnsNumber; i++) {
                String columnName = rsmd.getColumnName(i);
                String columnValue = rs.getString(i);
                columnName = convertToGenericColumn(columnName);
                object_payload.put(columnName, columnValue);
            }
            JSONObject match_entry = new JSONObject();
            match_entry.put("payload", object_payload);
            match_entry.put("info", "promis");
            payload_spec.add(match_entry);
        }
        return payload_spec;
    }

    public String toSQLScript(LinkedHashMap<String, Object> criteria) {
        RCO rco = (RCO) criteria.get("rco");
        String SQLScript = printSyntaxTree(rco);
        SQLScript =
            SQLScript.replace("$eq", " = ").replace("$and", " and ").replace("$or", " or ").replace("$lt", " < ")
                .replace("$in", " in ");
//        System.out.println(SQLScript);
        return SQLScript;
    }

    public String printSyntaxTree(RCO rco) {
        String equation = "";
        if (rco.getValue().toString().startsWith("$")) {
            for (int i = 0; i < rco.linkedRCOs.size(); i++) {
                if (i == rco.linkedRCOs.size() - 1) {
                    equation += printSyntaxTree(rco.linkedRCOs.get(i));
                } else {
                    equation += printSyntaxTree(rco.linkedRCOs.get(i));
                    equation += rco.getValue().toString();
                }
            }
            equation = "(" + equation + ")";
        } else {
            String value = rco.getValue().toString();
            equation = convertToSourceColumn(value);
        }
        return equation;
    }

    public HashMap<String, String> getCredential(LinkedHashMap<String, Object> criteria) {
        HashMap<String, String> result = new HashMap();
        JSONArray credentialList = (JSONArray) criteria.get("credentiallist");
        String sourceName = "promis";
        for (int i = 0; i < credentialList.size(); i++) {
            LinkedHashMap credential = (LinkedHashMap) credentialList.get(i);
            if (credential.containsKey(sourceName)) {
                result.put("username", (String) ((LinkedHashMap) credential.get(sourceName)).get("username"));
                result.put("password", (String) ((LinkedHashMap) credential.get(sourceName)).get("password"));
                break;
            }
        }
        return result;
    }

}
