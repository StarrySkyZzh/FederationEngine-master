package edu.unisa.ILE.FSA.EnginePortal;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by wenhaoli on 20/04/2017.
 */
public class JSONFunctions {

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

    public static JSONArray simpleMerge(JSONArray a, JSONArray b) {

        for (Object object : b) {
            a.add(object);
        }

        return a;
    }

}
