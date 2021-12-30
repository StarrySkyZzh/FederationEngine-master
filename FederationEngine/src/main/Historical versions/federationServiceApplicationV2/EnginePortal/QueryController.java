package edu.unisa.ILE.FSA.EnginePortal;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.unisa.ILE.FSA.InternalDataStructure.ControlSpec;
import edu.unisa.ILE.FSA.InternalDataStructure.QuerySpec;
import edu.unisa.ILE.FSA.InternalDataStructure.Request;
import edu.unisa.ILE.FSA.InternalDataStructure.Result;
import edu.unisa.ILE.FSA.InternalDataStructure.UserAccessSpec;

@CrossOrigin
@RestController
public class QueryController {

    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public
    @ResponseBody
    Result query(@RequestBody JSONObject json) {

        //display the request body
        System.out.println("request body: " + json);

        //form and display the responding info JSON
        JSONObject info = JSONFunctions.createSimpleObj("type", "query");
        JSONObject payload;

        //parse the input
        Request requestJSON;
        ObjectMapper mapper = new ObjectMapper();
        requestJSON = JSONFunctions.JSON_match(mapper, Request.class, json);

        //obtain SPECs and formalize
        JSONObject USER_ACCESS_SPEC = requestJSON.getUSER_ACCESS_SPEC();
        JSONObject CONTROL_SPEC = requestJSON.getCONTROL_SPEC();
        JSONObject QUERY_SPEC = requestJSON.getQUERY_SPEC();

        ControlSpec cs = JSONFunctions.JSON_match(mapper, ControlSpec.class, CONTROL_SPEC);
        QuerySpec qs = JSONFunctions.JSON_match(mapper, QuerySpec.class, QUERY_SPEC);
        UserAccessSpec uas = JSONFunctions.JSON_match(mapper, UserAccessSpec.class, USER_ACCESS_SPEC);

        payload = KeyFunctions.query(cs, qs, uas);

        return new Result(info,
                          payload);
    }
}
