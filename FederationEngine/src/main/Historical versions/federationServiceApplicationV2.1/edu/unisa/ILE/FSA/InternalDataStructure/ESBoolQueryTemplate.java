package edu.unisa.ILE.FSA.InternalDataStructure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by wenhaoli on 28/7/17.
 */
public class ESBoolQueryTemplate {

    private JSONObject requestBody;

    public JSONObject getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(JSONObject requestBody) {
        this.requestBody = requestBody;
    }

//    public static void main(String[] args){
//        ESBoolQueryTemplate query = new ESBoolQueryTemplate();
//
//        String matchKey = "_all";
//        String matchValue = "john smith";
//        String matchOperator = "and";
//
//        JSONObject criteria1 = query.createMatch(matchKey,matchValue,matchOperator,true);
//        query.inputMust(criteria1);
//
//        JSONObject criteria2 = query.createMatch("last_name","smith","or", true);
//        query.inputMust(criteria2);
//
//        String rangeKey = "height";
//        String gte = "100";
//        String lte = "200";
//
//        JSONObject criteria3 = query.createRange(rangeKey, gte, "gte");
//        query.inputMust(criteria3);
//
//        JSONObject criteria4 = query.createRange(rangeKey, lte, "lte");
//        query.inputMust(criteria4);
//
//        System.out.println(query.getRequestBody());
//        System.out.println(query.getBool());
//    }

    public ESBoolQueryTemplate() {
        requestBody = new JSONObject();
        JSONArray must = new JSONArray();
        JSONArray should = new JSONArray();
        JSONObject bool = new JSONObject();
        JSONObject query = new JSONObject();
        bool.put("must", must);
        bool.put("should", should);
        query.put("bool", bool);
        requestBody.put("query", query);
    }

    public void inputMust(JSONObject input) {
        ((JSONArray) ((JSONObject) ((JSONObject) requestBody.get("query")).get("bool")).get("must")).add(input);
    }

    public void inputShould(JSONObject input) {
        ((JSONArray) ((JSONObject) ((JSONObject) requestBody.get("query")).get("bool")).get("should")).add(input);
    }

    public JSONObject createMatch(String matchKey, String matchValue, String matchOperator, boolean useOperator) {
        JSONObject criteria = new JSONObject();
        JSONObject match = new JSONObject();
        JSONObject matchJSON = new JSONObject();
        matchJSON.put("query", matchValue);
        if (useOperator) {
            matchJSON.put("operator", matchOperator);
        }
        match.put(matchKey, matchJSON);
        criteria.put("match", match);
        return criteria;
    }

    public JSONObject createRange(String rangeKey, String rangeValue, String inequality) {
        JSONObject criteria = new JSONObject();
        JSONObject range = new JSONObject();
        JSONObject rangeJSON = new JSONObject();
        rangeJSON.put(inequality, rangeValue);
        range.put(rangeKey, rangeJSON);
        criteria.put("range", range);
        return criteria;
    }

    public JSONObject getBool() {
        return (JSONObject) requestBody.get("query");
    }
}
