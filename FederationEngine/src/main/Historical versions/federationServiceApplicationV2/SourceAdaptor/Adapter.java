package edu.unisa.ILE.FSA.SourceAdaptor;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by wenhaoli on 22/06/2017.
 */
public interface Adapter {

    JSONObject send(ArrayList<String> types, LinkedHashMap<String, Object> criteria);

    HashMap getCredential(LinkedHashMap<String, Object> criteria);
}
