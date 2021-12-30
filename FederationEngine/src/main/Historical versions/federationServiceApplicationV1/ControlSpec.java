package federationServiceApplicationV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.simple.JSONArray;

/**
 * Created by wenhaoli on 21/04/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ControlSpec {

    private String operation;
    private JSONArray sources;

    public String getOperation() {
        return operation;
    }

    public JSONArray getSources() {
        return sources;
    }

    public ControlSpec(@JsonProperty("operation") String operation, @JsonProperty("sources") JSONArray sources) {
        this.operation = operation;
        this.sources = sources;
    }

    public String toString() {
        return new String("operation: " + operation + " sources: " + sources);
    }
}
