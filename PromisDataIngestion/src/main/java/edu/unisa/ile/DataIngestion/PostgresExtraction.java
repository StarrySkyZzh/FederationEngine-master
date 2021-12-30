package edu.unisa.ile.DataIngestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PostgresExtraction {

	// public static void main(String[] args) {
	//// TODO Auto-generated method stub
	// try {
	// Connection c =
	// connect("jdbc:postgresql://130.220.209.27:5432/promis3", "unisa",
	// "unisa");
	//
	// c.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	public static Connection connect(String url, String user, String password) throws Exception {
		Connection c = null;
		Class.forName("org.postgresql.Driver");
		c = DriverManager.getConnection(url, user, password);
		System.out.println("Opened database successfully");
		return c;
	}

	public static void update(Connection c, String sql) throws Exception {
		Statement stmt = c.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
		System.out.println("update successful!");
	}

	public static ResultSet query(Connection c, String sql) throws Exception {
		ResultSet rs = null;
		Statement stmt = c.createStatement();
		rs = stmt.executeQuery(sql);
		// stmt.close();
		return rs;
	}

	public static ArrayList<Map<String, Object>> getPersons(Connection c, String schema) {
		ArrayList<Map<String, Object>> list = null;
		try {
			String sql = "select distinct * " + "from persons "
					+ "left join persons_cases on (persons.person_id = persons_cases.persons_id) "
					+ "left join cases on (persons_cases.case_id = cases.case_id) "
					+ "left join persons_locations on (persons.person_id = persons_locations.persons_id) "
					+ "left join locations on (persons_locations.location_id = locations.location_id) "
					+ "left join persons_contact_numbers on (persons.person_id = persons_contact_numbers.persons_id) "
					+ "left join contact_numbers on (persons_contact_numbers.contact_id = contact_numbers.contact_id);";
			ResultSet rs = query(c, sql);
			list = (ArrayList<Map<String, Object>>) DBUtilities.getListFromResultSet(rs);
			System.out.println("person entities: " + list.size());
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(list.size());
		return list;
	}

	public static ArrayList<Map<String, Object>> getEntityFromDB(Connection c, String schema,
			Map<String, String> schemaMapping) {
		ArrayList<Map<String, Object>> list = null;
		try {
			String sql = entityExtractionSQLGenerator(schema, schemaMapping);
			System.out.println(sql);
			ResultSet rs = query(c, sql);
			list = (ArrayList<Map<String, Object>>) DBUtilities.getListFromResultSet(rs);
			System.out.println("SQL query result set size: " + list.size());
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(list.size());
		return list;
	}

	public static ArrayList<Map<String, Object>> getEntityFromDBV2(Connection c, String schema,
			Map<String, String> schemaMapping, String[] tags) {
		ArrayList<Map<String, Object>> list = null;
		try {
			String sql = entityExtractionSQLGeneratorV2(schema, schemaMapping);
			System.out.println(sql);
			ResultSet rs = query(c, sql);
			list = (ArrayList<Map<String, Object>>) DBUtilities.getListFromResultSet(rs);

			// create documentId for each entity and put into each map
			for (Map<String, Object> map : list) {
				String documentId = null;

				for (int i = 0; i < tags.length; i++) {
					if (map.get(tags[i]) != null) {
						if (documentId == null) {
//							documentId = tags[i] + "_" + map.get(tags[i]);
                            documentId = ""+map.get(tags[i]);
						} else {
//							documentId += "_" + tags[i] + "_" + map.get(tags[i]);
                            documentId += map.get(tags[i]);
						}
						map.remove(tags[i]);
					}
				}
				map.put("documentId", documentId);
				System.out.println("documentId: "+documentId);
			}

			System.out.println("SQL query result set size: " + list.size());
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String entityExtractionSQLGenerator(String schema, Map<String, String> schemaMapping) {
		String sql = "select ";
		Set set = schemaMapping.entrySet();
		Iterator setIterator = set.iterator();
		while (setIterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) setIterator.next();
			sql += mentry.getKey().toString().trim() + " as " + mentry.getValue().toString().trim();
			if (setIterator.hasNext()) {
				sql += ", ";
			} else {
				sql += " ";
			}
		}
		sql += "from ";
		ArrayList<String> tables = DBUtilities.getDistinctTables(schemaMapping);
		Iterator tableIterator = tables.iterator();
		while (tableIterator.hasNext()) {
			String tableName = (String) tableIterator.next();
			sql += tableName;
			if (tableIterator.hasNext()) {
				sql += ", ";
			} else {
				sql += " ";
			}
		}
		return sql;
	}

	public static String entityExtractionSQLGeneratorV2(String schema, Map<String, String> schemaMapping) {
		String sql = "select ";
		Set set = schemaMapping.entrySet();
		Iterator setIterator = set.iterator();
		while (setIterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) setIterator.next();

			// compare to version 1, this version support that one map key have
			// more than one values, divided by ","
			String originColumnName = mentry.getKey().toString().trim();
			String[] castedColumnNames = mentry.getValue().toString().trim().split(",");
			for (int i = 0; i < castedColumnNames.length; i++) {
				if (i != 0)
					sql += ", ";
				sql += originColumnName + " as " + castedColumnNames[i];
			}

			if (setIterator.hasNext()) {
				sql += ", ";
			} else {
				sql += " ";
			}
		}
		sql += "from ";
		ArrayList<String> tables = DBUtilities.getDistinctTables(schemaMapping);
		Iterator tableIterator = tables.iterator();
		while (tableIterator.hasNext()) {
			String tableName = (String) tableIterator.next();
			sql += tableName;
			if (tableIterator.hasNext()) {
				sql += ", ";
			} else {
				sql += " ";
			}
		}
		return sql;
	}

	public static String entityExtractionSQLGeneratorV3(Connection c, String schema,
			Map<String, String> schemaMapping) throws Exception {

		// select part
		String sql = "select ";
		Set set = schemaMapping.entrySet();
		Iterator setIterator = set.iterator();
		while (setIterator.hasNext()) {
			Map.Entry mentry = (Map.Entry) setIterator.next();

			// compare to version 2, this version added support SQL scripts that
			// extract entities involving joining multiple PROMIS tables using
			// object_links table
			String originColumnName = mentry.getKey().toString().trim();
			String[] castedColumnNames = mentry.getValue().toString().trim().split(",");
			for (int i = 0; i < castedColumnNames.length; i++) {
				if (i != 0)
					sql += ", ";
				sql += originColumnName + " as " + castedColumnNames[i];
			}

			if (setIterator.hasNext()) {
				sql += ", ";
			} else {
				sql += " ";
			}
		}

		// from part
		sql += "from ";
		ArrayList<String> tables = DBUtilities.getDistinctTables(schemaMapping);
		sql += tables.get(0) + " ";

		// left join part
		if (tables.size() > 1) {
			// rename object_link table depending on the involved times
			String[] olNames = new String[tables.size()];
			for (int i = 1; i<tables.size(); i++) {
				olNames[i] = "ol"+i;
			}
			//add the left join tables scripts
			for (int i = 1; i < tables.size(); i++) {
				// left join object_link table
				sql += "left join object_links as " + olNames[i] + " on ("
						+ tables.get(0) + "." + DBUtilities.getPKNames(c, schema, tables.get(0)).get(0) + " = " + olNames[i] +".from_obj_id"
						+ ") ";
//				left join locations on (ol1.to_obj_id = locations.id) 
				// left join the consecutive table
				sql += "left join " + tables.get(i) + " on ("
						+ olNames[i] +".to_obj_id = "+ tables.get(i) + "." + DBUtilities.getPKNames(c, schema, tables.get(i)).get(0)
						+ ")";
			}
		}
		return sql;
	}
}
