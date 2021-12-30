package federationServiceApplicationV1;

/**
 * Created by wenhaoli on 6/04/2017.
 */


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class KeyFunctions {

    public static int checkCredential(UserAccessSpec uas, ControlSpec cs, QuerySpec qs) {
        //access control logic be conducted here
        int permission = 1;
        return permission;
    }

    public static JSONObject query(ControlSpec cs, QuerySpec qs) {
        //query logic conduct here
        JSONObject result = new JSONObject();

        switch (cs.getOperation()) {
            case "findEntities":
                result = findEntities(cs, qs);
                break;
            case "findEntitiesByKeyword":
                result = findEntitiesByKeyword(cs, qs);
                break;
            case "getBinaryContent":
                result = getBinaryContent(cs, qs);
                break;
            case "getLinks":
                result = getLinks(cs, qs);
                break;
            case "getAdjacentEntities":
                result = getAdjacentEntities(cs, qs);
                break;
        }
        return result;
    }

    public static JSONObject linking(ControlSpec cs, QuerySpec qs) {
        //query logic conduct here
        JSONObject result = new JSONObject();

//        switch (cs.getOperation()) {
//            case
//        }

        return result;
    }

    public static JSONObject metadata(ControlSpec cs, QuerySpec qs) {
        //query logic conduct here
        JSONObject result = new JSONObject();

//        switch (cs.getOperation()) {
//            case
//        }

        return result;
    }

    public static JSONObject ingestion(ControlSpec cs, QuerySpec qs) {
        //query logic conduct here
        JSONObject result = new JSONObject();

//        switch (cs.getOperation()) {
//            case
//        }

        return result;
    }

    public static JSONObject findEntities(ControlSpec cs, QuerySpec qs) {
        System.out.println("findEntities triggered");
        System.out.println("QuerySpec: " + qs);

        //first, parse query specifics
        //parse scope
        ArrayList<String> types = new ArrayList<String>();
        if (qs.getSCOPE_SPEC() != null) {
            JSONArray scope_spec = qs.getSCOPE_SPEC();
            scope_spec.forEach(item -> {
                String type = (String) item;
                types.add(type);
                System.out.println(type);
            });
        }

        //parse output
        if (qs.getOUTPUT_SPEC() != null) {
            LinkedHashMap<String, String> projection_spec = (LinkedHashMap) qs.getOUTPUT_SPEC().get("project");
            projection_spec.forEach((k, v) -> {
                System.out.println("(" + k + "," + v + ")");
            });
        }

        //parse filter
        ArrayList<String> criterias = new ArrayList<>();
        if (qs.getFILTER_SPEC() != null) {
            JSONObject filter_spec = qs.getFILTER_SPEC();
            for (Object key : filter_spec.keySet()) {
                String keyStr = (String) key;
                Object keyValue = filter_spec.get(keyStr);
                if (keyValue instanceof JSONObject) {

                } else if (keyValue instanceof JSONArray) {

                } else {
                    criterias.add(keyStr + "," + "eq" + "," + keyValue);
                    System.out.println(keyStr + "," + "eq" + "," + keyValue);
                }
            }
        }

        //parse window
        if (qs.getWINDOW_SPEC() != null) {

        }

        //second, parse the data source/s, if no such constraint in the request, query all data sources
        JSONArray sources;
        if (cs.getSources() != null) {
            sources = cs.getSources();
        } else {
            //query all sources
            sources = getAllSources();
        }

        //for each source, query and get the result together
        JSONObject result = new JSONObject();
        sources.forEach(item -> {
            String source = (String) item;
            JSONObject response = CallServiceFunctions.querySource(source, types, criterias);
            System.out.println("response from " + source + ": " + response);
            result.put(source, response);
        });
        return result;
    }

    public static JSONObject findEntitiesByKeyword(ControlSpec cs, QuerySpec qs) {
        System.out.println("findEntitiesByKeyword triggered");
        return new JSONObject();
    }

    public static JSONObject getBinaryContent(ControlSpec cs, QuerySpec qs) {
        System.out.println("getBinaryContent triggered");
        return new JSONObject();
    }

    public static JSONObject getLinks(ControlSpec cs, QuerySpec qs) {
        System.out.println("getLinks triggered");
        return new JSONObject();
    }

    public static JSONObject getAdjacentEntities(ControlSpec cs, QuerySpec qs) {
        System.out.println("getAdjacentEntities triggered");
        return new JSONObject();
    }

    public static JSONArray getAllSources() {
        JSONArray sources = new JSONArray();
        sources.add("promis");
        sources.add("es");
        return sources;
    }
}
