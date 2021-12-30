package DataPreparation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DataETL {
    public static void main(String[] args) throws Exception{
        String sourceUrl = "jdbc:postgresql://103.61.226.39:5432/CaseManagementSystem";
//        String sourceUrl = "jdbc:postgresql://103.61.226.39:5432/VehicleManagementSystem";
//        String targetUrl = "jdbc:postgresql://103.61.226.39:5432/CaseManagementSystem";
//        String targetUrl = "jdbc:sqlite:./GraalTest/VehicleManagementSystem.db";
        String targetUrl = "jdbc:sqlite:./GraalTest/CaseManagementSystem.db";

        String username = "unisa";
        String password = "unisa";
        Connection sourceConnection = connect(sourceUrl, username, password);
//        Connection targetConnection = connect(targetUrl, username, password);
        Connection targetConnection = DriverManager.getConnection(targetUrl);

        String fromTable = "persons_persons";
        String toTable = "persons_persons";
        String getTableSql = "select * from "+fromTable;
        int[] extractIndex = {1,2,3};
//        int[] extractIndex = {1,2,3,4,5,6,7};
        extractAndLoad(sourceConnection, getTableSql, extractIndex, targetConnection, toTable);
    }

    public static Connection connect(String url, String user, String password) throws Exception {
        Class.forName("org.postgresql.Driver");
        Connection c = DriverManager.getConnection(url, user, password);
        System.out.println("Opened database successfully");
        return c;
    }

    public static ResultSet query(Connection c, String sql) throws SQLException {
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        return rs;
    }


    public static void extractAndLoad(Connection sourceConnection, String getTableSql, int[] extractIndex, Connection targetConnection, String targetTable) throws Exception{

        //prepare insert table sql
        PreparedStatement preparedStatement = null;
        int targetColumnCount = extractIndex.length;
        String insertTableSQL = "INSERT INTO "+targetTable+" VALUES (";
        for (int i=0;i<targetColumnCount;i++){
            if (i>0) insertTableSQL += ",";
            insertTableSQL+="?";
        }
        insertTableSQL += ");";
        preparedStatement = targetConnection.prepareStatement(insertTableSQL);
        System.out.println(insertTableSQL);

        //extract values from source according to the extractIndex and insert into the preparedstatement one by one
        ResultSet rs = query(sourceConnection,getTableSql);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        while (rs.next()){
            for (int i=0; i<extractIndex.length; i++){
                int index = extractIndex[i];
                if (index <= columnsNumber){
                    Object cell = rs.getObject(index);
                    if (cell!=null) {
                        System.out.println(cell.toString());
                        if (cell.getClass().equals(String.class)){
                            preparedStatement.setString(i+1, (String) cell);
                        } else if (cell.getClass().equals(Integer.class)){
                            preparedStatement.setInt(i+1, (int) cell);
                        } else if (cell.getClass().equals(BigDecimal.class)){
                            preparedStatement.setBigDecimal(i+1, (BigDecimal) cell);
                        } else if (cell.getClass().equals(Date.class)){
//                            preparedStatement.setDate(i+1, (Date) cell);
                            preparedStatement.setString(i+1, cell.toString());
                        } else {System.out.println("not recognized: "+cell.getClass().toString());}
                    }
                    else{
                        //cell null
                        System.out.println("null");
                        preparedStatement.setObject(i+1, null);
                    }
                } else {
                    //index fault
                }
            }
            preparedStatement.executeUpdate();
        }
        rs.close();
        preparedStatement.close();
        sourceConnection.close();
    }
}
