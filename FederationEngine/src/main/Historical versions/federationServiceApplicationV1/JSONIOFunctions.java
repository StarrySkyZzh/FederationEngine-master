package federationServiceApplicationV1;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;

/**
 * Created by wenhaoli on 20/04/2017.
 */
public class JSONIOFunctions {

    public static JSONObject createSimpleObj(String name, String value) {
        JSONObject obj = new JSONObject();
        obj.put(name, value);
        return obj;
    }

    public static <T> T JSON_match(ObjectMapper mapper, Class<?> type, JSONObject json) {
        T t;
        try {
            t = mapper.reader(type).readValue(json.toJSONString());
            System.out.println(t.toString());
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("Cannot match input " + json + " to " + type);
            return null;
        }
        return t;
    }
}
