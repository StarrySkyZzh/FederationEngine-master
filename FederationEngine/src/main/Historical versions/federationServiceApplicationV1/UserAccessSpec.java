package federationServiceApplicationV1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by wenhaoli on 21/04/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserAccessSpec {

    private String username;
    private String pwd;

    public String getUsername() {
        return username;
    }

    public String getPwd() {
        return pwd;
    }

    public UserAccessSpec(@JsonProperty("username") String username, @JsonProperty("pwd") String pwd) {
        this.username = username;
        this.pwd = pwd;
    }

    public String toString() {
        return new String("username: " + username);
    }
}
