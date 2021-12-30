package FederatedDataAccess;

import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.driver.AbstractRdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;

import java.io.File;
import java.util.*;

public class SourceRepository {

    private String repoName;
    private Set<Source> sourcePool;
    private static SourceRepository instance;

    public String getRepoName() {
        return repoName;
    }

    public Set<Source> getSourcePool() {
        return sourcePool;
    }

    public SourceRepository(String repoName, Set<Source> sourcePool){
        this.repoName = repoName;
        this.sourcePool = sourcePool;
    }

    public SourceRepository(String repoName, boolean createDefaultPool){
        this.repoName = repoName;
        if (createDefaultPool){
            this.sourcePool = createDefaultSourcePool();
        }
    }

    public static synchronized SourceRepository emptyInstance(String repoName) {
        if (instance == null)
            instance = new SourceRepository(repoName, false);
        return instance;
    }

    public static synchronized SourceRepository defaultInstance(String repoName) {
        if (instance == null)
            instance = new SourceRepository(repoName, true);
        return instance;
    }

    public static synchronized SourceRepository instance(String repoName, Set<Source> sourcePool) {
        if (instance == null)
            instance = new SourceRepository(repoName, sourcePool);
        return instance;
    }

    public void addSource(Source s){
        this.getSourcePool().add(s);
    }

    public void removeSource(Source s){
        this.getSourcePool().remove(s);
    }

    public Set<Source> createDefaultSourcePool(){
//        String CaseMagementSystemURL = "jdbc:postgresql://103.61.226.39:5432/CaseManagementSystem";
//        String VehicleManagementSystemURL = "jdbc:postgresql://103.61.226.39:5432/VehicleManagementSystem";

        String root = "./DatalogRules/";
        Set<Source> sourcePool = new HashSet<Source>();
        try{
            AbstractRdbmsDriver cmsDriver = new SqliteDriver(new File("./GraalTest/CaseManagementSystem.db"));
            RDBMSSource cms = new RDBMSSource("cms", "sqlite", root+"CMS.dlp", cmsDriver);
            AbstractRdbmsDriver vmsDriver = new SqliteDriver(new File("./GraalTest/VehicleManagementSystem.db"));
            RDBMSSource vms = new RDBMSSource("vms", "sqlite", root+"VMS.dlp", vmsDriver);
            sourcePool.add(cms);
            sourcePool.add(vms);
        } catch(Exception e){
            e.printStackTrace();
        }

        return sourcePool;
    }
}
