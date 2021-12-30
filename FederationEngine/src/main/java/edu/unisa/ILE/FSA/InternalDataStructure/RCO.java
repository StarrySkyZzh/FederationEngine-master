package edu.unisa.ILE.FSA.InternalDataStructure;

import java.util.ArrayList;

/**
 * Created by wenhaoli on 9/05/2017. This class is representing the Request Container Object as described in the
 * Federation Engine design documentation
 */
public class RCO {

    private Object value;
    public ArrayList<RCO> linkedRCOs;

    public RCO(Object value) {
        this.value = value;
        linkedRCOs = new ArrayList<>();
    }

    public Object getValue() {
        return value;
    }

    public void prepend(RCO rco) {

        ArrayList<RCO> oldList = linkedRCOs;
        ArrayList<RCO> newList = new ArrayList<>();
        newList.add(rco);
        for (RCO oldRco : oldList) {
            newList.add(oldRco);
        }
        linkedRCOs = newList;
    }
}
