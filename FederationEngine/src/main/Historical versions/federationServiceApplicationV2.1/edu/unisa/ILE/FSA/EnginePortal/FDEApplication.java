package edu.unisa.ILE.FSA.EnginePortal;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import java.util.ArrayList;
import java.util.Arrays;

import edu.unisa.ILE.FSA.CDMMappingRepository.MRCache;
import edu.unisa.ILE.FSA.CDMMappingRepository.MRFunctions;

@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class})
public class FDEApplication {

    public static final String ESURL = "103.61.226.42:9200/leprofiles";
    public static final String LEIURL = "103.61.226.42:9200/leprofiles";
    public static final String PromisJDBC = "jdbc:postgresql://103.61.226.39:5432/promis3";
    public static final String PolerAPI = "ile-poler.d2dcrc.net:8090/api/v3.1.0/personIdentity";
    public static final String HDFS = "hdfs://103.61.226.39:8020/user/ile/document";

    //mongodb://MRuser:unisa@130.220.210.130:27017/MR
    public static final String MongoDBURL = "103.61.226.39:27017/MR";
    public static final String MRusername = "MRuser";
    public static final String MRpwd = "unisa";

    public static final MRCache Cache = new MRCache("MRCache.db", false);

    public static String getSourceURL(String sourceName, String MRusername, String MRpwd) {
        JSONObject response = MRFunctions.getSource(sourceName, MRusername, MRpwd);
        JSONObject source = (JSONObject) ((JSONArray) response.get("payload")).get(0);
        JSONObject accessInfo = (JSONObject) source.get("accessInfo");
        String URL = (String) accessInfo.get("URL");
        return URL;
    }

    public static ArrayList<String> getAllQuerySources() {
        ArrayList<String> sources = new ArrayList<>(Arrays.asList("es", "promis", "poler", "lei"));
        return sources;
    }

    public static ArrayList<String> getAllStorageSources() {
        ArrayList<String> sources = new ArrayList<>(Arrays.asList("hdfs"));
        return sources;
    }

    public static ArrayList<String> getAllEntityTypes(String sourceName) {
        ArrayList<String> result = new ArrayList<>();
        switch (sourceName) {
            case "es":
                result = new ArrayList<>(Arrays.asList("person", "location", "case"));
                break;
            case "promis":
                result = new ArrayList<>(Arrays.asList("person", "location", "case"));
                break;
            case "poler":
                result = new ArrayList<>(Arrays.asList("person"));
                break;
        }
        return result;
    }


    public static void main(String[] args) {
        SpringApplication.run(FDEApplication.class, args);
    }

}