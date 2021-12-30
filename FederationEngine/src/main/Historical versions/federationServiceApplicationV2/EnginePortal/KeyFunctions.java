package edu.unisa.ILE.FSA.EnginePortal;

/**
 * Created by wenhaoli on 6/04/2017.
 */


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import edu.unisa.ILE.FSA.InternalDataStructure.ControlSpec;
import edu.unisa.ILE.FSA.InternalDataStructure.QuerySpec;
import edu.unisa.ILE.FSA.InternalDataStructure.RCO;
import edu.unisa.ILE.FSA.InternalDataStructure.UserAccessSpec;
import edu.unisa.ILE.FSA.Parser.QueryParser;


public class KeyFunctions {

    public static JSONObject query(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        //query logic conduct here
        JSONObject result = new JSONObject();

        switch (cs.getOperation()) {
            case "findEntities":
                result = findEntities(cs, qs, uas);
                break;
            case "findEntitiesByKeyword":
                result = findEntitiesByKeyword(cs, qs, uas);
                break;
            case "getBinaryContent":
                result = getBinaryContent(cs, qs, uas);
                break;
            case "getLinks":
                result = getLinks(cs, qs, uas);
                break;
            case "getAdjacentEntities":
                result = getAdjacentEntities(cs, qs, uas);
                break;
        }
        return result;
    }

    public static JSONObject findEntities(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        System.out.println("findEntities triggered");
        System.out.println("ControlSpec: " + cs);
        System.out.println("QuerySpec: " + qs);
        System.out.println("UserAccessSpec: " + uas);

        //first, parse query specifics
        //parse scope
        ArrayList<String> types = new ArrayList<String>();
        if (qs.getSCOPE_SPEC() != null && qs.getSCOPE_SPEC().size() > 0) {
            JSONArray scope_spec = qs.getSCOPE_SPEC();
            scope_spec.forEach(item -> {
                String type = (String) item;
                types.add(type);
                System.out.println("type: " + type);
            });
        }

        //parse output
        LinkedHashMap<String, String> projection_spec = null;
        if (qs.getOUTPUT_SPEC() != null && !qs.getOUTPUT_SPEC().isEmpty()) {
            projection_spec = (LinkedHashMap) qs.getOUTPUT_SPEC().get("project");
            projection_spec.forEach((k, v) -> {
                System.out.println("(" + k + "," + v + ")");
            });
        }

        //parse filter
        LinkedHashMap<String, Object> criteria = new LinkedHashMap<>();
        if (qs.getFILTER_SPEC() != null && !qs.getFILTER_SPEC().isEmpty()) {
            JSONObject filter_spec = qs.getFILTER_SPEC();
            QueryParser parser = new QueryParser();
            RCO rco = parser.generateSyntaxTree(filter_spec);

            criteria.put("rco", rco);
            criteria.put("original", filter_spec);
            criteria.put("operation", cs.getOperation());
            criteria.put("credentiallist", uas.getCredentialList());
            criteria.put("projection_spec", projection_spec);
        }

        //parse window
        if (qs.getWINDOW_SPEC() != null && !qs.getWINDOW_SPEC().isEmpty()) {

        }

        //second, parse the data source/s, if no such constraint in the request, query all data sources
        ArrayList<String> sources;
        if (cs.getSources() != null && cs.getSources().size() > 0) {
            sources = cs.getSources();
        } else {
            //query all sources
            sources = Application.getAllSources();
        }

        //for each source, query and get the response and merge into the final payload
        JSONObject result = new JSONObject();
        JSONArray payload = new JSONArray();
        for (String source : sources) {
            JSONObject response = CallServiceFunctions.querySource(source, types, criteria);
            System.out.println("response from " + source + ": " + response);
            if (response.get("fail") != null) {
                response.put("source", source);
                result = response;
                break;
            } else {
                JSONArray a = (JSONArray) response.get("success");
                payload = JSONFunctions.simpleMerge(payload, a);
                result.put("success", payload);
            }
        }

        return result;
    }

    public static JSONObject findEntitiesByKeyword(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        System.out.println("findEntitiesByKeyword triggered");
        return new JSONObject();
    }

    public static JSONObject getBinaryContent(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        System.out.println("getBinaryContent triggered");
        return new JSONObject();
    }

    public static JSONObject getLinks(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        System.out.println("getLinks triggered");
        return new JSONObject();
    }

    public static JSONObject getAdjacentEntities(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        System.out.println("getAdjacentEntities triggered");
        return new JSONObject();
    }


}
