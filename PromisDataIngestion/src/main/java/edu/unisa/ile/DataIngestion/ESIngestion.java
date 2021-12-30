package edu.unisa.ile.DataIngestion;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.databind.*;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ESIngestion {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		System.out.println("1/2/3".replace("/", "-"));
//	}

	@SuppressWarnings({ "resource", "unchecked" })
	public static void ingest(String serverURL, String clusterName, String index, String type, Map<String, Object> map)
			throws Exception {

		Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", true)
				.build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));

		ObjectMapper objectMapper = new ObjectMapper();
		String JSONObject = objectMapper.writeValueAsString(map);
		System.out.println(JSONObject);
		String documentId = "" + DBUtilities.createID(map);
		IndexResponse response = client.prepareIndex(index, type, documentId).setSource(JSONObject).get();

		client.close();
		System.out.println("Ingestion is finished...");
	}

	@SuppressWarnings({ "resource", "unchecked" })
	public static void batchIngest(String serverURL, String clusterName, String index, String type,
			ArrayList<Map<String, Object>> maps) throws Exception {
		
		/* Obtain the transport client of ElasticSearch */
		Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", true)
				.build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));
		ObjectMapper objectMapper = new ObjectMapper();
		
		for (Map<String, Object> map : maps) {
			// String documentId = "" + maps.indexOf(map);
			String documentId = "" + DBUtilities.createID(map);
			
			System.out.println(documentId);
			String JSONObject = objectMapper.writeValueAsString(map);
			System.out.println(JSONObject);
			IndexResponse response = client.prepareIndex(index, type, documentId).setSource(JSONObject).get();
		}
		
		client.close();
		
		System.out.println("Batch size: " + maps.size());
		System.out.println("Batch ingestion is finished...");
	}
	
	@SuppressWarnings({ "resource", "unchecked" })
	public static void ingestV2(String serverURL, String clusterName, String index, String type, Map<String, Object> map)
			throws Exception {

		Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", true)
				.build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));

		String documentId = (String) map.get("documentId");
		System.out.println(documentId);
		map.remove(documentId);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String JSONObject = objectMapper.writeValueAsString(map);
		System.out.println(JSONObject);
		IndexResponse response = client.prepareIndex(index, type, documentId).setSource(JSONObject).get();

		client.close();
		System.out.println("Ingestion is finished...");
	}

	@SuppressWarnings({ "resource", "unchecked" })
	public static void batchIngestV2(String serverURL, String clusterName, String index, String type,
			ArrayList<Map<String, Object>> maps) throws Exception {
		
		/* Obtain the transport client of ElasticSearch */
		Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", true)
				.build();
		TransportClient client = new PreBuiltTransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));
		ObjectMapper objectMapper = new ObjectMapper();
		
		for (Map<String, Object> map : maps) {
			String documentId = (String) map.get("documentId");
			System.out.println(documentId);
			map.remove("documentId");
			String JSONObject = objectMapper.writeValueAsString(map);
			System.out.println(JSONObject);
			try{
			IndexResponse response = client.prepareIndex(index, type, documentId).setSource(JSONObject).get();
			} catch(MapperParsingException mpe){
				System.out.println(mpe);
			}
		}
		
		client.close();
		
		System.out.println("Batch size: " + maps.size());
		System.out.println("Batch ingestion is finished...");
	}
	
}
