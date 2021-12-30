package edu.unisa.ile.PromisUpdatePipeline.Watcher;

import java.io.Serializable;

import org.postgresql.PGNotification;

public class SerialisablePGNotification implements Serializable {

	private static final long serialVersionUID = -8441404020744096508L;
	/**
	 * 
	 */
	int PID;
	public int getPID() {
		return PID;
	}

	public String getName() {
		return name;
	}

	public String getParameter() {
		return parameter;
	}

	String name;
	String parameter;
	
	public SerialisablePGNotification(int PID, String name, String parameter){
		this.PID = PID;
		this.name = name;
		this.parameter = parameter;
	}

}
