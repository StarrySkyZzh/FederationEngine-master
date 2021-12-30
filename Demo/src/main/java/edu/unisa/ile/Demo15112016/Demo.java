package edu.unisa.ile.Demo15112016;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

public class Demo {

	public static void main(String[] args) {
		Demo d = new Demo();
		d.demoNov2016();
	}

//	public void demoJan2017() {
//		// TODO Auto-generated method stub
//		String dbURL = "jdbc:postgresql://130.220.210.130:5432/promis3";
//		String dbUser = "unisa";
//		String dbPwd = "unisa";
//		String dbSchema = "public";
//		String serverURL = "130.220.209.30";
//		String clusterName = "ILEESCL";
//		// String index = "from_promis_db";
//		String index = "from_promis_db_test";
//		// String type = "testtype";
//
//		try {
//			Connection c = PostgresExtraction.connect(dbURL, dbUser, dbPwd);
//			ArrayList<Map<String, Object>> personsMap = PostgresExtraction.getPersons(c, dbSchema);
//			ESIngestion.batchIngest5(serverURL, clusterName, index, "Person", personsMap);
//			c.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.err.println(e.getClass().getName() + ": " + e.getMessage());
//			System.exit(0);
//		}
//	}

	public void demoNov2016() {
		// TODO Auto-generated method stub
		String dbURL = "jdbc:postgresql://130.220.209.27:5432/promis3";
		String dbUser = "unisa";
		String dbPwd = "unisa";
		String dbSchema = "public";
		String serverURL = "130.220.212.114";
		String clusterName = "ILEESCL";
		// String index = "from_promis_db";
		String index = "from_promis_db_test";
		// String type = "testtype";

		try {
			Connection c = PostgresExtraction.connect(dbURL, dbUser, dbPwd);
			ArrayList<Map<String, Object>> personsMap = PostgresExtraction.getPersons(c, dbSchema);
			ESIngestion.batchIngest(serverURL, clusterName, index, "Person", personsMap);
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
}
