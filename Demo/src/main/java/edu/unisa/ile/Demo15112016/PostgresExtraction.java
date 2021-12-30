package edu.unisa.ile.Demo15112016;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

public class PostgresExtraction {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//			Connection c = connect("jdbc:postgresql://130.220.209.27:5432/promis3", "unisa", "unisa");
//			getOrgs(c, "public");
//			getCases(c, "public");
//			c.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
	}

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
			String sql = "select distinct * "
					+ "from persons "
					+ "left join persons_cases on (persons.person_id = persons_cases.persons_id) "
					+ "left join cases on (persons_cases.case_id = cases.case_id) "
					+ "left join persons_locations on (persons.person_id = persons_locations.persons_id) "
					+ "left join locations on (persons_locations.location_id = locations.location_id) "
					+ "left join persons_contact_numbers on (persons.person_id = persons_contact_numbers.persons_id) "
					+ "left join contact_numbers on (persons_contact_numbers.contact_id = contact_numbers.contact_id);";
// Promis2 SQL script:
//			String sql = "select * "
//					+ "from (persons as p "
//					+ "left join (select * from object_links where from_obj_type_code='per' and to_obj_type_code='cse') as ol on (p.id = ol.from_obj_id)) "
//					+ "left join cases as c on (c.case_id = ol.to_obj_id) "
//					+ "left join (select * from object_links where from_obj_type_code='per' and to_obj_type_code='loc') as ol2 on (p.id = ol2.from_obj_id) "
//					+ "left join locations as l on (l.id = ol2.to_obj_id);";
			ResultSet rs = query(c, sql);
			list = (ArrayList<Map<String, Object>>) Utilities.getListFromResultSet(rs);
			System.out.println("organisation table size: " + list.size());
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println(list.size());
		return list;
	}

	public static ArrayList<Map<String, Object>> getOrgs(Connection c, String schema) {
		ArrayList<Map<String, Object>> list = null;
		try {
			String sql = "select * from " + schema + ".organisations";
			ResultSet rs = query(c, sql);
			list = (ArrayList<Map<String, Object>>) Utilities.getListFromResultSet(rs);
			System.out.println("organisation table size: " + list.size());
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println(list.size());
		return list;
	}

	public static ArrayList<Map<String, Object>> getCases(Connection c, String schema) {
		ArrayList<Map<String, Object>> list = null;
		try {
			String sql = "select * from " + schema + ".cases";
			ResultSet rs = query(c, sql);
			list = (ArrayList<Map<String, Object>>) Utilities.getListFromResultSet(rs);
			System.out.println("cases table size: " + list.size());
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println(list.size());
		return list;
	}

//	public static ArrayList<Organisation> getOrgs2(Connection c, String schema) {
//		ArrayList<Organisation> list = new ArrayList<Organisation>();
//		String sql = "select * from " + schema + ".organisations";
//		try {
//			ResultSet rs = query(c, sql);
//			while (rs.next()) {
//				float code = rs.getFloat("code");
//				String org_type = rs.getString("org_type");
//				String title = rs.getString("title");
//				String client = rs.getString("client");
//				System.out.println(code);
//				System.out.println(org_type);
//				System.out.println(title);
//				System.out.println(client);
//				list.add(new Organisation(code, org_type, title, client));
//			}
//			rs.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//		// System.out.println(list.size());
//		return list;
//	}

//	public static ArrayList<Case> getCases2(Connection c, String schema) {
//
//		ArrayList<Case> list = new ArrayList<Case>();
//		String sql = "select * from " + schema + ".cases";
//		try {
//			ResultSet rs = query(c, sql);
//			while (rs.next()) {
//				float case_id = rs.getFloat("case_id");
//				String case_title = rs.getString("case_title");
//				Date date_approved = rs.getDate("date_approved");
//				String case_officer = rs.getString("case_officer");
//				float team_code = rs.getFloat("team_code");
//				System.out.println(case_id);
//				System.out.println(case_title);
//				System.out.println(date_approved);
//				System.out.println(case_officer);
//				System.out.println(team_code);
//				list.add(new Case(case_id, case_title, date_approved, case_officer, team_code));
//			}
//			rs.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//		System.out.println(list.size());
//		return list;
//	}
}
