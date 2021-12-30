package au.edu.unisa;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.*;
import java.util.*;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/*Load the whole table in MySQL into ES*/
public class LoadDataToES {
	
	public static void main(String args[]){
		
		String userName = new String("root");
		String pwd = new String("MAcri");
		String databaseName = new String("PROMIS");
		String tableName = new String("ENTITIES");
		ResultSet resultSet = null;
		String indexName = new String("promis");
		String typeName = new String("entities");
		String DbURL = new String("jdbc:mysql://localhost:3306/");		
		String serverURL = new String("localhost");
		IndexResponse response = null;
		int documentID = 1;
		
		try {
			
			String url = DbURL.concat(databaseName);
			
			Connection connection = DriverManager.getConnection(url, userName, pwd);
			
			System.out.println("Database connected!");
			
			Statement sta = connection.createStatement();
			
			String sql = new String("SELECT * FROM ").concat(tableName);
			
			/*catch the ResultSet Object*/
			resultSet = sta.executeQuery(sql);
			
			/*convert ResultSet Object to Map Object*/
			List<Map<String, Object>> mapObjectList = getListFromResultSet(resultSet);
			
			/*Create a new List to contain the json string for the whole*/
//			List<String> jsonObjectList = new ArrayList<String>();
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			
			/*Obtain the transport client of ElasticSearch*/
			Settings settings = Settings.settingsBuilder().put("elasticsearch","Annie Ghazikhanian").put("client.transport.sniff",true).build();
			TransportClient client = new TransportClient.Builder().settings(settings).build();
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));
			
			/*Iterate the whole List, and convert Map Object to Json Object, and then load Json Object to ES*/
			Iterator it = mapObjectList.iterator();
			while(it.hasNext()){
				
				Map<String, Object> rawData = (Map<String,Object>)it.next();
				
				/*convert Map Object to Json String*/				
				String rowJsonData = objectMapper.writeValueAsString(rawData);					
				
				response = client.prepareIndex(indexName, typeName, Integer.toString(documentID)).setSource(rowJsonData).get();
				
				documentID++;
				
				
			}
											
			/*close the ResultSet Object*/
			resultSet.close();
			
			sta.close();
			connection.close();		
			
			System.out.println("load is finished...");
			
		}catch(SQLException e){
			
			throw new IllegalStateException("Cannot connect to database!", e);
		}catch(JsonGenerationException e){
			
			e.printStackTrace();
		}catch(JsonMappingException e){
			
			e.printStackTrace();
		}catch(ElasticsearchException e){
			
			e.printStackTrace();
		}catch(IOException e){
			
			e.printStackTrace();
		}catch(Exception e){
			
			System.err.println("Exception" + e.getMessage());
		}
				
	}
	
	/*This function is used to convert ResultSet 'rs' to Array List*/
	public static List<Map<String,Object>> getListFromResultSet(ResultSet rs){
		
		List<Map<String,Object>> tableList = new ArrayList<Map<String,Object>>();
		
		
		if(rs!=null){
			
			try {
				ResultSetMetaData metaData = rs.getMetaData();
				
							
				while(rs.next()){
					
					Map<String, Object> columnMap = new HashMap<String, Object>();
					
					for(int columnIndex=1; columnIndex<=metaData.getColumnCount();columnIndex++){						
						
						
						if(rs.getString(metaData.getColumnName(columnIndex))!=null){
														
							columnMap.put(metaData.getColumnLabel(columnIndex), rs.getString(metaData.getColumnName(columnIndex)));
							
						}else{
							
							columnMap.put(metaData.getColumnLabel(columnIndex),"");							
							
						}
						
						
					}
					
					tableList.add(columnMap);
				}				
				
			}catch(SQLException e){
				
				e.printStackTrace();
			}
			
		}
		return tableList;
		
	}
	
	/*This function is responsible for converting a map to JSON*/
	public static String toJsonString(Map<String, Object> columnmap) {
	    try {
	        XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
	        builder.startObject();
	        builder.map(columnmap);
	        builder.endObject();
	        return builder.string();
	    } catch (IOException e) {
	        return "{ \"error\" : \"" + e.getMessage() + "\"}";
	    }
	}
	
	
	/*this function is just for testing*/
	public static String toJsonString(){
		
		try{
			
			XContentBuilder builder = XContentFactory.jsonBuilder().prettyPrint();
			builder.startObject().field("user","hunter").field("post","Macmillan").field("message","bcde");
			builder.endObject();
			return builder.string();
			
		}catch(IOException e){
			
			return "{ \"error\" : \"" + e.getMessage() + "\"}";
		}
		
	}
	
	
	
}
