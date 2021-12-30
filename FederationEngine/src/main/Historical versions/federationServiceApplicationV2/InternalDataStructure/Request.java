package edu.unisa.ILE.FSA.InternalDataStructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.simple.JSONObject;

/**
 * Created by wenhaoli on 10/04/2017.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
public class Request {

    private JSONObject QUERY_SPEC;
    private JSONObject CONTROL_SPEC;
    private JSONObject USER_ACCESS_SPEC;


    public Request(@JsonProperty("query") JSONObject QUERY_SPEC, @JsonProperty("control") JSONObject CONTROL_SPEC,
                   @JsonProperty("credentials") JSONObject USER_ACCESS_SPEC) {
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
        return new String("query: " + QUERY_SPEC + " control: " + CONTROL_SPEC + " credentials: " + USER_ACCESS_SPEC);
    }
}
