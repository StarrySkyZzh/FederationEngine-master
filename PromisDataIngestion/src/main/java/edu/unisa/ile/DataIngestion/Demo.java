package edu.unisa.ile.DataIngestion;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;

public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Demo d = new Demo();
		d.demoJan2017();
	}

	public void demoJan2017() {
		// general attributes
		String serverURL = "130.220.209.30";
		String clusterName = "ILEESCL";
		
//		 db pipeline parameters
		 String dbURL = "jdbc:postgresql://130.220.210.130:5432/promis3";
		 String dbUser = "unisa";
		 String dbPwd = "unisa";
		 String dbSchema = "public";
		 String index = "entity_store";
		 
		 String schemaMappingFilePath = "/Users/Shared/OneDrive/FederatedDataPlatformProject/testData/case.entity";
		 String[] tags = new String[] {"promis3_cases"};
		 
		 String type = DBUtilities.getFileName(schemaMappingFilePath);
		 System.out.println(type);
		
		 //First demo, ingest a comprehensive person entity with everything
//		 DBToESDemoPerson(dbURL, dbUser, dbPwd, dbSchema, serverURL,
//		 clusterName, index);
		 
		 //Second demo, ingest a generic entity from the persons table of Promis3 using schema mapping file 
//		 DBToESEntity(dbURL, dbUser, dbPwd, dbSchema, serverURL, clusterName,
//		 index, schemaMappingFilePath, type);

		 //Thrid demo, ingest generic entities from DB with a permanent documentID
		 DBToESEntityV2(dbURL, dbUser, dbPwd, dbSchema, serverURL, clusterName,
				 index, schemaMappingFilePath, type, tags);
		 
//		// HDFS pipelines
//		String namenodeIP = "130.220.210.127";
//		String namenodePort = "8020";
//		String hdfsSourcePath = "/user/ile/document/";
//		String index = "document_store";
//		String type = "document";
//		HDFSToESDocument(namenodeIP, namenodePort, hdfsSourcePath, serverURL,
//				clusterName, index, type);
	}

	public void DBToESDemoPerson(String dbURL, String dbUser, String dbPwd, String dbSchema, String serverURL,
			String clusterName, String index) {
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
	
	public void DBToESEntity(String dbURL, String dbUser, String dbPwd, String dbSchema, String serverURL,
			String clusterName, String index, String schemaMappingFilePath, String type) {
		try {
			Connection c = PostgresExtraction.connect(dbURL, dbUser, dbPwd);
			Map<String, String> schemaMapping = DBUtilities
					.getSchemaMapping(new FileInputStream(schemaMappingFilePath));
			
			ArrayList<Map<String, Object>> entityMap = PostgresExtraction.getEntityFromDB(c, dbSchema, schemaMapping);
			System.out.println(entityMap.size());
			ESIngestion.batchIngestV2(serverURL, clusterName, index, type, entityMap);
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}
	
	public void DBToESEntityV2(String dbURL, String dbUser, String dbPwd, String dbSchema, String serverURL,
			String clusterName, String index, String schemaMappingFilePath, String type, String[] tags) {
		//tags in schemaMapping for, say, primary key, are for creating the permanent documentId of entities 
		//future version can generate the tag list "tags" automatically according to the schema mapping file
		
		try {
			Connection c = PostgresExtraction.connect(dbURL, dbUser, dbPwd);
			Map<String, String> schemaMapping = DBUtilities
					.getSchemaMappingV2(new FileInputStream(schemaMappingFilePath));
			ArrayList<Map<String, Object>> entityMap = PostgresExtraction.getEntityFromDBV2(c, dbSchema, schemaMapping, tags);
			
			System.out.println(entityMap.size());
			ESIngestion.batchIngestV2(serverURL, clusterName, index, type, entityMap);
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
	}

	public void HDFSToESDocument(String namenodeIP, String namenodePort, String sourcePath, String serverURL,
			String clusterName, String index, String type) {
		String namenodePath = "hdfs://" + namenodeIP + ":" + namenodePort;
		Configuration conf = new Configuration();
		conf.set("fs.default.name", namenodePath);
		conf.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl", org.apache.hadoop.fs.LocalFileSystem.class.getName());
		try {
			ArrayList<Map<String, Object>> entityMap = FileExtraction.getEntityFromFile(sourcePath, conf);
			System.out.println(entityMap.size());
			ESIngestion.batchIngestV2(serverURL, clusterName, index, type, entityMap);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}
}
