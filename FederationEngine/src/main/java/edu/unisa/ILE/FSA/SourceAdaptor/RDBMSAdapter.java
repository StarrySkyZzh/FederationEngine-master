package edu.unisa.ILE.FSA.SourceAdaptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import edu.unisa.ILE.FSA.EnginePortal.FDEApplication;
import edu.unisa.ILE.FSA.Parser.DatalogQueryRewriter;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.driver.PostgreSQLDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.graal.store.rdbms.util.DBColumn;
import fr.lirmm.graphik.graal.store.rdbms.util.DBTable;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;

public class RDBMSAdapter extends Adapter {

    private static String sourceName;
    private static String dbType;

    public RDBMSAdapter (String sourceName, String dbType) {
        this.sourceName = sourceName;
        this.dbType = dbType;
    }

    public JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria) {

        Logger logger = LogManager.getLogger();
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("log4j2.xml");
        // this will force a reconfiguration
        context.setConfigLocation(file.toURI());

        JSONObject result = new JSONObject();
        JSONObject credential = getCredential(criteria, sourceName);
        if (credential != null) {
            result = standardQuery(criteria, credential);
            }
        return result;
    }

    public JSONObject standardQuery(LinkedHashMap<String, Object> criteria, JSONObject credential) {
        System.out.println("standardQuery of " + sourceName +", "+ dbType + " triggered");
        JSONObject payload = new JSONObject();
        JSONArray payloadArray;
        try {
            //prepare store
            Store store = null;
            if(dbType.equals("postgresql")){
                String username = (String) credential.get("username");
                String password = (String) credential.get("password");
                String uri = FDEApplication.getSourceURL(sourceName)+"?user="+username+"&password="+password;
                store = new NaturalRDBMSStore(new PostgreSQLDriver(uri));
            }
            if(dbType.equals("sqlite")){
                String uri = FDEApplication.getSourceURL(sourceName);
                store = new NaturalRDBMSStore(new SqliteDriver(new File(uri)));
            }

            //prepare datalog rule file
            File DLGPRules = new File(FDEApplication.datalogRule+"/"+sourceName+".dlp");
            System.out.println("DLGPRules file for "+sourceName+" found: "+DLGPRules.exists());

            //if no prefixList input, init rewriter with default prefixList
            DatalogQueryRewriter rewriter = new DatalogQueryRewriter(DLGPRules);
            //else init with prefixList, TODO.

            //prepare query
            String query = (String) criteria.get("query");
            //add prefixes to create a full query
            LinkedList<Prefix> prefixList = rewriter.getPrefixList();
            for (Prefix p : prefixList){
                String prefixString = "@prefix "+p.getPrefixName()+": <"+p.getPrefix()+">\n";
                query = prefixString+"        "+query;
            }

            //rewrite query
            UnionOfConjunctiveQueries ucq = rewriter.rewrite(query);
            UnionOfConjunctiveQueries opUcq = rewriter.optimise(ucq, store);

            //conduct query
            CloseableIterator<Substitution> results = SmartHomomorphism.instance().execute(opUcq, store);

            payloadArray = extractPayload(results);
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject responseBody = new JSONObject();
            responseBody.put("error", e.getMessage());
            responseBody.put("cause", e.getCause());
            payload.put("fail", responseBody);
            return payload;
        }
        payload.put("success", payloadArray);
        return payload;
    }

    public static JSONArray extractPayload(CloseableIterator<Substitution> results) throws Exception {
        JSONArray payload_spec = new JSONArray();
        if (results.hasNext()) {
            do {
                TreeMapSubstitution mapping = (TreeMapSubstitution) results.next();
                JSONObject object_payload = new JSONObject();
                for (Term key : mapping.getTerms()) {
                    object_payload.put(key.getLabel(),mapping.createImageOf(key).getLabel());
                }
                JSONObject match_entry = new JSONObject();
                match_entry.put("payload", object_payload);
                JSONObject info = new JSONObject();
                info.put("source", sourceName);
                match_entry.put("info", info);
                payload_spec.add(match_entry);
            } while (results.hasNext());
        }
        results.close();
        return payload_spec;
    }
}
