package edu.unisa.ILE.FSA.EnginePortal;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by wenhaoli on 26/04/2017.
 * This file is to store the functions for calling Elastic Search services
 * including query, update, insert, etc.
 */
public class CallServiceFunctions {

//    public static void main(String[] args) throws Exception {
////        try{
////            String payload = post("http://130.220.209.255:9200/entity_store/location/_search?pretty", "{\"query\": {\"match\": {\"_id\": \"promis3_locations_7354214\"}}}").toString();
////            System.out.println(payload);
////        } catch(Exception e){
////            e.printStackTrace();
////        }
//        JSONObject jsonObject = (JSONObject) new JSONParser().parse("{\"query\": {\"match\": {\"_id\": \"promis3_locations_7354214\"}}}");
//        System.out.println(jsonObject);
//    }

    public static JSONObject querySource(String source, ArrayList<String> types,
                                         LinkedHashMap<String, Object> criteria) {
        System.out.println("querySource triggered");
        JSONObject result = new JSONObject();
        Adapter adapter = SourceAdapterFactory.build(source);
        if (adapter != null) {
            result = adapter.send(types, criteria);
        }

        return result;
    }

    public static JSONObject consume(String URI, String json) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        JSONObject jsonObject = null;
        int statusCode;
        String reasonPhrase;
        try {
            HttpPost post = new HttpPost(URI);
            StringEntity postEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(postEntity);
            CloseableHttpResponse response = httpClient.execute(post);
            statusCode = response.getStatusLine().getStatusCode();
            reasonPhrase = response.getStatusLine().getReasonPhrase();
            try {
                if (statusCode == 200) { // need to return
                    HttpEntity entity = response.getEntity();
                    String entityString = EntityUtils.toString(entity);
                    JSONParser parser = new JSONParser();
                    jsonObject = (JSONObject) parser.parse(entityString);
                    jsonObject.put("request_status", reasonPhrase);
                    jsonObject.put("status_code", statusCode);
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("request_status", reasonPhrase);
                    jsonObject.put("status_code", statusCode);
                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
        return jsonObject;
    }
}
