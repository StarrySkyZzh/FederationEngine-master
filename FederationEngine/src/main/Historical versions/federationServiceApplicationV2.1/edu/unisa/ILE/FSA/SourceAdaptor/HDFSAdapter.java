package edu.unisa.ILE.FSA.SourceAdaptor;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;


import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import edu.unisa.ILE.FSA.EnginePortal.FDEApplication;
import edu.unisa.ILE.FSA.EnginePortal.CallServiceFunctions;

public class HDFSAdapter extends Adapter {

    private static String sourceName = "hdfs";

    public JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {
        String operation = criteria.get("operation").toString();
        JSONObject result = new JSONObject();
        JSONObject credential = getCredential(criteria, sourceName);
        if (credential != null) {
//            String username = (String) credential.get("username");
//            String password = (String) credential.get("password");
            try {
                switch (operation) {
                    case "getBinaryContent":
                        result = getBinaryContent(criteria, credential);
                        break;
                    case "getTextContent":
                        result = getTextContent(criteria, credential);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    // for retriving original files such as images, scanned documents, etc.
    public JSONObject getBinaryContent(LinkedHashMap<String, Object> criteria, JSONObject credential) throws Exception {
        System.out.println("getBinaryContent of " + sourceName + " triggered");
        //set authorization process -- to be implemented
        System.out.println(sourceName + " credential: " + credential);

        //send request and parse response
        String fileName = convertToSourceQueryCriteria(criteria);

        JSONObject responseBody = CallServiceFunctions.readHDFS(FDEApplication.HDFS, fileName);
        System.out.println(responseBody);
        JSONObject payload = new JSONObject();
        if ((int) responseBody.get("status_code") == 200) {
            payload.put("success", extractBinaryPayload(responseBody));
        } else {
            payload.put("fail", responseBody);
        }
        return payload;
    }

    //for retriving file text contents such as pdfs, word documents, ppts, etc.
    public JSONObject getTextContent(LinkedHashMap<String, Object> criteria, JSONObject credential) throws Exception {
        System.out.println("getTextContent of " + sourceName + " triggered");
        //set authorization process -- to be implemented
        System.out.println(sourceName + " credential: " + credential);

        //send request and parse response
        String fileName = convertToSourceQueryCriteria(criteria);

        JSONObject responseBody = CallServiceFunctions.readHDFS(FDEApplication.HDFS, fileName);
        System.out.println(responseBody);
        JSONObject payload = new JSONObject();
        if ((int) responseBody.get("status_code") == 200) {
            payload.put("success", extractTextPayload(responseBody));
        } else {
            payload.put("fail", responseBody);
        }
        return payload;
    }

    public String convertToSourceQueryCriteria(LinkedHashMap<String, Object> criteria) {
        String sourceQueryCriteria = null;
        JSONObject filter_spec = (JSONObject) criteria.get("filter_spec");
        Set<String> keys = filter_spec.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = (String) filter_spec.get(key);
//            String sourceKey = convertToSourceColumn(key);
            sourceQueryCriteria = value;
        }
        return sourceQueryCriteria;
    }

    public JSONArray extractBinaryPayload(JSONObject responseBody) {
        JSONArray payload_spec = new JSONArray();
        JSONObject match_entry = new JSONObject();

        JSONObject payload = new JSONObject();
        byte[] bytes = (byte[]) responseBody.get("_source");
        byte[] encodedBytes = Base64.encodeBase64(bytes);
        payload.put("id", responseBody.get("file_name"));
        payload.put("format", responseBody.get("format"));
        payload.put("content", encodedBytes);
        match_entry.put("payload", payload);

        JSONObject info = new JSONObject();
        info.put("source", sourceName);
        info.put("type", "document binary");
        match_entry.put("info", info);

        payload_spec.add(match_entry);
        return payload_spec;
    }

    public JSONArray extractTextPayload(JSONObject responseBody) {
        JSONArray payload_spec = new JSONArray();
        byte[] bytes = (byte[]) responseBody.get("_source");
        String text = "";
        InputStream input = null;
        try {
            input = new ByteArrayInputStream(bytes);
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext parseContext = new ParseContext();
            parser.parse(input, handler, metadata, parseContext);
            text = handler.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        JSONObject match_entry = new JSONObject();

        JSONObject payload = new JSONObject();
        payload.put("id", responseBody.get("file_name"));
        payload.put("format", responseBody.get("format"));
        payload.put("content", text);
        match_entry.put("payload", payload);

        JSONObject info = new JSONObject();
        info.put("source", sourceName);
        info.put("type", "document content");
        match_entry.put("info", info);

        payload_spec.add(match_entry);
        return payload_spec;
    }

    public String convertToSourceType(String input) {
        return input;
    }

    public String convertToSourceColumn(String input) {
        return input;
    }

    public String convertToGenericColumn(String input) {
        return input;
    }
}
