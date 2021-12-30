package DataPreparation;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

//import me.xdrop.jrand.JRand;

public class AddSampleData {

//    public static void main(String[] args) throws Exception {
////        String targetUrl = "jdbc:postgresql://103.61.226.39:5432/VehicleManagementSystem";
//        String targetUrl = "jdbc:postgresql://103.61.226.39:5432/CaseManagementSystem";
//        String username = "unisa";
//        String password = "unisa";
//        Connection targetConnection = connect(targetUrl, username, password);
//        addColumn(targetConnection, "persons", "dob", "persons_locations_relationship_type");
////        initialColumn(targetConnection, "documents", "id", "documents_id", 10);
////        insertOneToMany(targetConnection, "cases", targetConnection, "cases_documents", "id", "cases_id","documents_id", "cases_documents_documents_id", 3);
////        addBatchFilestoDocumentTable("/Users/Shared/OneDrive/FederatedDataPlatformProject/testData/HDFSDataSamples", targetConnection);
//    }
//
//    public static void addBatchFilestoDocumentTable(String fileFolder, Connection c) throws Exception {
//        File folder = new File(fileFolder);
//        String table = "documents";
//        File[] listOfFiles = folder.listFiles();
//
//        String retrieveSql = "select * from " + table;
//        ResultSet rs = query(c, retrieveSql);
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int columnsNumber = rsmd.getColumnCount();
//        int count = 0;
//        while (rs.next()) {
//            String fileName = listOfFiles[count].getName();
//            String filePath = listOfFiles[count].getAbsolutePath();
//            String fileFormat = fileName.substring(fileName.lastIndexOf(".")+1);
//            count++;
//            String whereClause = "";
//            for (int i = 1; i <= columnsNumber; i++) {
//                if (i > 1) {
//                    whereClause += " and ";
//                }
//                Object columnValue = rs.getObject(i);
//                if (columnValue == null) {
//                    whereClause += rsmd.getColumnName(i) + " IS NULL";
//                } else {
//                    if (columnValue.getClass().equals(String.class)) {
//                        whereClause += rsmd.getColumnName(i) + "=" + "'" + columnValue + "'";
//                    } else {
//                        whereClause += rsmd.getColumnName(i) + "=" + columnValue;
//                    }
//
//                }
//            }
//            String updateSQL = "update " + table + " set title = '" + fileName + "', format = '"+ fileFormat + "', url = '"+ filePath +"' where " + whereClause;
//            System.out.println(updateSQL);
//            update(c, updateSQL);
//        }
//    }
//
//    public static Connection connect(String url, String user, String password) throws Exception {
//        Class.forName("org.postgresql.Driver");
//        Connection c = DriverManager.getConnection(url, user, password);
//        System.out.println("Opened database successfully");
//        return c;
//    }
//
//    public static ResultSet query(Connection c, String sql) throws SQLException {
//        Statement stmt = c.createStatement();
//        ResultSet rs = stmt.executeQuery(sql);
//        return rs;
//    }
//
//    public static void update(Connection c, String sql) throws SQLException {
//        Statement stmt = c.createStatement();
//        stmt.executeUpdate(sql);
//    }

//    public static void initialColumn(Connection c, String table, String column, String newDataType, int records) throws Exception{
//        for (int i = 0; i<records; i++){
//            Object newData = generateNewData(newDataType);
//            String newDataString = "null";
//            if (newData != null) {
//                if (newData.getClass().equals(String.class)) {
//                    newDataString = "'" + newData + "'";
//                }
//                if (newData.getClass().equals(BigDecimal.class)) {
//                    newDataString = "" + newData;
//                }
//                if (newData.getClass().equals(Date.class)) {
//                    newDataString = "'" + newData + "'";
//                }
//            }
//            String updateSQL = "insert into " + table + " (" + column + ") values (" + newDataString + " )";
//            System.out.println(updateSQL);
//            update(c, updateSQL);
//        }
//    }

//    public static void insertOneToMany(Connection sourceConnection, String sourceTable, Connection targetConnection, String targetTable, String sourceColumn, String oneColumn, String manyColumn, String newDataType, int records) throws Exception{
//        String retrieveSql = "select * from " + sourceTable;
//        ResultSet rs = query(sourceConnection, retrieveSql);
//
//        while (rs.next()) {
//            Object valueOneColumn = rs.getObject(sourceColumn);
//            String valueOneColumnString = null;
//            if (valueOneColumn!=null) {
//                if (valueOneColumn.getClass().equals(String.class)){
//                    valueOneColumnString = "'"+valueOneColumnString+"'";
//                } else {
//                    valueOneColumnString = valueOneColumn.toString();
//                }
//                System.out.print(valueOneColumn.toString()+"    ");
//                for(int i=0; i<records;i++){
//                    Object newData = generateNewData(newDataType);
//                    String newDataString = "null";
//                    if (newData != null) {
//                        if (newData.getClass().equals(String.class)) {
//                            newDataString = "'" + newData + "'";
//                        }
//                        if (newData.getClass().equals(BigDecimal.class)) {
//                            newDataString = "" + newData;
//                        }
//                    }
//                    System.out.print(newDataString+"    ");
//
//                    String updateSQL = "insert into " + targetTable + " (" + oneColumn +","+manyColumn+ ") values (" +valueOneColumnString+","+ newDataString + " )";
//                    System.out.println(updateSQL);
//                    update(targetConnection, updateSQL);
//
//                }
//                System.out.println();
//            }
//        }
//    }

//    public static void addColumn(Connection c, String table, String column, String newDataType) throws SQLException {
//        //retrieve old records from the table
//        String retrieveSql = "select * from " + table;
//        ResultSet rs = query(c, retrieveSql);
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int columnsNumber = rsmd.getColumnCount();
//        while (rs.next()) {
//            Object newData = generateNewData(newDataType);
//            String newDataString = "null";
//            if (newData != null) {
//                if (newData.getClass().equals(String.class)) {
//                    newDataString = "'" + newData + "'";
//                }
//                if (newData.getClass().equals(BigDecimal.class)) {
//                    newDataString = "" + newData;
//                }
//                if (newData.getClass().equals(Date.class)) {
//                    newDataString = "'" + newData + "'";
//                }
//            }
//            String whereClause = "";
//            for (int i = 1; i <= columnsNumber; i++) {
//                if (i > 1) {
//                    whereClause += " and ";
//                }
//                Object columnValue = rs.getObject(i);
//                if (columnValue == null) {
//                    whereClause += rsmd.getColumnName(i) + " IS NULL";
//                } else {
//                    if (columnValue.getClass().equals(String.class)){
//                        whereClause += rsmd.getColumnName(i) + "=" + "'"+columnValue+"'";
//                    } else {
//                        whereClause += rsmd.getColumnName(i) + "=" + columnValue;
//                    }
//
//                }
//            }
//            String updateSQL = "update " + table + " set " + column + "=" + newDataString + " where " + whereClause;
//            System.out.println(updateSQL);
//            update(c, updateSQL);
//        }
//    }
//
//    public static Object generateNewData(String newDataType) {
//        Object result=null;
//        if(newDataType.equals("persons_cases_relationship")){
//            String[] relationship = {"suspect","witness","investigator","victim"};
//            int[] distribution = {50,75,85,100};
//            int randomInt = ThreadLocalRandom.current().nextInt(100);
//            for (int i = 0;i<distribution.length;i++){
//                if (randomInt <= distribution[i]){
//                    result = relationship[i];
//                    break;
//                }
//            }
//        }
//        if(newDataType.equals("persons_contact_numbers_relationship")){
//            String[] relationship = {"office","home","mobile"};
//            int[] distribution = {33,66,100};
//            int randomInt = ThreadLocalRandom.current().nextInt(100);
//            for (int i = 0;i<distribution.length;i++){
//                if (randomInt <= distribution[i]){
//                    result = relationship[i];
//                    break;
//                }
//            }
//        }
//        if(newDataType.equals("persons_vehicles_relationship_type")){
//            String[] relationship = {"owns","pre-owns","drives"};
//            int[] distribution = {50,80,100};
//            int randomInt = ThreadLocalRandom.current().nextInt(100);
//            for (int i = 0;i<distribution.length;i++){
//                if (randomInt <= distribution[i]){
//                    result = relationship[i];
//                    break;
//                }
//            }
//        }
//        if(newDataType.equals("persons_vehicles_persons_id")){
//            BigDecimal x = new BigDecimal(9930000);
//            BigDecimal add = new BigDecimal(ThreadLocalRandom.current().nextInt(9999));
//            result = x.add(add);
//        }
//        if(newDataType.equals("persons_persons_relationship")){
//            String[] relationship = {"doctor of","wife of","hasband of","employee of","employer of","friend of","associate of"};
//            int[] distribution = {3,9,15,25,30,60,100};
//            int randomInt = ThreadLocalRandom.current().nextInt(100);
//            for (int i = 0;i<distribution.length;i++){
//                if (randomInt <= distribution[i]){
//                    result = relationship[i];
//                    break;
//                }
//            }
//        }
//        if(newDataType.equals("vehicles_plate_number")){
//            String prefix = JRand.string().alpha().range(3,3).casing("upper").gen();
//            String suffix = JRand.string().digits().range(3,3).gen();
//            result = prefix + suffix;
//        }
//        if(newDataType.equals("vehicles_make")){
//            String[] make = {"ford","mazda","jeep","toyota","mitsubishi","holden","honda"};
//            int randomInt = ThreadLocalRandom.current().nextInt(0,7);
//            return make[randomInt];
//        }
//        if(newDataType.equals("vehicles_body_type")){
//            String[] make = {"convertible","coupe","hatch","sedan","suv","ute","van","wagon"};
//            int randomInt = ThreadLocalRandom.current().nextInt(0,8);
//            return make[randomInt];
//        }
//        if(newDataType.equals("vehicles_year_manufacture")){
//            int randomInt = ThreadLocalRandom.current().nextInt(1990,2019);
//            return new BigDecimal(randomInt);
//        }
//        if(newDataType.equals("vehicles_colour")){
//            String[] make = {"red","yellow","green","black","blue","white","silver","gray"};
//            int randomInt = ThreadLocalRandom.current().nextInt(0,8);
//            return make[randomInt];
//        }
//        if(newDataType.equals("vehicles_vin")){
//            String vin = JRand.string().range(17,17).casing("upper").gen();
//            return vin;
//        }
//        if(newDataType.equals("documents_id")){
//            BigDecimal x = new BigDecimal(5850000);
//            BigDecimal add = new BigDecimal(ThreadLocalRandom.current().nextInt(9999));
//            result = x.add(add);
//        }
//        if(newDataType.equals("cases_documents_documents_id")){
//            int[] set = {5856323,5858225,5854022,5856840,5857820,5854070,5856823,5852551,5853947,5858719};
//            int randomInt = ThreadLocalRandom.current().nextInt(0,10);
//            return new BigDecimal(set[randomInt]);
//        }
//        if(newDataType.equals("cases_documents_relationship_type")){
//            String[] set = {"A","B","C"};
//            int randomInt = ThreadLocalRandom.current().nextInt(0,3);
//            return set[randomInt];
//        }
//        if(newDataType.equals("persons_locations_relationship_type")){
//            String[] set = {"lives_in","works_at","owns","stayed_at"};
//            int randomInt = ThreadLocalRandom.current().nextInt(0,4);
//            return set[randomInt];
//        }
//        return result;
//    }

}
