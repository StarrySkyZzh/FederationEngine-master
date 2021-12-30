package edu.unisa.ILE.FSA.Parser;

import org.json.simple.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.unisa.ILE.FSA.InternalDataStructure.RCO;

/**
 * Created by wenhaoli on 11/05/2017.
 */
public class QueryParser {
//    public static void main(String[] args){
//        JSONObject input1 = new JSONObject();
//        JSONObject status1 = new JSONObject();
//        List<String> list1 = Arrays.asList("A", "D");
//        status1.put("$in",list1);
//        input1.put("status",status1);
//        System.out.println("input1: "+input1);
//
//        JSONObject input2 = new JSONObject();
//        JSONObject status2 = new JSONObject();
//        JSONObject qty2 = new JSONObject();
//        JSONObject lt2 = new JSONObject();
//        status2.put("status","A");
//        lt2.put("$lt",30);
//        qty2.put("qty",lt2);
//        List<JSONObject> list2 = Arrays.asList(status2, qty2);
//        input2.put("$or",list2);
//        System.out.println("input2: "+input2);
//
//        JSONObject input3 = new JSONObject();
//        JSONObject input4 = new JSONObject();
//        JSONObject input5 = new JSONObject();
//        try{
//            JSONParser parser = new JSONParser();
//            input3 = (JSONObject) parser.parse("{\"status\":{\"$in\":[\"A\",\"D\"]}, \"$or\":[{\"status\":\"A\"},{\"qty\":{\"$lt\":30}}]}");
//            input4 = (JSONObject) parser.parse("{\"status\":\"A\",\"y\":\"B\"}");
//            input5 = (JSONObject) parser.parse("{\"$and\":[{\"$or\":[{\"price\":{\"$in\":[\"A\",\"D\"]}},{\"price\":1.99}]},{\"$or\":[{\"sale\":true},{\"qty\":{\"$lt\":20}}]}]}");
//            System.out.println("input3: "+input3);
//            System.out.println("input4: "+input4);
//            System.out.println("input5: "+input5);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//
//        QueryParser parser = new QueryParser();
////        parser.iterate(input5);
//        RCO rco = parser.generateSyntaxTree(input5);
//        parser.iterateDFS(rco);
//        String syntaxTreeString = parser.printSyntaxTree(rco);
//        syntaxTreeString = syntaxTreeString.replace("$eq","=").replace("$and"," and ").replace("$or"," or ").replace("$lt","<").replace("$in"," in ");
//        System.out.println(syntaxTreeString);
//    }

    public static void iterateDFS(JSONObject input) {
        Set<String> keys = input.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            System.out.println(key);
            if (input.get(key) instanceof java.util.List) {
                List content = (List) input.get(key);
                for (Object i : content) {
                    if (i instanceof JSONObject) {
                        iterateDFS((JSONObject) i);
                    } else {
                        System.out.println(i);
                    }
                }
            } else if (input.get(key) instanceof JSONObject) {
                iterateDFS((JSONObject) input.get(key));
            } else {
                System.out.println(input.get(key));
            }
        }
    }

    public static void iterateDFS(RCO rco) {
        System.out.println(rco.getValue());
        for (RCO subRco : rco.linkedRCOs) {
            iterateDFS(subRco);
        }
    }

    public static String printSyntaxTree(RCO rco) {
        String equation = "";
        if (rco.getValue().toString().startsWith("$")) {
            for (int i = 0; i < rco.linkedRCOs.size(); i++) {
                if (i == rco.linkedRCOs.size() - 1) {
                    equation += printSyntaxTree(rco.linkedRCOs.get(i));
                } else {
                    equation += printSyntaxTree(rco.linkedRCOs.get(i));
                    equation += rco.getValue().toString();
                }
            }
            equation = "(" + equation + ")";
        } else {
            equation = rco.getValue().toString();
        }
        return equation;
    }

    public RCO generateSyntaxTree(JSONObject input) {
        Set<String> keys = input.keySet();
        RCO root;
        if (keys.size() > 1) {
            //it is a JSON with more than one key which means a intersection relationship,
            //add $and on top
            RCO superRoot = new RCO("$and");
            for (String key : keys) {
                //if the key name is an operator
                if (key.startsWith("$")) {
                    System.out.println("operator key found: " + key);
                    root = new RCO(key);
                    if (input.get(key) instanceof java.util.List) {
                        List content = (List) input.get(key);
                        for (Object i : content) {
                            if (i instanceof JSONObject) {
                                System.out.println("JSONObject value found: " + i);
                                RCO subRoot = generateSyntaxTree((JSONObject) i);
                                root.linkedRCOs.add(subRoot);
                            } else {
                                System.out.println("simple value list found: " + content);
                                RCO subRoot = new RCO(content);
                                root.linkedRCOs.add(subRoot);
                                break;
                            }
                        }
                    } else {//key name is an operator and the content is not a list,
                        // add the content to the operator
                        System.out.println("simple value found 1:" + input.get(key));
                        RCO subRoot = new RCO("'" + input.get(key) + "'");
                        root.linkedRCOs.add(subRoot);
                    }
                } else {//if the key name is a non operator
                    System.out.println("non operator key found: " + key);
                    //assumption: if the content is a JSONObject then it must start with an operator
                    //rearrange the RCO structure and insert the non operator key to the left
                    if (input.get(key) instanceof JSONObject) {
                        System.out.println("JSONObject value found: " + input.get(key));
                        RCO subRoot = new RCO(key);
                        root = generateSyntaxTree((JSONObject) input.get(key));
                        root.prepend(subRoot);
                    } else {//the content is a non json value,
                        // add $eq between the key and the value
                        System.out.println("simple value found 2:" + input.get(key));
                        root = new RCO("$eq");
                        RCO subRoot = new RCO(key);
                        RCO subRoot2 = new RCO("'" + input.get(key) + "'");
                        root.linkedRCOs.add(subRoot);
                        root.linkedRCOs.add(subRoot2);
                    }
                }
                superRoot.linkedRCOs.add(root);
            }

            return superRoot;

        } else {//keySet size is 1
            String key = (String) keys.toArray()[0];
            //if the key name is an operator
            if (key.startsWith("$")) {
                System.out.println("operator key found: " + key);
                root = new RCO(key);
                if (input.get(key) instanceof java.util.List) {
                    List content = (List) input.get(key);
                    for (Object i : content) {
                        if (i instanceof JSONObject) {
                            System.out.println("JSONObject value found: " + i);
                            RCO subRoot = generateSyntaxTree((JSONObject) i);
                            root.linkedRCOs.add(subRoot);
                        } else {
                            System.out.println("simple value content list: " + content);
                            RCO subRoot = new RCO(content);
                            root.linkedRCOs.add(subRoot);
                            break;
                        }
                    }
                } else {//key name is an operator and the content is not a list,
                    // add the content to the operator
                    System.out.println("simple value found 3: " + input.get(key));
                    RCO subRoot = new RCO("'" + input.get(key) + "'");
                    root.linkedRCOs.add(subRoot);
                }
            } else {//if the key name is a non operator
                System.out.println("non operator key found: " + key);
                //assumption: if the content is a JSONObject then it must start with an operator
                //rearrange the RCO structure and insert the non operator key to the left
                if (input.get(key) instanceof JSONObject) {
                    System.out.println("JSONObject value found: " + input.get(key));
                    RCO subRoot = new RCO(key);
                    root = generateSyntaxTree((JSONObject) input.get(key));
                    root.prepend(subRoot);
                } else {//the content is a non json value,
                    // add $eq between the key and the value
                    System.out.println("simple value found 4: " + input.get(key));
                    root = new RCO("$eq");
                    RCO subRoot = new RCO(key);
                    RCO subRoot2 = new RCO("'" + input.get(key) + "'");
                    root.linkedRCOs.add(subRoot);
                    root.linkedRCOs.add(subRoot2);
                }
            }

            return root;

        }
    }
}
