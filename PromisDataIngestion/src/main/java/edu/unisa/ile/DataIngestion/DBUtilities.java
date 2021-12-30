package edu.unisa.ile.DataIngestion;

import com.opencsv.CSVReader;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*Providing utilities functions for loading data to ES*/
public class DBUtilities {

	public static void main(String[] args) throws Exception {
		String dbURL = "jdbc:postgresql://130.220.210.130:5432/promis4";
		 String dbUser = "unisa";
		 String dbPwd = "unisa";
		 String dbSchema = "public";
		
		Map<String, String> schemaMapping = getSchemaMappingV2(new FileInputStream("PromisDataIngestion/person_location_case.entity"));
		Connection c = PostgresExtraction.connect(dbURL, dbUser, dbPwd);
		String sql = PostgresExtraction.entityExtractionSQLGeneratorV3(c, dbSchema, schemaMapping);
		System.out.println(sql);
	}

	/* This function is used to convert ResultSet 'rs' to Array List */
	public static List<Map<String, Object>> getListFromResultSet(ResultSet rs) {

		List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();
		if (rs != null) {
			try {
				while (rs.next()) {
					
					ResultSetMetaData metaData = rs.getMetaData();
					Map<String, Object> columnMap = new LinkedHashMap<String, Object>();
					for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
						if (rs.getString(metaData.getColumnName(columnIndex)) != null) {
							columnMap.put(metaData.getColumnLabel(columnIndex),
									stringAdaptor(rs.getString(metaData.getColumnName(columnIndex))));
						} else {
							 columnMap.put(metaData.getColumnLabel(columnIndex), null);
						}
					}
					tableList.add(columnMap);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return tableList;
	}
	
	public static ArrayList<String> getPKNames(Connection c, String schema, String tableName) throws Exception{
		ArrayList<String> PKNames = new ArrayList<String>();
		ResultSet rsmd = null;
	    DatabaseMetaData meta = c.getMetaData();
	    rsmd = meta.getPrimaryKeys(null, schema, tableName);
	    while (rsmd.next()) {
	            String PKName = rsmd.getString("column_name");
	            PKNames.add(PKName);
	        }
		return PKNames;
	}

	public static String stringAdaptor(String s) {
		List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {
			{
				add(new SimpleDateFormat("dd/MM/yy"));
				add(new SimpleDateFormat("dd/M/yy"));
				add(new SimpleDateFormat("d/M/yy"));
				add(new SimpleDateFormat("d/MM/yy"));
			}
		};

		SimpleDateFormat esFormat = new SimpleDateFormat("yyyy/MM/dd");

		Date date = null;
		if (s == null) {
			return null;
		}
		for (SimpleDateFormat format : dateFormats) {
			try {
				format.setLenient(false);
				date = format.parse(s);
			} catch (ParseException e) {
				// Shhh.. try other formats
			}
			if (date != null) {
				s = esFormat.format(date).toString();
				break;
			}
		}
		return s.replace("/", "-");
	}
	
	public static Map<String, String> getSchemaMapping(FileInputStream is) throws Exception {
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		Map<String, String> mappingList = new LinkedHashMap<String, String>();
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine.length == 2) {
				mappingList.put(nextLine[0], nextLine[1]);
			}
		}
		System.out.println("Schema mappings obtained: " + mappingList.size());
		// getDistinctTables(mappingList);
		reader.close();
		return mappingList;
	}
	//v2 support the third attribute that appears in a line
	public static Map<String, String> getSchemaMappingV2(FileInputStream is) throws Exception {
		CSVReader reader = new CSVReader(new InputStreamReader(is));
		Map<String, String> mappingList = new LinkedHashMap<String, String>();
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			if (nextLine.length == 2) {
				mappingList.put(nextLine[0], nextLine[1]);
			}
			if (nextLine.length == 3) {
				mappingList.put(nextLine[0], nextLine[1] + "," + nextLine[2]);
			}
		}
		System.out.println("Schema mappings obtained: " + mappingList.size());
//		 getDistinctTables(mappingList);
		reader.close();
		return mappingList;
	}

	public static ArrayList<String> getDistinctTables(Map<String, String> schemaMapping) {
		ArrayList<String> tables = new ArrayList<String>();

		Set set = schemaMapping.entrySet();
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) iterator.next();
			// System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
			// System.out.println(mentry.getValue());
			String key = mentry.getKey().toString();
			String tableName = key.split("\\.")[0];
			if (!tables.contains(tableName)) {
				tables.add(tableName);
				System.out.println("tableName: " + tableName);
			}
			System.out.println("tables: " + tables.size());
		}
		return tables;
	}

	public static String getFileName(String filePath) {
		String[] array = filePath.split("/");
		String fileNameExtended = array[array.length - 1];
		String fileName = fileNameExtended.split("\\.")[0];
		return fileName;
	}
	
	public static int createID(Map<String, Object> map){
		int id = map.hashCode();
		return id;
	}
	
	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
	    ResultSetMetaData rsmd = rs.getMetaData();
	    int columns = rsmd.getColumnCount();
	    for (int x = 1; x <= columns; x++) {
	        if (columnName.equals(rsmd.getColumnName(x))) {
	            return true;
	        }
	    }
	    return false;
	}
}
