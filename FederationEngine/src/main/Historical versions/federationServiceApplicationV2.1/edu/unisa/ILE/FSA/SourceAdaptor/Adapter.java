package edu.unisa.ILE.FSA.SourceAdaptor;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.LinkedHashMap;


/**
 * Created by wenhaoli on 22/06/2017.
 */
public abstract class Adapter {

    public abstract JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria);

    public JSONObject getCredential(LinkedHashMap<String, Object> criteria, String sourceName) {
        JSONObject result = new JSONObject();
        JSONArray credentialList;
        try {
            JSONParser parser = new JSONParser();
            credentialList = (JSONArray) parser.parse(criteria.get("credentiallist").toString());
        } catch (ParseException e) {
            return null;
        }
        for (int i = 0; i < credentialList.size(); i++) {
            JSONObject credential = (JSONObject) credentialList.get(i);
            if (credential.containsKey(sourceName)) {
                result = (JSONObject) credential.get(sourceName);
                break;
            }
        }
        return result;
    }

//    public abstract String convertToSourceType(String input);
//
//    public abstract String convertToSourceColumn(String input);
//
//    public abstract String convertToGenericColumn(String input);
}
