package edu.unisa.ILE.FSA.EnginePortal;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import edu.unisa.ILE.FSA.SourceAdaptor.Adapter;
import edu.unisa.ILE.FSA.SourceAdaptor.SourceAdapterFactory;

/**
 * Created by wenhaoli on 26/04/2017. This file is to store the functions for calling Elastic Search services including
 * query, update, insert, etc.
 */
public class CallServiceFunctions {

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

    public static JSONObject post(String URI, String content) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        JSONObject jsonObject;
        int statusCode;
        String reasonPhrase;
        try {
            HttpPost post = new HttpPost(URI);
            StringEntity postEntity = new StringEntity(content, ContentType.APPLICATION_JSON);
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

    public static JSONObject get(String URI) throws Exception {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        JSONObject jsonObject;
        int statusCode;
        String reasonPhrase;
        try {
            HttpGet get = new HttpGet(URI);
            CloseableHttpResponse response = httpClient.execute(get);
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

    public static JSONObject readHDFS(String uri, String fileName) throws IOException {
        String fullPath = uri + "/" + fileName;
        URI fileURI = URI.create(fullPath.replace(" ", "%20"));
        System.out.println(fileURI.toString());
        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.get(fileURI, conf);

        JSONObject jsonObject;
        byte[] bytes;
        Path path = new Path(fileURI);
        if (!fileSystem.exists(path)) {
            jsonObject = new JSONObject();
            jsonObject.put("request_status", "File " + path.getName() + " does not exist");
            jsonObject.put("status_code", -1);
            return jsonObject;
        } else {
            int length = (int) fileSystem.getFileStatus(path).getLen();
            if (length > Integer.MAX_VALUE) {
                // File is too large
                jsonObject = new JSONObject();
                jsonObject.put("request_status", "File" + path.getName() + "is too large to transfer");
                jsonObject.put("status_code", -1);
                return jsonObject;
            }
            FSDataInputStream in = fileSystem.open(path);
            bytes = new byte[length];
            int offset = 0;
            int numRead;
            while (offset < bytes.length
                   && (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            if (offset < bytes.length) {
                // read can not be completed
                jsonObject = new JSONObject();
                jsonObject.put("request_status", "Could not completely read file " + path.getName());
                jsonObject.put("status_code", -1);
                return jsonObject;
            }
            in.close();
            fileSystem.close();
        }

        //parse format
        String[] name_structure = fileName.split("\\.");
        String format;
        if (name_structure.length > 1) {
            format = name_structure[1];
        } else {
            format = "unknown";
        }

        jsonObject = new JSONObject();
        jsonObject.put("request_status", "success");
        jsonObject.put("status_code", 200);
        jsonObject.put("file_name", fileName);
        jsonObject.put("format", format);
        jsonObject.put("_source", bytes);
        return jsonObject;
    }
}
