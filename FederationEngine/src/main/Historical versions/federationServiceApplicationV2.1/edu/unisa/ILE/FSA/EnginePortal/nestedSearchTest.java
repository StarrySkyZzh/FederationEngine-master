package edu.unisa.ILE.FSA.EnginePortal;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;

import edu.unisa.ILE.FSA.EnginePortal.JSONFunctions;

public class nestedSearchTest {

//    public static void main(String[] args) throws Exception {
//        String s = "{\n"
//                   + "        \"_index\": \"leprofiles\",\n"
//                   + "        \"_type\": \"location\",\n"
//                   + "        \"_id\": \"7356019\",\n"
//                   + "        \"_score\": 5.7662354,\n"
//                   + "        \"_source\": {\n"
//                   + "          \"le_id\": \"7356019\",\n"
//                   + "          \"profile\": [\n"
//                   + "            {\n"
//                   + "              \"type\": \"location\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"release_level\": \"G\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"pcode\": \"2008\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"date_created\": \"2009-11-20\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"street_name1\": \"CARMODY\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"date_time_last_modified\": \"2016-06-03\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"street_no\": \"428\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"city_town\": \"CHIPPENDALE\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"street_type_desc\": \"QUAYS\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"street_type\": \"QYS\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"state\": \"NSW\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"country_code_desc\": \"AUSTRALIA\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"creator_id_desc\": \"BOEHM, Gavin Mark\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"target_type\": \"ENTITY - PERSON\",\n"
//                   + "              \"target_summary\": \" HUMBERTO TYRONE JOHNSON M 2059-06-06\",\n"
//                   + "              \"relation_desc\": \"GUNS PHYSICAL ADDRESS OF\",\n"
//                   + "              \"target_eid\": \"6055914\"\n"
//                   + "            },\n"
//                   + "            {\n"
//                   + "              \"target_type\": \"ENTITY - PERSON\",\n"
//                   + "              \"relation_desc\": \"LEASED BY\",\n"
//                   + "              \"target_summary\": \" DANIEL KENNY JOHN M 2045-01-16\",\n"
//                   + "              \"target_eid\": \"6062808\"\n"
//                   + "            }\n"
//                   + "          ]\n"
//                   + "        }\n"
//                   + "      }";
//
//        ArrayList<Object> candidateList = new ArrayList<>();
//        String queryString = "_source.profile.target_eid";
//        JSONParser parser = new JSONParser();
//        JSONObject entry = (JSONObject) parser.parse(s);
//        candidateList.add(entry);
//
//        long start = System.nanoTime();
//        Object result1 = JSONFunctions.simpleNestedSearch(entry,queryString);
//        long elapsedTime = System.nanoTime() - start;
//        System.out.println("time used in simple nested search: " + elapsedTime);
//        System.out.println(result1);
//
//        long start2 = System.nanoTime();
//        ArrayList<Object> result2 = JSONFunctions.complexNestedSearch(candidateList, queryString);
//        long elapsedTime2 = System.nanoTime() - start2;
//        System.out.println("time used in complex nested search: " + elapsedTime2);
//
//
//        for (Object a : result2) {
//            System.out.println(a.toString());
//        }
//    }
}
