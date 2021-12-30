package edu.unisa.ILE.FSA.InternalDataStructure;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ContentResult extends Result {
    // info stores the query result status data such as success or fail, query performance
    // payload for FindEntities service is in JSONArray format, which stores the payload in the following format

    /*
    PAYLOAD_SPEC ::= list of <MATCH_ENTRY>
    MATCH_ENTRY  ::= {
        payload: <OBJECT_PAYLOAD>,
            info: <MATCH_INFO>
    }
    OBJECT_PAYLOAD ::= dictionary of <FIELD>:<VALUE> pairs
    */

    public ContentResult() {
        JSONObject info = new JSONObject();
        info.put("status", new JSONObject());
        info.put("performance", new JSONObject());
        info.put("hits", new JSONObject());
        info.put("type", "query");
        info.put("operation", "");
        JSONArray payload = new JSONArray();
        setInfo(info);
        setPayload(payload);
    }

    public void addHits(String source, int size) {
        ((JSONObject) this.getInfo().get("hits")).put(source, size);
    }

    public void addStatus(String key, Object value) {
        ((JSONObject) this.getInfo().get("status")).put(key, value);
    }

    public void addOperation(String operation) {
        this.getInfo().put("operation", operation);
    }

    public void addPerformance(String key, Object value) {
        ((JSONObject) this.getInfo().get("performance")).put(key, value);
    }

    public void insertPayload(Object record) {
        ((JSONArray) this.getPayload()).add(record);
    }
}
