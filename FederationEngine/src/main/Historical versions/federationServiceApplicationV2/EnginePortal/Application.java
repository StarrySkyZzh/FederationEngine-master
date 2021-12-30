package edu.unisa.ILE.FSA.EnginePortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class Application {

    public static final String ESURL = "130.220.209.255:9200/entity_store";
    public static final String PromisJDBC = "jdbc:postgresql://130.220.210.130:5432/promis3";

    public static ArrayList<String> getAllSources() {
        ArrayList<String> sources = new ArrayList<>(Arrays.asList("es", "promis"));
        return sources;
    }

    public static ArrayList<String> getAllEntityTypes() {
        ArrayList<String> result = new ArrayList<>(Arrays.asList("person", "location", "case"));
        return result;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}