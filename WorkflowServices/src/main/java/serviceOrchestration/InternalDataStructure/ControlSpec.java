package serviceOrchestration.InternalDataStructure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ControlSpec {

    private String operation;
    private JSONArray sources;

    public String getOperation() {
        return operation;
    }

    public JSONArray getSources() {
        return sources;
    }

    public ControlSpec(String operation, JSONArray sources) {
        this.operation = operation;
        this.sources = sources;
    }

    public String toString() {
        return new String("operation: " + operation + " sources: " + sources);
    }

    public JSONObject toJSONObject() {
        JSONObject cs = new JSONObject();
        cs.put("operation", operation);
        cs.put("sources", sources);
        return cs;
    }
}
