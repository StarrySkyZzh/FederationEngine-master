package edu.unisa.ILE.FSA.InternalDataStructure;

/**
 * Created by wenhaoli on 10/04/2017.
 */
public class Result {

    private final Object info;
    private final Object payload;

    public Result(Object info, Object payload) {
        this.info = info;
        this.payload = payload;
    }

    public Object getInfo() {
        return info;
    }

    public Object getPayload() {
        return payload;
    }
}
