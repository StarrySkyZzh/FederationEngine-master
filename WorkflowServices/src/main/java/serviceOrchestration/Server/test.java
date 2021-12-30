package serviceOrchestration.Server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import serviceOrchestration.InternalDataStructure.Request;

public class test {

    public static void main(String[] args) {
        JSONArray entities = new JSONArray();
        entities.add("6062808");
        entities.add("6055914");
        JSONArray types = new JSONArray();
        types.add("person");

        Request request = FindAdjacentEntities.createFDERequestPayload(entities, types);
        JSONObject requestJSON = request.toJSONObject();
        System.out.println(requestJSON);
    }
}
