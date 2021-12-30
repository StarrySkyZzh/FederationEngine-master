package edu.unisa.ILE.FSA.InternalDataStructure;

import org.json.simple.JSONObject;

/**
 * Created by wenhaoli on 10/04/2017.
 */
public class Result {

    private JSONObject info;
    private Object payload;

    public Result(JSONObject info, Object payload) {
        this.info = info;
        this.payload = payload;
    }

    public Result() {
    }

    public JSONObject getInfo() {
        return info;
    }

    public Object getPayload() {
        return payload;
    }

    public void setInfo(JSONObject info) {
        this.info = info;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
