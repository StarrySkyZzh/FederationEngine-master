package edu.unisa.ILE.FSA.EnginePortal;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.unisa.ILE.FSA.CDMMappingRepository.MRFunctions;
import edu.unisa.ILE.FSA.InternalDataStructure.ControlSpec;
import edu.unisa.ILE.FSA.InternalDataStructure.QuerySpec;
import edu.unisa.ILE.FSA.InternalDataStructure.Request;
import edu.unisa.ILE.FSA.InternalDataStructure.UserAccessSpec;

@CrossOrigin
@RestController
public class MRController {

    @RequestMapping(value = "/mr", method = RequestMethod.POST)
    public
    @ResponseBody
    JSONObject query(@RequestBody JSONObject json) {

        //display the request body
        System.out.println("request body: " + json);

        //parse the input
        Request requestJSON;
        ObjectMapper mapper = new ObjectMapper();
        requestJSON = JSONFunctions.JSON_match(mapper, Request.class, json);

        //obtain SPECs and formalize
        JSONObject USER_ACCESS_SPEC = requestJSON.getUSER_ACCESS_SPEC();
        JSONObject CONTROL_SPEC = requestJSON.getCONTROL_SPEC();
        JSONObject QUERY_SPEC = requestJSON.getQUERY_SPEC();

        ControlSpec cs = JSONFunctions.JSON_match(mapper, ControlSpec.class, CONTROL_SPEC);
        QuerySpec qs = null;
        if (QUERY_SPEC != null) {
            qs = JSONFunctions.JSON_match(mapper, QuerySpec.class, QUERY_SPEC);
        }
        UserAccessSpec uas = JSONFunctions.JSON_match(mapper, UserAccessSpec.class, USER_ACCESS_SPEC);

        //fetch result
        JSONObject result = MRFunctions.conduct(cs, qs, uas);

        return result;
    }
}