package edu.unisa.ILE.FSA.InternalDataStructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Created by wenhaoli on 18/04/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    public QuerySpec(@JsonProperty("scope") JSONArray SCOPE_SPEC, @JsonProperty("filter") JSONObject FILTER_SPEC,
                     @JsonProperty("output") JSONObject OUTPUT_SPEC, @JsonProperty("window") JSONObject WINDOW_SPEC) {
        this.SCOPE_SPEC = SCOPE_SPEC;
        this.FILTER_SPEC = FILTER_SPEC;
        this.OUTPUT_SPEC = OUTPUT_SPEC;
        this.WINDOW_SPEC = WINDOW_SPEC;
    }

    public String toString() {
        return new String(
            "scope: " + SCOPE_SPEC + " filter: " + FILTER_SPEC + " output: " + OUTPUT_SPEC + " window: " + WINDOW_SPEC);
    }

}
