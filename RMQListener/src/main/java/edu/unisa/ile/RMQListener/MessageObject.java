package edu.unisa.ile.RMQListener;

public class MessageObject {

    public static void main(String[] args) {

        String
            message =
            "high,UPDATE,company,id,1,100,{id,name,age,address,salary},old:(1,Paul,32,\"California            \",20000.00),new:(100,paul,32,\"California                                        \",20000.00)";
        // String message =
        // "high,INSERT,company,id,11,{id,name,age,address,salary},new:(11,Paul,32,
        // \"California \",20000.00)";
        // String message =
        // "high,DELETE,company,id,4,{id,name,age,address,salary},old:(4,Paul,32,\"California
        // \",20000.00)";

        MessageObject mo = new MessageObject(message);
        System.out.println(mo.getPriority());
        System.out.println(mo.getOp());
        System.out.println(mo.getTableName());

        // System.out.println(mo.pknames[0] + ", " + mo.oldPkValues[0]);
        // System.out.println(mo.pknames[0] + ", " + mo.newPkValues[0]);
        System.out.println(mo.pknames[0] + ", " + mo.oldPkValues[0] + "," + mo.newPkValues[0]);

        System.out.println(mo.getColumnNames().length);

        if (mo.getOldRecord() != null) {
            System.out.println(mo.getOldRecord().length);
        }
        if (mo.getNewRecord() != null) {
            System.out.println(mo.getNewRecord().length);
        }
    }

    String priority;
    String op;
    String tableName;
    String[] pknames;
    String[] oldPkValues;
    String[] newPkValues;
    String[] fknames;
    String[] oldFkValues;
    String[] newFkvalues;
    String[] columnNames;
    String[] oldRecord;
    String[] newRecord;

    public String[] getColumnNames() {
        return columnNames;
    }

    public String getPriority() {
        return priority;
    }

    public String getOp() {
        return op;
    }

    public String getTableName() {
        return tableName;
    }

    public String[] getPknames() {
        return pknames;
    }

    public String[] getOldPkValues() {
        return oldPkValues;
    }

    public String[] getNewPkValues() {
        return newPkValues;
    }

    public String[] getFknames() {
        return fknames;
    }

    public String[] getOldFkValues() {
        return oldFkValues;
    }

    public String[] getNewFkvalues() {
        return newFkvalues;
    }

    public String[] getOldRecord() {
        return oldRecord;
    }

    public String[] getNewRecord() {
        return newRecord;
    }

    public MessageObject(String message) {
        // TODO Auto-generated method stub
        message = message.replace("\"", "");
        String[] info = message.split(",");
        String priority = info[0];
        this.priority = priority;
        String op = info[1];
        this.op = op;
        String tableName = info[2];
        this.tableName = tableName;
        String pkname = info[3];
        this.pknames = new String[]{pkname};

        String columnNameString = message.substring(message.indexOf("{") + 1, message.indexOf("}"));
        String[] columnNames = columnNameString.split(",", -1);
        this.columnNames = columnNames;

        String[] oldRecord = null;
        String[] newRecord = null;

        if (op.equals("UPDATE")) {
            String oldPkValue = info[4];
            this.oldPkValues = new String[]{oldPkValue};
            String newPkValue = info[5];
            this.newPkValues = new String[]{newPkValue};
//careful when use split in update, the index is ",new:" rather than "new:"
            String sOld = message.substring(message.indexOf("old:"), message.indexOf(",new:"));
            sOld = sOld.replace("old:", "").replace("(", "").replace(")", "");
            oldRecord = sOld.split(",", -1);
            this.oldRecord = oldRecord;

            String sNew = message.substring(message.indexOf("new:"), message.length());
            sNew = sNew.replace("new:", "").replace("(", "").replace(")", "");
            newRecord = sNew.split(",", -1);
            this.newRecord = newRecord;

        } else if (op.equals("INSERT")) {

            String newPkValue = info[4];
            this.newPkValues = new String[]{newPkValue};

            String sNew = message.substring(message.indexOf("new:"), message.length());
            sNew = sNew.replace("new:", "").replace("(", "").replace(")", "");
            newRecord = sNew.split(",", -1);
            this.newRecord = newRecord;

        } else if (op.equals("DELETE")) {

            String oldPkValue = info[4];
            this.oldPkValues = new String[]{oldPkValue};

            String sOld = message.substring(message.indexOf("old:"), message.length());
            sOld = sOld.replace("old:", "").replace("(", "").replace(")", "");
            oldRecord = sOld.split(",", -1);
            this.oldRecord = oldRecord;

        }

    }

}
