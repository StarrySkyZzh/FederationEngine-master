package serviceOrchestration.Server;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstanceWithVariables;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import serviceOrchestration.InternalDataStructure.FindAdjacentEntitiesResult;

@CrossOrigin
@RestController
public class Controller {

    @Autowired
    private RuntimeService runtimeService;

    @RequestMapping(value = "/findNearByEntities", method = RequestMethod.POST)
    public @ResponseBody
    FindAdjacentEntitiesResult
    findNearByEntities(@RequestBody JSONObject json) {

//        System.out.println("request body: " + json);

        // prepare input variables
        JSONParser parser = new JSONParser();
        JSONObject request = new JSONObject();
        try {
            request = (JSONObject) parser.parse(json.toString());
            System.out.println("request body: " + request);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray entities = new JSONArray();
        if (request.get("entities") != null) {
            try {
                entities = (JSONArray) parser.parse(request.get("entities").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JSONArray types = new JSONArray();
        if (request.get("types") != null) {
            try {
                types = (JSONArray) parser.parse(request.get("types").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int hops = 0;
        if (request.get("hops") != null) {
            hops = Integer.parseInt(request.get("hops").toString());
        }

        System.out.println("initial input entities: " + entities);
        System.out.println("initial input hops: " + hops);
        System.out.println("initial input types: " + types);

//        HashMap<String, Object> variables = new HashMap<String,Object>();
//        variables.put("entities",entities);
//        variables.put("hops",hops);
//        String id = runtimeService.startProcessInstanceByKey("test", variables).getProcessInstanceId();

        ProcessInstanceWithVariables pi = runtimeService.createProcessInstanceByKey("FindNearByEntities")
            .setVariable("entities", entities)
            .setVariable("hops", hops)
            .setVariable("types", types)
            .setVariable("historicalEntities", new JSONArray())
            .executeWithVariablesInReturn();

//        System.out.println("now we have" + runtimeService.createProcessInstanceQuery().count()+"");

        return new FindAdjacentEntitiesResult((JSONArray) pi.getVariables().get("historicalEntities"));
    }
}
