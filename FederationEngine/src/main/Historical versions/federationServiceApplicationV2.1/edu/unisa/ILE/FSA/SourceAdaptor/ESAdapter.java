package edu.unisa.ILE.FSA.SourceAdaptor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import edu.unisa.ILE.FSA.CDMMappingRepository.SchemaTranslater;
import edu.unisa.ILE.FSA.EnginePortal.FDEApplication;
import edu.unisa.ILE.FSA.EnginePortal.CallServiceFunctions;
import edu.unisa.ILE.FSA.InternalDataStructure.ESBoolQueryTemplate;
import edu.unisa.ILE.FSA.InternalDataStructure.RCO;

/**
 * Created by wenhaoli on 22/06/2017.
 */
public class ESAdapter extends Adapter {

    private static String sourceName = "es";

    public JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {
//        System.out.println("criteria: "+criteria);
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
                        result = findEntitiesByKeyword(types, criteria, username, password);
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
        System.out.println("findEntities of " + sourceName + " triggered");
        //compose request body
        JSONObject sourceQueryCriteria = convertToSourceQueryCriteria((RCO) criteria.get("rco"));
        //compose URL
        String URL = "http://" + username + ":" + password + "@" + FDEApplication.ESURL;
        if (!types.isEmpty()) {
            URL += "/";
            for (int i = 0; i < types.size(); i++) {
                String sourceType = convertToSourceType(types.get(i));
                if (i < types.size() - 1) {
                    URL += sourceType + ",";
                } else {
                    URL += sourceType;
                }
            }
        }

        //check fetching limit of records
        JSONObject window_spec = (JSONObject) criteria.get("window_spec");
        if (window_spec != null) {
            String limitString = (String) window_spec.get("limit");
            if (limitString != null) {
                int size = Integer.parseInt(limitString);
                URL += "/_search?size=" + size;
            } else {
                URL += "/_search?size=50";
            }
        } else {
            URL += "/_search?size=50";
        }
        System.out.println(URL);

        JSONObject requestBody = (JSONObject) new JSONParser().parse("{\"query\":" + sourceQueryCriteria + "}");
        System.out.println(requestBody);
        JSONObject responseBody = CallServiceFunctions.post(URL, requestBody.toString());
//        System.out.println(responseBody);
        JSONObject payload = new JSONObject();
        if ((int) responseBody.get("status_code") == 200) {
            payload.put("success", extractPayload(responseBody, criteria));
        } else {
            payload.put("fail", responseBody);
        }
        return payload;
    }

    public JSONObject findEntitiesByKeyword(ArrayList<String> types, LinkedHashMap<String, Object> criteria,
                                            String username, String password) throws Exception {
        System.out.println("findEntitiesByKeyword of " + sourceName + " triggered");
        //compose request body
        JSONArray keywords = (JSONArray) criteria.get("keywords");
        String mode = (String) criteria.get("mode");
        String operator;
        if (mode != null) {
            if (mode.equals("all")) {
                operator = "and";
            } else {
                operator = "or";
            }
        } else {
            operator = "or";
        }
        ESBoolQueryTemplate requestBody = new ESBoolQueryTemplate();
        for (int i = 0; i < keywords.size(); i++) {
            String keyword = (String) keywords.get(i);
            JSONObject matchCriteria = requestBody.createMatch("_all", keyword, "or", true);
            if (operator.equals("or")) {
                requestBody.inputShould(matchCriteria);
            } else {
                requestBody.inputMust(matchCriteria);
            }
        }
        System.out.println(requestBody.getRequestBody());

        //compose URL
        String URL = "http://" + username + ":" + password + "@" + FDEApplication.ESURL;
        if (!types.isEmpty()) {
            URL += "/";
            for (int i = 0; i < types.size(); i++) {
                String sourceType = convertToSourceType(types.get(i));
                if (i < types.size() - 1) {
                    URL += sourceType + ",";
                } else {
                    URL += sourceType;
                }
            }
        }

        //check fetching limit of records
        JSONObject window_spec = (JSONObject) criteria.get("window_spec");
        if (window_spec != null) {
            String limitString = (String) window_spec.get("limit");
            if (limitString != null) {
                int size = Integer.parseInt(limitString);
                URL += "/_search?size=" + size;
            } else {
                URL += "/_search?size=50";
            }
        } else {
            URL += "/_search?size=50";
        }
        System.out.println(URL);

        //send request and parse response
        JSONObject responseBody = CallServiceFunctions.post(URL, requestBody.getRequestBody().toString());
//        System.out.println(responseBody);
        JSONObject payload = new JSONObject();
        if ((int) responseBody.get("status_code") == 200) {
            payload.put("success", extractPayload(responseBody, criteria));
        } else {
            payload.put("fail", responseBody);
        }
        return payload;
    }

    public JSONObject convertToSourceQueryCriteria(RCO rco) {
        System.out.println("convertToSourceQueryCriteria of " + sourceName + " triggered");
        ESBoolQueryTemplate sourceQuery = new ESBoolQueryTemplate();
        String value = rco.getValue().toString();
        if (value.equals("$or") || value.equals("$and")) {
            for (int i = 0; i < rco.linkedRCOs.size(); i++) {
                RCO child = rco.linkedRCOs.get(i);
                JSONObject childQueryCriteria = convertToSourceQueryCriteria(child);
                if (value.equals("$or")) {
                    sourceQuery.inputShould(childQueryCriteria);
                }
                if (value.equals("$and")) {
                    sourceQuery.inputMust(childQueryCriteria);
                }
            }
            System.out.println(sourceQuery.getBool());

            return sourceQuery.getBool();
        } else {
            RCO keyChild = rco.linkedRCOs.get(0);
            RCO valueChild = rco.linkedRCOs.get(1);
            String keyChildValue = convertToSourceColumn(keyChild.getValue().toString());
            if (value.equals("$lt") || value.equals("$lte") || value.equals("$gt") || value.equals("$gte")) {
                String valueChildValue = valueChild.getValue().toString().replace("'", "");
                JSONObject result = sourceQuery.createRange(keyChildValue, valueChildValue, value.replace("$", ""));
                System.out.println("" + value + " " + result);
                return result;
            }
            if (value.equals("$in")) {
                String valueChildValue = valueChild.getValue().toString();
                String[] valueArray = valueChildValue.replace("[", "").replace("]", "").replace("\"", "").split(",");
                String queryValue = "";
                for (String s : valueArray) {
                    queryValue += s + " ";
                }
                JSONObject result = sourceQuery.createMatch(keyChildValue, queryValue, "or", true);
                System.out.println("" + value + " " + result);
                return result;
            }
            if (value.equals("$eq")) {
                JSONObject
                    result =
                    sourceQuery
                        .createMatch(keyChildValue, valueChild.getValue().toString().replace("'", ""), null, false);
                System.out.println("" + value + " " + result);
                return result;
            }
            return null;
        }
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
            String type = (String) hit.get("_type");
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
                    Object value = object_payload.get(key);
                    converted_payload.put(genericColumn, value);
                }
                match_entry.put("payload", converted_payload);
            }
            JSONObject info = new JSONObject();
            info.put("source", sourceName);
            info.put("type", type);
            match_entry.put("info", info);
            payload_spec.add(match_entry);
        }
        return payload_spec;
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
                result = "given_name1";
                break;
            case "height":
                result = "height";
                break;
            default:
                result = input;
        }
        return result;
    }

    public String convertToGenericColumn(String input) {
        String result;
        switch (input) {
            case "given_name1":
                result = "first_name";
                break;
            default:
                result = input;
        }
        return result;
    }
}
