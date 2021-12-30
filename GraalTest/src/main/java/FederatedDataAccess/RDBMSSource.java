package FederatedDataAccess;

import fr.lirmm.graphik.graal.store.rdbms.driver.AbstractRdbmsDriver;
import org.json.simple.JSONObject;

public class RDBMSSource extends Source {

    private String ruleFilePath;
    private AbstractRdbmsDriver driver;

    public String getRuleFilePath() {
        return ruleFilePath;
    }

    public AbstractRdbmsDriver getDriver() {
        return driver;
    }

    public RDBMSSource(String sourceName, String description, String sourceType, JSONObject accessInfo, JSONObject metadata, String ruleFilePath){
        super(sourceName, description, sourceType, accessInfo, metadata);
        this.ruleFilePath = ruleFilePath;
        this.driver = buildDriver(sourceType, accessInfo);
    }

    public RDBMSSource(String sourceName, String sourceType, JSONObject accessInfo, String ruleFilePath){
        super(sourceName, null, sourceType, accessInfo, null);
        this.ruleFilePath = ruleFilePath;
        this.driver = buildDriver(sourceType, accessInfo);
    }

    public RDBMSSource(String sourceName, String sourceType, String ruleFilePath, AbstractRdbmsDriver driver){
        super(sourceName, null, sourceType, null, null);
        this.ruleFilePath = ruleFilePath;
        this.driver = driver;
    }

    public AbstractRdbmsDriver buildDriver(String sourceType, JSONObject accessInfo){
        //TO DO
        return null;
    }



}
