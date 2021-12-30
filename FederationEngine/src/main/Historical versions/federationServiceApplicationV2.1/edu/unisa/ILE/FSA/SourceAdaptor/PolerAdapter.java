package edu.unisa.ILE.FSA.SourceAdaptor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import edu.unisa.ILE.FSA.EnginePortal.FDEApplication;
import edu.unisa.ILE.FSA.EnginePortal.CallServiceFunctions;


/**
 * Created by wenhaoli on 28/7/17.
 */
public class PolerAdapter extends Adapter {

    private static String sourceName = "poler";

    public JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {
        String operation = criteria.get("operation").toString();
        JSONObject result = new JSONObject();
        JSONObject credential = getCredential(criteria, sourceName);
        if (credential != null) {
            String username = (String) credential.get("username");
            String password = (String) credential.get("password");
            try {
                switch (operation) {
                    case "findEntities":
                        result = findEntities(types, criteria, username, password);
                        break;
                    case "findEntitiesByKeyword":
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
        System.out.println("findEntities of " + sourceName + " triggered");
        //compose request body
        String sourceCriteria = convertToSourceQueryCriteria(criteria);
        System.out.println("sourceCriteria: " + sourceCriteria);
        //compose URL
//        Application.PolerAPI: http://ile-poler.d2dcrc.net:8090/api/v3.1.0/personIdentity
//        GET /api/v3.1.0/personIdentity/search/findByFamilyName
//        GET /api/v3.1.0/personIdentity/search/findByPersonalName
        String URL = "http://" + username + ":" + password + "@" + FDEApplication.PolerAPI;
        URL += sourceCriteria;
        System.out.println(URL);
        JSONObject responseBody = CallServiceFunctions.get(URL);
        JSONObject payload = new JSONObject();
        System.out.println(responseBody);
        if ((int) responseBody.get("status_code") == 200) {
            payload.put("success", extractPayload(responseBody, criteria));
        } else {
            payload.put("fail", responseBody);
        }
        return payload;
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
        JSONArray hits = (JSONArray) ((JSONObject) responseBody.get("_embedded")).get("personIdentity");
        for (int i = 0; i < hits.size(); i++) {
            JSONObject hit = (JSONObject) hits.get(i);
            JSONObject object_payload = hit;
            JSONObject match_entry = new JSONObject();
            if (!projection.isEmpty()) {
                JSONObject filtered_payload = new JSONObject();
                for (String key : projection) {
                    String sourceColumn = convertToSourceColumn(key);
                    if (object_payload.get(sourceColumn) != null) {
                        filtered_payload.put(key, object_payload.get(sourceColumn));
                    }
                }
                match_entry.put("payload", filtered_payload);
            } else {
                JSONObject converted_payload = new JSONObject();
                Set<String> keys = object_payload.keySet();
                Iterator<String> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    String genericColumn = convertToGenericColumn(key);
                    Object value;
                    if (object_payload.get(key) != null) {
                        value = object_payload.get(key);
                    } else {
                        value = null;
                    }
                    converted_payload.put(genericColumn, value);
                }
                match_entry.put("payload", converted_payload);
            }

            JSONObject info = new JSONObject();
            info.put("source", sourceName);
            info.put("type", "person");
            match_entry.put("info", info);
            payload_spec.add(match_entry);
        }
        return payload_spec;
    }

    // need to be revised in the next version
    public String convertToSourceQueryCriteria(LinkedHashMap<String, Object> criteria) {
        //        URL += "/findByFamilyName?FamilyName=peter";
        //        "filter_spec": {"_all":"john"}
        String sourceQueryCriteria = null;
        JSONObject filter_spec = (JSONObject) criteria.get("filter_spec");
        Set<String> keys = filter_spec.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = (String) filter_spec.get(key);
            String sourceKey = convertToSourceColumn(key);
            if (sourceKey.toLowerCase().equals("familyname")) {
                sourceQueryCriteria = "/search/findByFamilyName?FamilyName=" + value;
            } else if (sourceKey.toLowerCase().equals("personalname")) {
                sourceQueryCriteria = "/search/findByPersonalName?PersonalName=" + value;
            } else {
                sourceQueryCriteria = "/search/findByPersonalName?PersonalName=" + value;
            }
        }
        return sourceQueryCriteria;
    }

    //hard coded version
    public String convertToSourceType(String input) {
        String result;
        switch (input) {
            default:
                result = input;
        }
        return result;
    }

    //hard coded version
    public String convertToSourceColumn(String input) {
        String result;
        switch (input) {
            case "first_name":
                result = "PersonalName";
                break;
            case "given_name":
                result = "PersonalName";
                break;
            case "last_name":
                result = "FamilyName";
                break;
            case "surname":
                result = "FamilyName";
                break;
            case "_all":
                result = "PersonalName";
                break;
            default:
                result = input;
        }
        return result;
    }

    public String convertToGenericColumn(String input) {
        String result;
        switch (input) {
            case "PersonalName":
                result = "first_name";
                break;
            case "FamilyName":
                result = "last_name";
                break;
            default:
                result = input;
        }
        return result;
    }
}
