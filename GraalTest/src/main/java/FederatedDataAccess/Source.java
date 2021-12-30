package FederatedDataAccess;

import org.json.simple.JSONObject;

public class Source {
    private String sourceName;
    private String description;
    private String sourceType;
    private JSONObject accessInfo;
    private JSONObject metadata;

    public String getSourceName() {
        return sourceName;
    }

    public String getDescription() {
        return description;
    }

    public String getSourceType() {
        return sourceType;
    }

    public JSONObject getAccessInfo() {
        return accessInfo;
    }

    public JSONObject getMetadata() {
        return metadata;
    }

    public Source(String sourceName, String description, String sourceType, JSONObject accessInfo, JSONObject metadata){

        this.sourceName = sourceName;
        this.description = description;
        this.sourceType = sourceType;
        this.accessInfo = accessInfo;
        this.metadata = metadata;
    }
}
