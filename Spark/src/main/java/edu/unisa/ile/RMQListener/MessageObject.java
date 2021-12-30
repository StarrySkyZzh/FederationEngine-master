package edu.unisa.ile.RMQListener;

public class MessageObject {
	
	public static void main(String[] args){
		
		String message = "high,UPDATE,company,old:(100,paul,32,\"California                                        \",20000.00),new:(1,paul,32,\"California                                        \",20000.00)";
		MessageObject mo = new MessageObject(message);
		System.out.println(mo.getPriority());
		System.out.println(mo.getOp());
		System.out.println(mo.getTableName());
		System.out.println(mo.getOldRecord().length);
		System.out.println(mo.getNewRecord().length);
		
	}
	
	String priority;
	String op;
	String tableName;
	String [] oldRecord;
	String [] newRecord;
	
	public String getPriority() {
		return priority;
	}
	
	public String getOp() {
		return op;
	}

	public String getTableName() {
		return tableName;
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
		
		String [] oldRecord = null;
		String [] newRecord = null;
		
		if (op.equals("UPDATE")){
			
			String sOld = message.substring(message.indexOf("old:"), message.indexOf("new:"));
			sOld = sOld.replace("old:", "").replace("(","").replace(")", "");
			oldRecord = sOld.split(",");
			this.oldRecord = oldRecord;
			
			String sNew = message.substring(message.indexOf("new:"), message.length());
			sNew = sNew.replace("new:", "").replace("(","").replace(")", "");
			newRecord = sNew.split(",");
			this.newRecord = newRecord;
			
		} else if (op.equals("INSERT")){
			
			String sNew = message.substring(message.indexOf("new:"), message.length());
			sNew = sNew.replace("new:", "").replace("(","").replace(")", "");
			newRecord = sNew.split(",");
			this.newRecord = newRecord;
			
		} else if (op.equals("DELETE")){
			
			String sOld = message.substring(message.indexOf("old:"), message.length());
			sOld = sOld.replace("old:", "").replace("(","").replace(")", "");
			oldRecord = sOld.split(",");
			this.oldRecord = oldRecord;
			
		}
	
	}

}
