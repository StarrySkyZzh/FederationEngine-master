package serviceOrchestration.InternalDataStructure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class QuerySpec {

    private JSONArray SCOPE_SPEC;
    private JSONObject FILTER_SPEC;
    private JSONObject OUTPUT_SPEC;
    private JSONObject WINDOW_SPEC;


    public JSONArray getSCOPE_SPEC() {
        return SCOPE_SPEC;
    }

    public JSONObject getFILTER_SPEC() {
        return FILTER_SPEC;
    }

    public JSONObject getOUTPUT_SPEC() {
        return OUTPUT_SPEC;
    }

    public JSONObject getWINDOW_SPEC() {
        return WINDOW_SPEC;
    }

    public QuerySpec(JSONArray SCOPE_SPEC, JSONObject FILTER_SPEC,
                     JSONObject OUTPUT_SPEC, JSONObject WINDOW_SPEC) {
        this.SCOPE_SPEC = SCOPE_SPEC;
        this.FILTER_SPEC = FILTER_SPEC;
        this.OUTPUT_SPEC = OUTPUT_SPEC;
        this.WINDOW_SPEC = WINDOW_SPEC;
    }

    public String toString() {
        return new String(
            "scope:" + SCOPE_SPEC + "," + "filter:" + FILTER_SPEC + "," + "output:" + OUTPUT_SPEC + "," + "window:" + WINDOW_SPEC);
    }

    public JSONObject toJSONObject() {
        JSONObject qs = new JSONObject();
        qs.put("scope", SCOPE_SPEC);
        qs.put("output", OUTPUT_SPEC);
        qs.put("window", WINDOW_SPEC);
        qs.put("filter", FILTER_SPEC);
        return qs;
    }

}
