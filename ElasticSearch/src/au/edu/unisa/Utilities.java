package au.edu.unisa;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

/*Providing utilities functions for loading data to ES*/
public class Utilities {
	
	
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
	
	/*extract 'a' and 'b' from a string formated ‘(a,b)’*/
	public static String[] extractKeyElement(String str){
		
		int indexOfComma = 0;
		/*return the index of the comma in the string*/
		indexOfComma = str.indexOf(new String(","));
		
		String str1 = str.substring(1,indexOfComma);
		
		String str2 = str.substring(indexOfComma+1,str.length()-1);
		
		String[] array = new String[2];
		
		array[0] = str1;
		
		array[1] = str2;
		
		return array;
	}

}
