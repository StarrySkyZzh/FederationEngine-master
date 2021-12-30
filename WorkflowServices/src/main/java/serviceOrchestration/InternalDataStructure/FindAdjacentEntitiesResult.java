package serviceOrchestration.InternalDataStructure;

import org.json.simple.JSONArray;

public class FindAdjacentEntitiesResult {

    private JSONArray entities;

    public FindAdjacentEntitiesResult(){
        entities = new JSONArray();
    }

    public FindAdjacentEntitiesResult(JSONArray list){
        entities = list;
    }

    public JSONArray getEntities() {
        return entities;
    }

    public void setEntities(JSONArray entities) {
        this.entities = entities;
    }

}
