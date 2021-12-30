package au.edu.unisa;

import java.io.*;

/*this bean consists of two files, which is supposed as the output of Apache Spark*/
public class FilesBean {
	
	private String fileName=null;
	private BufferedReader fileContent = null;
	
	public String getFileName(){
		
		return this.fileName;
	}
	
	public void setFileName(String fileName){
		
		this.fileName = fileName;
	}
	
	public BufferedReader getFileContent(){
		
		return this.fileContent;
	}
	
	public void setFileContent(BufferedReader fileContent){
		
		this.fileContent = fileContent;
	}

}
