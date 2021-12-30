package edu.unisa.ile.Demo15112016;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;


public class ESIngestion {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("1/2/3".replace("/", "-"));
	}
//
//	public static void ingest5(String serverURL, String clusterName, String index, String type, String documentId,
//			Map<String, Object> map) throws Exception {
//
//		Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", true)
//				.build();
//		TransportClient client = new PreBuiltTransportClient(settings)
//				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		String JSONObject = objectMapper.writeValueAsString(map);
//		System.out.println(JSONObject);
//		IndexResponse response = client.prepareIndex(index, type, documentId).setSource(JSONObject).get();
//
//		client.close();
//		System.out.println("Ingestion is finished...");
//	}
//
//	public static void batchIngest5(String serverURL, String clusterName, String index, String type,
//			ArrayList<Map<String, Object>> maps) throws Exception {
//		/* Create a new List to contain the json string for the whole */
//		ObjectMapper objectMapper = new ObjectMapper();
//
//		/* Obtain the transport client of ElasticSearch */
//		Settings settings = Settings.builder().put("cluster.name", clusterName).put("client.transport.sniff", true)
//				.build();
//		TransportClient client = new PreBuiltTransportClient(settings)
//				.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));
//
//		for (Map<String, Object> map : maps) {
//			String documentId = "" + maps.indexOf(map);
//			String JSONObject = objectMapper.writeValueAsString(map);
//			System.out.println(JSONObject);
//			IndexResponse response = client.prepareIndex(index, type, documentId).setSource(JSONObject).get();
//		}
//
//		client.close();
//		System.out.println("Batch ingestion is finished...");
//	}

	public static void ingest(String serverURL, String clusterName, String index, String type, String documentId,
			Map<String, Object> map) throws Exception {

		/* Create a new List to contain the json string for the whole */
		ObjectMapper objectMapper = new ObjectMapper();

		/* Obtain the transport client of ElasticSearch */
		Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName)
				.put("client.transport.sniff", true).build();

		TransportClient client = new TransportClient.Builder().settings(settings).build();

		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));

		String JSONObject = objectMapper.writeValueAsString(map);
		System.out.println(JSONObject);
		IndexResponse response = client.prepareIndex(index, type, documentId).setSource(JSONObject).get();

		client.close();
		System.out.println("Ingestion is finished...");
	}

	public static void batchIngest(String serverURL, String clusterName, String index, String type,
			ArrayList<Map<String, Object>> maps) throws Exception {

		/* Create a new List to contain the json string for the whole */
		ObjectMapper objectMapper = new ObjectMapper();

		/* Obtain the transport client of ElasticSearch */
		Settings settings = Settings.settingsBuilder().put("cluster.name", clusterName)
				.put("client.transport.sniff", true).build();

		TransportClient client = new TransportClient.Builder().settings(settings).build();

		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(serverURL), 9300));

		for (Map<String, Object> map : maps) {
			String documentId = "" + maps.indexOf(map);
			String JSONObject = objectMapper.writeValueAsString(map);
			System.out.println(JSONObject);
			IndexResponse response = client.prepareIndex(index, type, documentId).setSource(JSONObject).get();
		}

		client.close();
		System.out.println("Batch ingestion is finished...");
	}
}
