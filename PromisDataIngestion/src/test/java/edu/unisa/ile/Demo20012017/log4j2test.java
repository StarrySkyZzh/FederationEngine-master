package edu.unisa.ile.Demo20012017;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.*;

public class log4j2test {

	public static void main(String[] args) {
		
		
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		File file = new File("log4j2.xml");
		 
		// this will force a reconfiguration
		context.setConfigLocation(file.toURI());
		
		Logger logger = LogManager.getLogger(log4j2test.class.getName());
		logger.debug("Hello this is an debug message");
		logger.info("Hello this is an info message");
		logger.warn("Hello this is an warn message");
		logger.error("Hello this is an error message");
		logger.fatal("Hello this is an fatal message");
	}

}
