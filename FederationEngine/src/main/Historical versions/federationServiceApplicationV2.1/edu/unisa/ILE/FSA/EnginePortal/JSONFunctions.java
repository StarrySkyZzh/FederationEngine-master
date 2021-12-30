package edu.unisa.ILE.FSA.EnginePortal;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;


/**
 * Created by wenhaoli on 20/04/2017.
 */
public class JSONFunctions {

//    public static void main(String[] args){
//        JSONObject a = new JSONObject();
//        JSONObject b1 = new JSONObject();
//        JSONObject c11 = new JSONObject();
//        JSONArray b2 = new JSONArray();
//        JSONObject c21 = new JSONObject();
//        JSONObject c22 = new JSONObject();
//        String c23 = "test23";
//        c21.put("c21",createSimpleObj("d211","test"));
//        c22.put("c22","test22");
//        b2.add(c21);
//        b2.add(c23);
//        b2.add(c22);
//        c11.put("value","test11");
//        b1.put("c11",c11);
//        b1.put("c12",null);
//        a.put("b1",b1);
//        a.put("b2",b2);
//
//        System.out.println(a);
//        String queryString = "b2.c21.d211";
//        try{
//            long start = System.nanoTime();
//            Object result1 = simpleNestedSearch(a,queryString);
//            System.out.println(result1);
//            long elapsedTime = System.nanoTime() - start;
//
//            System.out.println("time used in simple nested search: " + elapsedTime);
//
//            ArrayList<Object> candidateList = new ArrayList<>();
//            candidateList.add(a.toString());
//            long start2 = System.nanoTime();
//            ArrayList<Object> result2 = JSONFunctions.complexNestedSearch(candidateList, queryString);
//            for (Object s : result2) {
//                System.out.println(s);
//            }
//            long elapsedTime2 = System.nanoTime() - start2;
//
//            System.out.println("time used in complex nested search: " + elapsedTime2);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//
//    }

    public static JSONObject createSimpleObj(String name, Object value) {
        JSONObject obj = new JSONObject();
        obj.put(name, value);
        return obj;
    }

    public static <T> T JSON_match(ObjectMapper mapper, Class<?> type, JSONObject json) {
        T t;
        try {
            t = mapper.reader(type).readValue(json.toJSONString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cannot match input " + json + " to " + type);
            return null;
        }
        return t;
    }

    public static JSONArray arrayMerge(JSONArray a, JSONArray b) {
        for (Object object : b) {
            a.add(object);
        }
        return a;
    }

    public static Object simpleNestedSearch(JSONObject entry, String queryString) throws ParseException{
        //queryString format "a.b.c"
        //this function only return the first matching entry found in the JSONObject
        String[] queryList = queryString.split("\\.");
        Object newEntry = entry;
        JSONParser parser = new JSONParser();
        for (int i=0;i<queryList.length;i++){
            String keyword = queryList[i];
            if (newEntry == null) {
                System.out.println(1);
                return null;
            } else if (newEntry instanceof String){
                System.out.println(2);
                return null;
            } else if(parser.parse(newEntry.toString()) instanceof JSONObject) {
                //newEntry is a JSONObject
                JSONObject newEntryJSONObject = (JSONObject) newEntry;
                newEntry = newEntryJSONObject.get(keyword);
                continue;
            } else if (parser.parse(newEntry.toString()) instanceof JSONArray){
                //newEntry is a JSONArray
                JSONArray newEntryJSONArray = (JSONArray) newEntry;
                boolean found = false;
                for (int j = 0; j < newEntryJSONArray.size(); j++) {
                    Object object = newEntryJSONArray.get(j);
                    //we assume that a JSONArray only stores either JSONObjects or values
                    if (object instanceof JSONObject) {
                        if (((JSONObject) object).keySet().contains(keyword)) {
                            newEntry = ((JSONObject) object).get(keyword);
                            found = true;
                            break;
                        }
                    } else {
                        continue;
                    }
                }
                if (!found){
                    System.out.println(3);
                    return null;
                }
            }
        }
        return newEntry;
    }

    public static ArrayList<Object> complexNestedSearch(ArrayList<Object> candidateList, String queryString)
        throws ParseException {
        ArrayList<Object> hitsList = new ArrayList<>();
        //queryString format "a.b.c"
        String[] queryList = queryString.split("\\.");
        String firstQuery = queryList[0];
        String restQueries = "";
        Boolean hasRest = false;
        if (queryList.length > 1) {
            hasRest = true;
            for (int i = 1; i < queryList.length; i++) {
                restQueries += "." + queryList[i];
            }
            restQueries = restQueries.replaceFirst("\\.", "");
//            System.out.println("restQueries: " + restQueries);
        }

        JSONParser parser = new JSONParser();

        if (!hasRest) {
            //the last query
            for (Object candidate : candidateList) {
                //parse the candidate string to a JSON element
                if (parser.parse(candidate.toString()) instanceof JSONArray) {
                    //scan the JSONArray to get the matching entries and add to the result list
                    JSONArray array = (JSONArray) parser.parse(candidate.toString());
                    for (int i = 0; i < array.size(); i++) {
                        Object object = array.get(i);
                        //we assume that a JSONArray only stores either JSONObjects or values
                        if (object instanceof JSONObject) {
                            if (((JSONObject) object).get(firstQuery) != null) {
                                //match, so add to the hitslist
                                hitsList.add(((JSONObject) object).get(firstQuery));
                            }
                        } else {
                            //traced to a non JSON element, not match
                            continue;
                        }
                    }
                } else if (parser.parse(candidate.toString()) instanceof JSONObject) {
                    //simply get the matching entry and add to the result list
                    Object value = ((JSONObject) parser.parse(candidate.toString())).get(firstQuery);
                    if (value != null) {
                        hitsList.add(value);
                    }
                } else {
                    // not JSON element, does not match the query, and hence ignored
                }
            }
        } else {
            //not the last query
            for (Object candidate : candidateList) {
                //parse the candidate string to a JSON element
                if (parser.parse(candidate.toString()) instanceof JSONArray) {
                    //scan the JSONArray to get the matching entries and add to the result list
                    JSONArray array = (JSONArray) parser.parse(candidate.toString());
                    for (int i = 0; i < array.size(); i++) {
                        Object object = array.get(i);
                        //we assume that a JSONArray only stores either JSONObjects or values
                        if (object instanceof JSONObject) {
                            if (((JSONObject) object).get(firstQuery) != null) {
                                //match and not the last query, so take a recursive call to the function
                                ArrayList<Object> subCandidateList = new ArrayList<>();
                                subCandidateList.add(((JSONObject) object).get(firstQuery));
                                ArrayList<Object> subHitsList = complexNestedSearch(subCandidateList, restQueries);
                                for (Object hit : subHitsList) {
                                    hitsList.add(hit);
                                }
                            }
                        } else {
                            //traced to a non JSON element, not match
                            continue;
                        }
                    }
                } else if (parser.parse(candidate.toString()) instanceof JSONObject) {
                    //simply get the matching entry and add to the result list

                    ArrayList<Object> subCandidateList = new ArrayList<>();
                    subCandidateList.add(((JSONObject) parser.parse(candidate.toString())).get(firstQuery));
                    ArrayList<Object> subHitsList = complexNestedSearch(subCandidateList, restQueries);
                    for (Object hit : subHitsList) {
                        hitsList.add(hit);
                    }
                } else {
                    // not JSON element, does not match the query, and hence ignored
                }
            }
        }
        return hitsList;
    }
}
