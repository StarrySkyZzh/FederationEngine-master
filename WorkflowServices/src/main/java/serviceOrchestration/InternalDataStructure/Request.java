package serviceOrchestration.InternalDataStructure;

import org.json.simple.JSONObject;

public class Request {

    private JSONObject QUERY_SPEC;
    private JSONObject CONTROL_SPEC;
    private JSONObject USER_ACCESS_SPEC;


    public Request(JSONObject QUERY_SPEC, JSONObject CONTROL_SPEC,
                   JSONObject USER_ACCESS_SPEC) {
        this.QUERY_SPEC = QUERY_SPEC;
        this.CONTROL_SPEC = CONTROL_SPEC;
        this.USER_ACCESS_SPEC = USER_ACCESS_SPEC;
    }

    public JSONObject getQUERY_SPEC() {
        return QUERY_SPEC;
    }

    public JSONObject getCONTROL_SPEC() {
        return CONTROL_SPEC;
    }

    public JSONObject getUSER_ACCESS_SPEC() {
        return USER_ACCESS_SPEC;
    }

    public String toString() {
        return new String(
            "query:" + QUERY_SPEC + "," + "control:" + CONTROL_SPEC + "," + "credentials:" + USER_ACCESS_SPEC);
    }

    public JSONObject toJSONObject() {
        JSONObject request = new JSONObject();

        request.put("query", QUERY_SPEC);
        request.put("control", CONTROL_SPEC);
        request.put("credentials", USER_ACCESS_SPEC);

        return request;
    }
}
