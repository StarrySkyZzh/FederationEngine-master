package serviceOrchestration.Server;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Component;

import serviceOrchestration.InternalDataStructure.ControlSpec;
import serviceOrchestration.InternalDataStructure.QuerySpec;
import serviceOrchestration.InternalDataStructure.Request;
import serviceOrchestration.InternalDataStructure.UserAccessSpec;


@Component
public class FindAdjacentEntities implements JavaDelegate {

    public void execute(DelegateExecution execution) throws Exception {
        //get variables
        int hops = (Integer) execution.getVariable("hops");
        JSONArray entities = (JSONArray) execution.getVariable("entities");
        JSONArray types = (JSONArray) execution.getVariable("types");
        JSONArray historicalEntities = (JSONArray) execution.getVariable("historicalEntities");

        //create request payload
        Request request = FindAdjacentEntities.createFDERequestPayload(entities, types);
        JSONObject requestJSON = request.toJSONObject();

        //call service
        JSONObject
            responseJSON =
            CallServiceFunctions.post(ServiceOrchestrationApplication.FDEURL, requestJSON.toString());
        JSONArray relatedEntities = extractFDEResponsePayloads(responseJSON);

        //update variables
        execution.setVariable("entities", relatedEntities);

        for (int i = 0; i < relatedEntities.size(); i++) {
            String entityID = (String) relatedEntities.get(i);
            if (!historicalEntities.contains(entityID)){
                historicalEntities.add(entityID);
            }
        }

        System.out.println("related entities: " + relatedEntities);
        System.out.println("historical entities: " + historicalEntities);

        hops--;
        execution.setVariable("hops", hops);
        System.out.println(hops + " hops more");

    }

    public static Request createFDERequestPayload(JSONArray entities, JSONArray types) {
        Request request;
        QuerySpec qs;
        ControlSpec cs;
        UserAccessSpec us;

        //create querySpec
        JSONArray SCOPE_SPEC;
        JSONObject FILTER_SPEC;
        JSONObject OUTPUT_SPEC;
        JSONObject WINDOW_SPEC;

        FILTER_SPEC = new JSONObject();
        OUTPUT_SPEC = new JSONObject();
        WINDOW_SPEC = new JSONObject();
        SCOPE_SPEC = types;
        FILTER_SPEC.put("entities", entities);
        OUTPUT_SPEC.put("project", new JSONObject());
        WINDOW_SPEC.put("limit", "100");

        qs = new QuerySpec(SCOPE_SPEC, FILTER_SPEC, OUTPUT_SPEC, WINDOW_SPEC);

        //create controlSpec
        String operation;
        JSONArray sources;

        sources = new JSONArray();
        sources.add("lei");
        operation = "getAdjacentEntities";

        cs = new ControlSpec(operation, sources);

        //create userAccessSpec
        JSONArray credentialList;

        credentialList = new JSONArray();
        String creString = "[\n"
                           + "    {\"es\":{\"username\":\"unisaile\",\"password\":\"unisaile\"}},\n"
                           + "    {\"lei\":{\"username\":\"unisaile\",\"password\":\"unisaile\"}},\n"
                           + "    {\"promis\":{\"username\":\"unisa\",\"password\":\"unisa\"}},\n"
                           + "    {\"poler\":{\"username\":\"poler\",\"password\":\"nefUphuch!ahE\"}}]";
        JSONParser parser = new JSONParser();
        try {
            credentialList = (JSONArray) parser.parse(creString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        us = new UserAccessSpec(credentialList);

        //create request
        request = new Request(qs.toJSONObject(), cs.toJSONObject(), us.toJSONObject());

        return request;
    }

    public JSONArray extractFDEResponsePayloads(JSONObject FDEResponse) {
        JSONArray entities = new JSONArray();
        // extract all the target_eids of the payloads of each match_entry into the return JSONArray
        JSONArray responsePayloads = (JSONArray) FDEResponse.get("payload");
        for (int i = 0; i < responsePayloads.size(); i++) {
            JSONObject match_entry = (JSONObject) responsePayloads.get(i);
            JSONObject payload = (JSONObject) match_entry.get("payload");
            String target_eid = (String) payload.get("target_eid");
            entities.add(target_eid);
        }

        return entities;
    }

}
