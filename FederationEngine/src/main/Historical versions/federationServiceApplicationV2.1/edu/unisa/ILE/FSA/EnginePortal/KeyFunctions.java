package edu.unisa.ILE.FSA.EnginePortal;

/**
 * Created by wenhaoli on 6/04/2017.
 */


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import edu.unisa.ILE.FSA.InternalDataStructure.ContentResult;
import edu.unisa.ILE.FSA.InternalDataStructure.ControlSpec;
import edu.unisa.ILE.FSA.InternalDataStructure.FindAdjacentEntitiesResult;
import edu.unisa.ILE.FSA.InternalDataStructure.FindEntitiesResult;
import edu.unisa.ILE.FSA.InternalDataStructure.QuerySpec;
import edu.unisa.ILE.FSA.InternalDataStructure.RCO;
import edu.unisa.ILE.FSA.InternalDataStructure.Result;
import edu.unisa.ILE.FSA.InternalDataStructure.UserAccessSpec;
import edu.unisa.ILE.FSA.Parser.QueryParser;


public class KeyFunctions {

    public static Result query(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        System.out.println("ControlSpec: " + cs);
        System.out.println("QuerySpec: " + qs);
        System.out.println("UserAccessSpec: " + uas);

        //query logic conduct here
        Result result = new Result();

        //trigger function accordingly
        switch (cs.getOperation()) {
            //generic services available for all sources
            case "findEntities":
                result = findEntities(cs, qs, uas);
                break;
            case "findEntitiesByKeyword":
                result = findEntitiesByKeyword(cs, qs, uas);
                break;
            //services only available for file storage systems
            case "getBinaryContent":
                result = getBinaryContent(cs, qs, uas);
                break;
            case "getTextContent":
                result = getTextContent(cs, qs, uas);
                break;
            //services only available for entity linking storage
            case "getLinks":
                result = getLinks(cs, qs, uas);
                break;
            case "getAdjacentEntities":
                result = getAdjacentEntities(cs, qs, uas);
                break;
        }
        return result;
    }

    public static FindEntitiesResult findEntities(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        //first, parse query specifics
        //parse scope
        ArrayList<String> types = new ArrayList<>();
        if (qs.getSCOPE_SPEC() != null && qs.getSCOPE_SPEC().size() > 0) {
            JSONArray scope_spec = qs.getSCOPE_SPEC();
            scope_spec.forEach(item -> {
                String type = (String) item;
                types.add(type);
                System.out.println("type: " + type);
            });
        }

        //parse projection
        LinkedHashMap<String, String> projection_spec = null;
        if (qs.getOUTPUT_SPEC() != null && !qs.getOUTPUT_SPEC().isEmpty()) {
            projection_spec = (LinkedHashMap) qs.getOUTPUT_SPEC().get("project");
        }

        //parse filter
        LinkedHashMap<String, Object> criteria = new LinkedHashMap<>();
        if (qs.getFILTER_SPEC() != null && !qs.getFILTER_SPEC().isEmpty()) {
            JSONParser jsonParser = new JSONParser();
            JSONObject filter_spec = new JSONObject();
            try {
                filter_spec = (JSONObject) jsonParser.parse(qs.getFILTER_SPEC().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("filter_spec: " + filter_spec);
            QueryParser queryParser = new QueryParser();
            RCO rco = queryParser.generateSyntaxTree(filter_spec);
            criteria.put("rco", rco);
            criteria.put("filter_spec", filter_spec);
            criteria.put("operation", cs.getOperation());
            criteria.put("credentiallist", uas.getCredentialList());
            criteria.put("projection_spec", projection_spec);
        }

        //parse window, leave for future extension
        if (qs.getWINDOW_SPEC() != null && !qs.getWINDOW_SPEC().isEmpty()) {
            JSONObject window_spec = qs.getWINDOW_SPEC();
            criteria.put("window_spec", window_spec);
        }

        //second, parse the data source/s
        ArrayList<String> sources;
        if (cs.getSources() != null && cs.getSources().size() > 0) {
            sources = cs.getSources();
        } else {
            //if no such constraint in the request, query all data sources
            sources = FDEApplication.getAllQuerySources();
        }

        //third, initialize result object
        FindEntitiesResult result = new FindEntitiesResult();
        result.addOperation(cs.getOperation());
        //for each source, query and get the response, and then composite the final result
        for (String source : sources) {
            long startTime = System.currentTimeMillis();
            JSONObject response = CallServiceFunctions.querySource(source, types, criteria);
            long latency = System.currentTimeMillis() - startTime;
            if (response.get("fail") != null) {
                result.addStatus(source, response);
            } else {
                JSONArray a = (JSONArray) response.get("success");
                result.addStatus(source, "success");
                if (a != null) {
                    for (Object obj : a) {
                        result.insertPayload(obj);
                    }
                    result.addHits(source, a.size());
                }
            }
            result.addPerformance(source, latency);
        }
        return result;
    }

    public static Result findEntitiesByKeyword(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {

        //first, parse query specifics
        //parse scope
        ArrayList<String> types = new ArrayList<>();
        if (qs.getSCOPE_SPEC() != null && qs.getSCOPE_SPEC().size() > 0) {
            JSONArray scope_spec = qs.getSCOPE_SPEC();
            scope_spec.forEach(item -> {
                String type = (String) item;
                types.add(type);
                System.out.println("type: " + type);
            });
        }
        //parse projection
        LinkedHashMap<String, String> projection_spec = null;
        if (qs.getOUTPUT_SPEC() != null && !qs.getOUTPUT_SPEC().isEmpty()) {
            projection_spec = (LinkedHashMap) qs.getOUTPUT_SPEC().get("project");
        }
        //parse filter
//        FILTER_SPEC ::={
//            keywords: list of <string>,
//            mode: ‘all’|’any’
//        }
        LinkedHashMap<String, Object> criteria = new LinkedHashMap<>();
        if (qs.getFILTER_SPEC() != null && !qs.getFILTER_SPEC().isEmpty()) {
            JSONParser jsonParser = new JSONParser();
            JSONObject filter_spec = new JSONObject();
            try {
                filter_spec = (JSONObject) jsonParser.parse(qs.getFILTER_SPEC().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONArray keywords = (JSONArray) filter_spec.get("keywords");
            String mode = (String) filter_spec.get("mode");

            criteria.put("keywords", keywords);
            criteria.put("mode", mode);
            criteria.put("filter_spec", filter_spec);
            criteria.put("operation", cs.getOperation());
            criteria.put("credentiallist", uas.getCredentialList());
            criteria.put("projection_spec", projection_spec);
        }

        //parse window, leave for future extension
        if (qs.getWINDOW_SPEC() != null && !qs.getWINDOW_SPEC().isEmpty()) {
            JSONObject window_spec = qs.getWINDOW_SPEC();
            criteria.put("window_spec", window_spec);
        }

        //second, parse the data source/s
        ArrayList<String> sources;
        if (cs.getSources() != null && cs.getSources().size() > 0) {
            sources = cs.getSources();
        } else {
            //if no such constraint in the request, query all data sources
            sources = FDEApplication.getAllQuerySources();
        }
        //third, initialize result object
        FindEntitiesResult result = new FindEntitiesResult();
        result.addOperation(cs.getOperation());
        //for each source, query and get the response, and then composite the final result
        for (String source : sources) {
            long startTime = System.currentTimeMillis();
            JSONObject response = CallServiceFunctions.querySource(source, types, criteria);
            long latency = System.currentTimeMillis() - startTime;
            if (response.get("fail") != null) {
                result.addStatus(source, response);
            } else {
                System.out.println("response " + response);
                JSONArray a = (JSONArray) response.get("success");
                result.addStatus(source, "success");
                for (Object obj : a) {
                    result.insertPayload(obj);
                }
                result.addHits(source, a.size());
            }
            result.addPerformance(source, latency);
        }
        return result;
    }

    public static Result getBinaryContent(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        //first, parse query specifics
        LinkedHashMap<String, Object> criteria = new LinkedHashMap<>();
        if (qs.getFILTER_SPEC() != null && !qs.getFILTER_SPEC().isEmpty()) {
            JSONParser jsonParser = new JSONParser();
            JSONObject filter_spec = new JSONObject();
            try {
                filter_spec = (JSONObject) jsonParser.parse(qs.getFILTER_SPEC().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            criteria.put("filter_spec", filter_spec);
            criteria.put("operation", cs.getOperation());
            criteria.put("credentiallist", uas.getCredentialList());
        }

        //second, parse the data source/s
        ArrayList<String> sources;
        if (cs.getSources() != null && cs.getSources().size() > 0) {
            sources = cs.getSources();
        } else {
            //if no such constraint in the request, query all data sources
            sources = FDEApplication.getAllStorageSources();
        }

        //third, initialize result object
        ContentResult result = new ContentResult();
        result.addOperation(cs.getOperation());
        //for each source, query and get the response, and then composite the final result
        for (String source : sources) {
            long startTime = System.currentTimeMillis();
            JSONObject response = CallServiceFunctions.querySource(source, null, criteria);
            long latency = System.currentTimeMillis() - startTime;
            if (response.get("fail") != null) {
                result.addStatus(source, response);
            } else {
                JSONArray a = (JSONArray) response.get("success");
                result.addStatus(source, "success");
                if (a != null) {
                    for (Object obj : a) {
                        result.insertPayload(obj);
                    }
                    result.addHits(source, a.size());
                }
            }
            result.addPerformance(source, latency);
        }
        return result;
    }

    public static Result getTextContent(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        //first, parse query specifics
        LinkedHashMap<String, Object> criteria = new LinkedHashMap<>();
        if (qs.getFILTER_SPEC() != null && !qs.getFILTER_SPEC().isEmpty()) {
            JSONParser jsonParser = new JSONParser();
            JSONObject filter_spec = new JSONObject();
            try {
                filter_spec = (JSONObject) jsonParser.parse(qs.getFILTER_SPEC().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            criteria.put("filter_spec", filter_spec);
            criteria.put("operation", cs.getOperation());
            criteria.put("credentiallist", uas.getCredentialList());
        }

        //second, parse the data source/s
        ArrayList<String> sources;
        if (cs.getSources() != null && cs.getSources().size() > 0) {
            sources = cs.getSources();
        } else {
            //if no such constraint in the request, query all data sources
            sources = FDEApplication.getAllStorageSources();
        }

        //third, initialize result object
        ContentResult result = new ContentResult();
        result.addOperation(cs.getOperation());
        //for each source, query and get the response, and then composite the final result
        for (String source : sources) {
            long startTime = System.currentTimeMillis();
            JSONObject response = CallServiceFunctions.querySource(source, null, criteria);
            long latency = System.currentTimeMillis() - startTime;
            if (response.get("fail") != null) {
                result.addStatus(source, response);
            } else {
                JSONArray a = (JSONArray) response.get("success");
                result.addStatus(source, "success");
                if (a != null) {
                    for (Object obj : a) {
                        result.insertPayload(obj);
                    }
                    result.addHits(source, a.size());
                }
            }
            result.addPerformance(source, latency);
        }
        return result;
    }

    public static Result getAdjacentEntities(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {

        //first, parse query specifics
        //parse scope
        ArrayList<String> types = new ArrayList<>();
        if (qs.getSCOPE_SPEC() != null && qs.getSCOPE_SPEC().size() > 0) {
            JSONArray scope_spec = qs.getSCOPE_SPEC();
            scope_spec.forEach(item -> {
                String type = (String) item;
                types.add(type);
                System.out.println("type: " + type);
            });
        }
        //parse projection
        LinkedHashMap<String, String> projection_spec = null;
        if (qs.getOUTPUT_SPEC() != null && qs.getOUTPUT_SPEC().get("project") != null) {
            projection_spec = (LinkedHashMap) qs.getOUTPUT_SPEC().get("project");
        }
        //parse filter
        //FILTER_SPEC ::= {
        //  "entities":["6062808","6055914"]
        //}}
        LinkedHashMap<String, Object> criteria = new LinkedHashMap<>();
        if (qs.getFILTER_SPEC() != null && !qs.getFILTER_SPEC().isEmpty()) {
            JSONParser jsonParser = new JSONParser();
            JSONObject filter_spec = new JSONObject();
            try {
                filter_spec = (JSONObject) jsonParser.parse(qs.getFILTER_SPEC().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONArray entities = (JSONArray) filter_spec.get("entities");
            criteria.put("entities", entities);
            criteria.put("operation", cs.getOperation());
            criteria.put("credentiallist", uas.getCredentialList());
            criteria.put("projection_spec", projection_spec);
        }
        //parse window
        if (qs.getWINDOW_SPEC() != null && !qs.getWINDOW_SPEC().isEmpty()) {
            JSONObject window_spec = qs.getWINDOW_SPEC();
            criteria.put("window_spec", window_spec);
        }

        //second, parse the data source/s
        ArrayList<String> sources;
        if (cs.getSources() != null && cs.getSources().size() > 0) {
            sources = cs.getSources();
        } else {
            //if no such constraint in the request, query all data sources
            sources = FDEApplication.getAllQuerySources();
        }

        //third, initialize result object
        FindAdjacentEntitiesResult result = new FindAdjacentEntitiesResult();
        result.addOperation(cs.getOperation());
        //for each source, query and get the response, and then composite the final result
        for (String source : sources) {
            long startTime = System.currentTimeMillis();
            JSONObject response = CallServiceFunctions.querySource(source, types, criteria);
            long latency = System.currentTimeMillis() - startTime;
            if (response.get("fail") != null) {
                result.addStatus(source, response);
            } else {
                System.out.println("response " + response);
                JSONArray a = (JSONArray) response.get("success");
                result.addStatus(source, "success");
                for (Object obj : a) {
                    result.insertPayload(obj);
                }
                result.addHits(source, a.size());
            }
            result.addPerformance(source, latency);
        }
        return result;
    }

    public static Result getLinks(ControlSpec cs, QuerySpec qs, UserAccessSpec uas) {
        System.out.println("getLinks triggered");
        return new Result();
    }
}
