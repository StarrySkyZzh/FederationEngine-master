package serviceOrchestration.Server;

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

public class CallServiceFunctions {
    public static JSONObject post(String URI, String content) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        JSONObject jsonObject;
        try {
            HttpPost post = new HttpPost(URI);
            StringEntity postEntity = new StringEntity(content, ContentType.APPLICATION_JSON);
            post.setEntity(postEntity);
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity entity = response.getEntity();
            String entityString = EntityUtils.toString(entity);
            JSONParser parser = new JSONParser();
            jsonObject = (JSONObject) parser.parse(entityString);
        } finally {
            httpClient.close();
        }
        return jsonObject;
    }
}
