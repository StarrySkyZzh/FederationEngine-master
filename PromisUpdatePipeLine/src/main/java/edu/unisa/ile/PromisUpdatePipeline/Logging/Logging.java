package edu.unisa.ile.PromisUpdatePipeline.Logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;


public class Logging {

	public static void main(String[] args) {

//		LoggerContext testContext = (LoggerContext) LogManager.getContext(false);
//		File file = new File("log4j2.xml");
//
//		// this will force a reconfiguration
//		testContext.setConfigLocation(file.toURI());
		Logging.init("PromisUpdatePipeLine/log4j2.xml");

//		Logger logger = LogManager.getLogger(log4j2test.class.getName());
//		logger.debug("Hello this is an debug message");
//		logger.info("Hello this is an info message");
//		logger.warn("Hello this is an warn message");
//		logger.error("Hello this is an error message");
//		logger.fatal("Hello this is an fatal message");
		
		debug("Hello this is an debug message");
		info("Hello this is an info message");
		warn("Hello this is an warn message");
		error("Hello this is an error message");
		fatal("Hello this is an fatal message");
	}

	public static void init(String configPath){
		LoggerContext context = (LoggerContext) LogManager.getContext(false);
		File file = new File(configPath);
		// this will force a reconfiguration
		context.setConfigLocation(file.toURI());
	}
	
	private static final Logger logger = LogManager.getLogger();
	
	public static void trace(String log){
		logger.trace(log);
	}
	
	public static void debug(String log){
		logger.debug(log);
	}
	public static void info(String log){
		logger.info(log);
	}
	public static void warn(String log){
		logger.warn(log);
	}
	public static void error(String log){
		logger.error(log);
	}
	public static void fatal(String log){
		logger.fatal(log);
	}
}
