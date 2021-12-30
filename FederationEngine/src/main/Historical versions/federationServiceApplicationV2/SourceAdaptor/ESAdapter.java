package edu.unisa.ILE.FSA.SourceAdaptor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import edu.unisa.ILE.FSA.EnginePortal.FDEApplication;
import edu.unisa.ILE.FSA.EnginePortal.CallServiceFunctions;

/**
 * Created by wenhaoli on 22/06/2017.
 */
public class ESAdapter implements Adapter {

    public JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {
        System.out.println("criteria: " + criteria);
        String operation = criteria.get("operation").toString();
        JSONObject result = new JSONObject();

        HashMap<String, String> credential = getCredential(criteria);
        String username = credential.get("username");
        String password = credential.get("password");

        if (username != null && password != null) {
            try {
                switch (operation) {
                    case "findEntities":
                        result = findEntities(types, criteria, username, password);
                        break;
                    case "findEntitiesByKeyword":
                        break;
                    case "getBinaryContent":
                        break;
                    case "getLinks":
                        break;
                    case "getAdjacentEntities":
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public JSONObject findEntities(ArrayList<String> types, LinkedHashMap<String, Object> criteria, String username,
                                   String password) throws Exception {
        //form Elastic Search expression and conduct query
        String ESCriteria = toESScript(criteria);
        String URL = "http://" + username + ":" + password + "@" + Application.ESURL;

        if (!types.isEmpty()) {
            URL += "/";
            for (int i = 0; i < types.size(); i++) {
                if (i < types.size() - 1) {
                    URL += types.get(i) + ",";
                } else {
                    URL += types.get(i);
                }
            }
        }

        URL += "/_search?pretty";
        System.out.println(URL);
        JSONObject requestBody = (JSONObject) new JSONParser().parse("{\"query\": {\"match\": " + ESCriteria + "}}");
        JSONObject responseBody = CallServiceFunctions.consume(URL, requestBody.toString());
        System.out.println(responseBody);
        JSONObject payload = new JSONObject();
        if ((int) responseBody.get("status_code") == 200) {
            payload.put("success", extractPayload(responseBody, criteria));
        } else {
            JSONArray error = new JSONArray();
            error.add(responseBody);
            payload.put("fail", error);
        }
        return payload;
    }

    // need to be revised in the next version
    public String toESScript(LinkedHashMap<String, Object> criteria) {
        return criteria.get("original").toString();
    }

    public JSONArray extractPayload(JSONObject responseBody, LinkedHashMap<String, Object> criteria) {
        JSONArray payload_spec = new JSONArray();

        LinkedHashMap<String, String> projection_spec = (LinkedHashMap<String, String>) criteria.get("projection_spec");

        ArrayList<String> projection = new ArrayList<>();
        if (projection_spec != null) {
            Set<String> keys = projection_spec.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = projection_spec.get(key);
                if (value.toLowerCase().equals("true")) {
                    projection.add(key);
                }
            }
        }

        JSONArray hits = (JSONArray) ((JSONObject) responseBody.get("hits")).get("hits");
        for (int i = 0; i < hits.size(); i++) {
            JSONObject hit = (JSONObject) hits.get(i);
            JSONObject object_payload = (JSONObject) hit.get("_source");

            JSONObject match_entry = new JSONObject();
            if (!projection.isEmpty()) {
                JSONObject filtered_payload = new JSONObject();
                for (String key : projection) {
                    if (object_payload.get(key) != null) {
                        filtered_payload.put(key, object_payload.get(key));
                    }
                }
                match_entry.put("payload", filtered_payload);
            } else {
                match_entry.put("payload", object_payload);
            }

            match_entry.put("info", "es");
            payload_spec.add(match_entry);
        }
        return payload_spec;
    }

    public HashMap<String, String> getCredential(LinkedHashMap<String, Object> criteria) {
        HashMap<String, String> result = new HashMap();
        JSONArray credentialList = (JSONArray) criteria.get("credentiallist");
        String sourceName = "es";
        for (int i = 0; i < credentialList.size(); i++) {
            LinkedHashMap credential = (LinkedHashMap) credentialList.get(i);
            if (credential.containsKey(sourceName)) {
                result.put("username", (String) ((LinkedHashMap) credential.get(sourceName)).get("username"));
                result.put("password", (String) ((LinkedHashMap) credential.get(sourceName)).get("password"));
                break;
            }
        }
        return result;
    }

    //hard coded version
    public String convertToSourceType(String input) {
        String result = null;
        switch (input) {
            case "person":
                result = "persons";
                break;
            default:
                result = input;
        }
        return result;
    }

    //hard coded version
    public String convertToSourceColumn(String input) {
        String result = null;
        switch (input) {
            case "id":
                result = "person_id";
                break;
            case "first_name":
                result = "given_name1";
                break;
            case "_all":
                result = "given_name1";
                break;
            default:
                result = input;
        }
        return result;
    }

    public String convertToGenericColumn(String input) {
        String result = null;
        switch (input) {
            case "person_id":
                result = "id";
                break;
            case "given_name1":
                result = "first_name";
                break;
            default:
                result = input;
        }
        return result;
    }
}
