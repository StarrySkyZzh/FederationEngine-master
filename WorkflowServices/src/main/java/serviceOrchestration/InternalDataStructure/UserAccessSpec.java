package serviceOrchestration.InternalDataStructure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class UserAccessSpec {

    private JSONArray credentialList;

    public JSONArray getCredentialList() {
        return credentialList;
    }

    public UserAccessSpec(JSONArray credentialList) {
        this.credentialList = credentialList;
    }

    public String toString() {
        return new String("credentiallist: " + credentialList);
    }

    public JSONObject toJSONObject() {
        JSONObject us = new JSONObject();
        us.put("credentiallist", credentialList);
        return us;
    }
}
