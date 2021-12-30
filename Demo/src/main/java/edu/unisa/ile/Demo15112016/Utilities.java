package edu.unisa.ile.Demo15112016;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

/*Providing utilities functions for loading data to ES*/
public class Utilities {

	/* This function is used to convert ResultSet 'rs' to Array List */
	public static List<Map<String, Object>> getListFromResultSet(ResultSet rs) {

		List<Map<String, Object>> tableList = new ArrayList<Map<String, Object>>();

		if (rs != null) {

			try {
				ResultSetMetaData metaData = rs.getMetaData();

				while (rs.next()) {

					Map<String, Object> columnMap = new HashMap<String, Object>();

					for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {

						if (rs.getString(metaData.getColumnName(columnIndex)) != null) {
							
							columnMap.put(metaData.getColumnLabel(columnIndex),
									stringAdaptor(rs.getString(metaData.getColumnName(columnIndex))));

						} else {

							// columnMap.put(metaData.getColumnLabel(columnIndex),"");

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
	
	public static String stringAdaptor(String s){

		
		List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>() {{
			add(new SimpleDateFormat("dd/MM/yy"));
			add(new SimpleDateFormat("dd/M/yy"));
			add(new SimpleDateFormat("d/M/yy"));
			add(new SimpleDateFormat("d/MM/yy"));
		}};
		
		SimpleDateFormat esFormat = new SimpleDateFormat("yyyy/MM/dd");
		
		Date date = null;
		if(s == null) {
			return null;
		}
		for (SimpleDateFormat format : dateFormats) {
			try {
				format.setLenient(false);
				date = format.parse(s);
			} catch (ParseException e) {
				//Shhh.. try other formats
			}
			if (date != null) {
				s = esFormat.format(date).toString();
				break;
			}
		}
		return s.replace("/", "-");
	}
	
	/* This function is responsible for converting a map to JSON */
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

}
