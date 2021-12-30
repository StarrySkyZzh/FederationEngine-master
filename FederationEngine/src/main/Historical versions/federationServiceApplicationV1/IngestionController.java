package federationServiceApplicationV1;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class IngestionController {

    @RequestMapping("/ingestion")
    public
    @ResponseBody
    Result ingestion(@RequestBody JSONObject json) {
        //display the input JSON
        System.out.println("request body: " + json);

        //form and display the responding info JSON
        JSONObject infoJSON = new JSONObject();
        infoJSON.put("type", "ingestion");
        String info = infoJSON.toString();
        String payload;

        //parse the input
        Request requestJSON;
        ObjectMapper mapper = new ObjectMapper();
        requestJSON = JSONIOFunctions.JSON_match(mapper, Request.class, json);

        //obtain SPECs and formalize them
        JSONObject USER_ACCESS_SPEC = requestJSON.getUSER_ACCESS_SPEC();
        JSONObject CONTROL_SPEC = requestJSON.getCONTROL_SPEC();
        JSONObject QUERY_SPEC = requestJSON.getQUERY_SPEC();

        ControlSpec cs = JSONIOFunctions.JSON_match(mapper, ControlSpec.class, CONTROL_SPEC);
        QuerySpec qs = JSONIOFunctions.JSON_match(mapper, QuerySpec.class, QUERY_SPEC);
        UserAccessSpec uas = JSONIOFunctions.JSON_match(mapper, UserAccessSpec.class, USER_ACCESS_SPEC);

        //check permission to query
        int permission = KeyFunctions.checkCredential(uas, cs, qs);
        if (permission == 1) {
            //permission granted
            //conduct query
            JSONObject result = KeyFunctions.ingestion(cs, qs);
            payload = result.toString();
            //prepare the response
        } else {
            //permission denied
            //prepare the response
            payload = "error: permission denied";
        }

        return new Result(info,
                          payload);
    }
}

