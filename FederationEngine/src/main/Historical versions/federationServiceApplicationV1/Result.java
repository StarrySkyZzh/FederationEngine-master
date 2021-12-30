package federationServiceApplicationV1;

/**
 * Created by wenhaoli on 10/04/2017.
 */
public class Result {

    private final String info;
    private final String payload;

    public Result(String info, String payload) {
        this.info = info;
        this.payload = payload;
    }

    public String getInfo() {
        return info;
    }

    public String getPayload() {
        return payload;
    }

    public String toString() {
        return "info: " + info + " payload: " + payload;
    }
}
