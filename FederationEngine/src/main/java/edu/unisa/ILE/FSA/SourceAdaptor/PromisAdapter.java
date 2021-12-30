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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import edu.unisa.ILE.FSA.CDMMappingRepository.SchemaTranslater;
import edu.unisa.ILE.FSA.EnginePortal.FDEApplication;
import edu.unisa.ILE.FSA.EnginePortal.JSONFunctions;
import edu.unisa.ILE.FSA.InternalDataStructure.RCO;

/**
 * Created by wenhaoli on 22/06/2017.
 */
public class PromisAdapter extends Adapter {

    private static String sourceName = "promis";
    private static int recordLimit = 50;

    public JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {
//        System.out.println("criteria: "+criteria);
        String operation = criteria.get("operation").toString();
        JSONObject result = new JSONObject();
        JSONObject credential = getCredential(criteria, sourceName);
        if (credential != null) {
            String username = (String) credential.get("username");
            String password = (String) credential.get("password");
            switch (operation) {
                case "findEntities":
                    result = findEntities(types, criteria, username, password);
                    break;
                case "findEntitiesByKeyword":
                    result = findEntitiesByKeyword(types, criteria, username, password);
                    break;
                case "getLinks":
                    break;
                case "getAdjacentEntities":
                    break;
            }
        }
        return result;
    }

    public JSONObject findEntities(ArrayList<String> types, LinkedHashMap<String, Object> criteria, String username,
                                   String password) {
        System.out.println("findEntities of " + sourceName + " triggered");

        //determin source types
        ArrayList<String> sourceTypes;
        if (!types.isEmpty()) {
            sourceTypes = types;
        } else {
            sourceTypes = FDEApplication.getAllEntityTypes(sourceName);
        }
        JSONObject payload = new JSONObject();
        JSONArray payloadArray = new JSONArray();
        System.out.println(sourceTypes);

        try {
            Connection c = connect(FDEApplication.PromisJDBC, username, password);
            for (String type : sourceTypes) {
                try {
                    JSONArray a = findEntities(type, criteria, c);
                    payloadArray = JSONFunctions.arrayMerge(payloadArray, a);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
            c.close();
        } catch (SQLException e) {
            JSONObject responseBody = new JSONObject();
            responseBody.put("request_status", e.getMessage());
            responseBody.put("status_code", e.getSQLState());
            payload.put("fail", responseBody);
            return payload;
        } catch (Exception e) {
            e.printStackTrace();
        }

        payload.put("success", payloadArray);
        return payload;
    }
//  convertToSourceType(String sourceSchemaName, String targetName)
//  convertToSourceColumn(String sourceSchemaName, String sourceAttriName, String targetName, String targetSchemaName)
    public JSONArray findEntities(String type, LinkedHashMap<String, Object> criteria, Connection c)
        throws SQLException {
        String sourceType = SchemaTranslater.convertToSourceType(type, sourceName);
        String sql;
        LinkedHashMap<String, String> projection_spec = (LinkedHashMap<String, String>) criteria.get("projection_spec");

        //determin select columns
        String columns = "";
        if (projection_spec != null) {
            Set<String> keys = projection_spec.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = projection_spec.get(key);
                if (value.toLowerCase().equals("true")) {
                    key = SchemaTranslater.convertToSourceColumn(type, key, sourceName, sourceType);
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

        //check fetching recordLimit of records
        int limit = 50;
        JSONObject window_spec = (JSONObject) criteria.get("window_spec");
        if (window_spec != null) {
            String limitString = (String) window_spec.get("recordLimit");
            if (limitString != null) {
                limit = Integer.parseInt(limitString);
            }
        }

        //compose sql script and execute
        JSONArray payload = new JSONArray();
        if (sourceType != null) {
            sql =
                "select " + columns + " from " + sourceType + " where " + convertToSourceQueryCriteria(criteria, type, sourceType)
                    .toUpperCase() + " limit " + limit;
            System.out.println(sql);
            ResultSet rs = query(c, sql);
            payload = extractPayload(rs, sourceType, type);
            rs.close();
//            rs.getStatement().close();
        }
        return payload;
    }

    public JSONObject findEntitiesByKeyword(ArrayList<String> types, LinkedHashMap<String, Object> criteria,
                                            String username, String password) {
        System.out.println("findEntitiesByKeyword of " + sourceName + " triggered");
        ArrayList<String> sourceTypes;
        if (!types.isEmpty()) {
            sourceTypes = types;
        } else {
            sourceTypes = FDEApplication.getAllEntityTypes(sourceName);
        }

        JSONObject payload = new JSONObject();
        JSONArray payloadArray = new JSONArray();
        System.out.println(sourceTypes);

        try {
            Connection c = connect(FDEApplication.PromisJDBC, username, password);
            for (String type : sourceTypes) {
                try {
                    JSONArray a = findEntitiesByKeyword(type, criteria, c);
                    payloadArray = JSONFunctions.arrayMerge(payloadArray, a);
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    continue;
                }
            }
            c.close();
        } catch (SQLException e) {
            JSONObject responseBody = new JSONObject();
            responseBody.put("request_status", e.getMessage());
            responseBody.put("status_code", e.getSQLState());
            payload.put("fail", responseBody);
            return payload;
        } catch (Exception e) {
            e.printStackTrace();
        }
        payload.put("success", payloadArray);
        return payload;
    }

    public JSONArray findEntitiesByKeyword(String type, LinkedHashMap<String, Object> criteria, Connection c)
        throws SQLException {
        String sourceType = SchemaTranslater.convertToSourceType(type, sourceName);
        String sql;
        LinkedHashMap<String, String> projection_spec = (LinkedHashMap<String, String>) criteria.get("projection_spec");
        // first, get filtered column names
        String columns = "";
        if (projection_spec != null) {
            Set<String> keys = projection_spec.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = projection_spec.get(key);
                if (value.toLowerCase().equals("true")) {
                    key = SchemaTranslater.convertToSourceColumn(type, key, sourceName, sourceType);
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
        //second, get document
        String column_name_doc = "";
        LinkedHashMap<String, String> columnList = getColumns(sourceType, c);
        Set<Map.Entry<String, String>> entries = columnList.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String column_name = entry.getKey();
            String data_type = entry.getValue();
            String notNullColumn = "";
            switch (data_type) {
                case "numeric":
                    notNullColumn = "coalesce(" + column_name + ",0)";
                    break;
                case "character varying":
                    notNullColumn = "coalesce(" + column_name + ",'')";
                    break;
                case "date":
                    notNullColumn = "coalesce(" + column_name + ",date '1900-01-1')";
                    break;
            }
            if (iterator.hasNext()) {
                column_name_doc += notNullColumn + " || ' ' || ";
            } else {
                column_name_doc += notNullColumn;
            }
        }
//        System.out.println(column_name_doc);

        //third, get criteria string
        JSONArray keywords = (JSONArray) criteria.get("keywords");
        String mode = (String) criteria.get("mode");
        String operator;
        if (mode != null) {
            if (mode.equals("all")) {
                operator = "&";
            } else {
                operator = "|";
            }
        } else {
            operator = "|";
        }

        String criteriaString = "";
        for (int i = 0; i < keywords.size(); i++) {
            String keyword = (String) keywords.get(i);
            if (i < keywords.size() - 1) {
                criteriaString += keyword + operator;
            } else {
                criteriaString += keyword;
            }
        }

        criteriaString = "'" + criteriaString + "'";
        System.out.println(criteriaString);

        //third, check and get scoring & ranking string
        String scoreScript;
        String score = (String) criteria.get("score");
        if (score != null && score.equals("true")) {
            scoreScript =
                "ts_rank_cd(to_tsvector('english'," + column_name_doc + "), to_tsquery(" + criteriaString
                + "), 2) AS score";
            columns += ", " + scoreScript;
        }

        //forth, check fetching recordLimit of records
        JSONObject window_spec = (JSONObject) criteria.get("window_spec");
        if (window_spec != null) {
            String limitString = (String) window_spec.get("recordLimit");
            if (limitString != null) {
                recordLimit = Integer.parseInt(limitString);
            }
        }

        //finally, composite query string and execute
        JSONArray payload = new JSONArray();
        if (sourceType != null) {
            sql = "SELECT " + columns + "\n"
                  + "FROM " + sourceType + "\n"
                  + "WHERE to_tsvector(" + column_name_doc + ") @@ to_tsquery(" + criteriaString + ")\n"
                  + "limit " + recordLimit;
            System.out.println(sql);
            ResultSet rs = query(c, sql);
            payload = extractPayload(rs, sourceType, type);
            rs.close();
//            rs.getStatement().close();
        }
        return payload;
    }

//    //hard coded version
//    public String convertToSourceType(String input) {
//        String result = null;
//        switch (input) {
//            case "person":
//                result = "persons";
//                break;
//            case "location":
//                result = "locations";
//                break;
//            case "case":
//                result = "cases";
//                break;
//            default:
//                result = input;
//        }
//        return result;
//    }
//
//    //hard coded version
//    public String convertToSourceColumn(String input) {
//        String result = null;
//        switch (input) {
//            case "id":
//                result = "person_id";
//                break;
//            case "first_name":
//                result = "given_name1";
//                break;
//            case "_all":
//                result = "given_name1";
//                break;
//            default:
//                result = input;
//        }
//        return result;
//    }
//
//    public String convertToGenericColumn(String input) {
//        String result = null;
//        switch (input) {
//            case "person_id":
//                result = "id";
//                break;
//            case "location_id":
//                result = "id";
//                break;
//            case "case_id":
//                result = "id";
//                break;
//            case "given_name1":
//                result = "first_name";
//                break;
//            default:
//                result = input;
//        }
//        return result;
//    }

    public Connection connect(String url, String user, String password) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection(url, user, password);
        System.out.println("url: "+url);
//            System.out.println("Opened database successfully");
        return c;
    }

    public ResultSet query(Connection c, String sql) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
//        stmt.closeOnCompletion();
        return rs;
    }

//    convertToGenericColumn(String sourceName, String sourceSchemaName, String sourceAttriName, String targetSchemaName)
    public JSONArray extractPayload(ResultSet rs, String sourceType, String type) throws SQLException {
        JSONArray payload_spec = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()) {
            JSONObject object_payload = new JSONObject();
            for (int i = 1; i <= columnsNumber; i++) {
                String columnName = rsmd.getColumnName(i);
                String columnValue = rs.getString(i);
                columnName = SchemaTranslater.convertToGenericColumn(sourceName, sourceType, columnName, type);
                object_payload.put(columnName, columnValue);
            }
            JSONObject match_entry = new JSONObject();
            match_entry.put("payload", object_payload);
            JSONObject info = new JSONObject();
            info.put("source", sourceName);
            info.put("type", sourceType);
            match_entry.put("info", info);
            payload_spec.add(match_entry);
        }
        return payload_spec;
    }

    public String convertToSourceQueryCriteria(LinkedHashMap<String, Object> criteria, String type, String sourceType) {
        RCO rco = (RCO) criteria.get("rco");
        String SQLScript = printSyntaxTree(rco, type, sourceType);
        SQLScript =
            SQLScript.replace("$eq", " = ").replace("$and", " and ").replace("$or", " or ").replace("$lte", " <= ")
                .replace("$lt", " < ").replace("$gte", " >= ").replace("$gt", " > ").replace("$in", " in ");
//        System.out.println(SQLScript);
        return SQLScript;
    }

//    convertToSourceColumn(String sourceSchemaName, String sourceAttriName, String targetName, String targetSchemaName)
    public String printSyntaxTree(RCO rco, String type, String sourceType) {
        String equation = "";
        if (rco.getValue().toString().startsWith("$")) {
            for (int i = 0; i < rco.linkedRCOs.size(); i++) {
                if (i == rco.linkedRCOs.size() - 1) {
                    equation += printSyntaxTree(rco.linkedRCOs.get(i), type, sourceType);
                } else {
                    equation += printSyntaxTree(rco.linkedRCOs.get(i), type, sourceType);
                    equation += rco.getValue().toString();
                }
            }
            equation = "(" + equation + ")";
        } else {
            String value = rco.getValue().toString();
            //when it is the attribute name
            if (!value.startsWith("\'")&&!value.startsWith("\"")){
                equation = SchemaTranslater.convertToSourceColumn(type, value, sourceName, sourceType);
            } else {
                //when it is the attribute value
                equation = value;
            }
        }
        return equation;
    }

    public LinkedHashMap<String, String> getColumns(String table, Connection c) throws SQLException {
        LinkedHashMap<String, String> columns = new LinkedHashMap<>();
        String sql = "select column_name, data_type \n"
                     + "from information_schema.columns \n"
                     + "where table_name = '" + table + "';";
        System.out.println(table);
        ResultSet rs = query(c, sql);
        while (rs.next()) {
            String column_name = rs.getString(1);
            String data_type = rs.getString(2);
            columns.put(column_name, data_type);
        }
        rs.close();
//        rs.getStatement().close();
//          for (int i=0;i<columns.size();i++) System.out.println(columns.get(i));
        System.out.println(columns.size());
        return columns;
    }

}

