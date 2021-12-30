package edu.unisa.ile.DataIngestion;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SqlTest {

	public static void main(String[] args) {
		test1();
	}

	public static void test1()
	{
		String dbURL = "jdbc:postgresql://130.220.210.130:5432/promis4";
		String dbUser = "unisa";
		String dbPwd = "unisa";
		String dbSchema = "public";
		try {
			Connection c = PostgresExtraction.connect(dbURL, dbUser, dbPwd);
			ResultSet rs = null;
			DatabaseMetaData meta = c.getMetaData();

			rs = meta.getPrimaryKeys(null, "public", "persons");
			while (rs.next()) {
			      String columnName = rs.getString("column_name");
			      System.out.println("getPrimaryKeys(): columnName=" + columnName);
			}

//			rs = meta.getPrimaryKeys(null, "public", "referrals");
//			int count = rs.getMetaData().getColumnCount();
//			while (rs.next()) {
//				for (int i = 1; i <= count; i++) {
//					String columnValue = rs.getString(i);
//					System.out.print(rs.getMetaData().getColumnName(i) + "： " + columnValue);
//					System.out.print(",  ");
//				}
//				System.out.println("");
//			}
			
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void test2()
	{
		String dbURL = "jdbc:postgresql://130.220.210.130:5432/lestore";
		String dbUser = "unisa";
		String dbPwd = "unisa";
		String dbSchema = "public";
		try {
			Connection c = PostgresExtraction.connect(dbURL, dbUser, dbPwd);
			ResultSet rs = null;

			rs = PostgresExtraction.query(c, "select * from company");
			if(DBUtilities.hasColumn(rs, "id")) {
				while (rs.next()) {
					System.out.println(rs.getString("id"));	
				}
			}
			else System.out.println("no such column");
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test3()
	{
		String dbURL = "jdbc:postgresql://130.220.210.130:5432/lestore";
		String dbUser = "unisa";
		String dbPwd = "unisa";
		String dbSchema = "public";
		try {
			Connection c = PostgresExtraction.connect(dbURL, dbUser, dbPwd);
			ResultSet rs = PostgresExtraction.query(c, "select id as id1, person as p1 from company, processedinstances");
			ResultSetMetaData rsmd = rs.getMetaData();
			System.out.println(rsmd.getTableName(1));
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void test4()
	{
		Map map = new LinkedHashMap();
		map.put("xx", "1");
		map.put("xx", "2");
		System.out.println(map.get("xx"));
	}

	

}
