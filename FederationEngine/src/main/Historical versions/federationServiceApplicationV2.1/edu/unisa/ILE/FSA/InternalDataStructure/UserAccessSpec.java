package edu.unisa.ILE.FSA.InternalDataStructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.simple.JSONArray;

/**
 * Created by wenhaoli on 21/04/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccessSpec {

    private JSONArray credentialList;

    public JSONArray getCredentialList() {
        return credentialList;
    }

    public UserAccessSpec(@JsonProperty("credentiallist") JSONArray credentialList) {
        this.credentialList = credentialList;
    }

    public String toString() {
        return new String("credentiallist: " + credentialList);
    }
}
