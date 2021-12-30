package federationServiceApplicationV1;

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

    public static JSONObject consume(String URI, String json) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        JSONObject jsonObject = null;
        int statusCode;
        try {
            HttpPost post = new HttpPost(URI);
            StringEntity postEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(postEntity);
            CloseableHttpResponse response = httpClient.execute(post);
            statusCode = response.getStatusLine().getStatusCode();
            try {
                if (statusCode == 200) { // need to return
                    HttpEntity entity = response.getEntity();
                    String entityString = EntityUtils.toString(entity);
                    JSONParser parser = new JSONParser();
                    jsonObject = (JSONObject) parser.parse(entityString);
                    jsonObject.put("request status:", "sucess");
                    jsonObject.put("status code:", statusCode);
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("request status:", "error");
                    jsonObject.put("status code:", statusCode);
                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
        return jsonObject;
    }

    public static JSONObject querySource(String source, ArrayList<String> types, ArrayList<String> criterias) {
        System.out.println("querySource triggered");
        JSONObject result = new JSONObject();
        if (source.equals("es")) {
            for (String criteria : criterias) {
                System.out.println("criteria: " + criteria);
                String[] equation = criteria.split(",");
                String keyStr = equation[0];
                String symbol = equation[1];
                String keyValue = equation[2];
                try {
                    //form Elastic Search expression and conduct query
                    String URL = "http://130.220.209.255:9200/entity_store/" + types.get(0) + "/_search?pretty";
                    JSONObject
                        jsonObject =
                        (JSONObject) new JSONParser()
                            .parse("{\"query\": {\"match\": {\"" + keyStr + "\": \"" + keyValue + "\"}}}");
                    result = consume(URL, jsonObject.toString());

                    System.out.println("result: " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
