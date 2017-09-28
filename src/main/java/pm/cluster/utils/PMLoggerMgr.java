package pm.cluster.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class PMLoggerMgr {

	public static PMLoggerMgr instance = null;
	private final String loggerName = PMLoggerMgr.class.getName().replace("PMLoggerMgr", "");
	private Logger testLogger = null;
	private FileHandler loggerFileHdlr = null;
	private Properties confProps = null;
	private File configFile = new File((getClass().getClassLoader().getResource("PmLogMgrConfig")).getFile());

	/**
	 * Since this is a Singleton, the constructor will be called only once per running process.  
	 */
	private PMLoggerMgr() {
		testLogger = Logger.getLogger(loggerName);
		this.readLoggerConfigs();
		try {
			loggerFileHdlr = new FileHandler(confProps.getProperty("logdir") + "/" + PMLoggerMgr.class.getName() + "_"
					+ System.currentTimeMillis());
			loggerFileHdlr.setFormatter(new SimpleFormatter());
			testLogger.addHandler(loggerFileHdlr);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		String logLevel = confProps.getProperty("level", "all").toLowerCase();
		testLogger.info("***The log level is: " + logLevel);
		switch (logLevel) {
		case "all":
			testLogger.setLevel(Level.ALL);
			break;
		case "info":
			testLogger.setLevel(Level.INFO);
			break;
		case "severe":
			testLogger.setLevel(Level.SEVERE);
			break;
		case "fine":
			testLogger.setLevel(Level.FINE);
			break;
		case "finest":
			testLogger.setLevel(Level.FINEST);
			break;
		case "finer":
			testLogger.setLevel(Level.FINER);
			break;
		case "config":
			testLogger.setLevel(Level.CONFIG);
			break;
		case "warning":
			testLogger.setLevel(Level.WARNING);
			break;
		case "off":
			testLogger.setLevel(Level.OFF);
			break;
		}
	}

	/**
	 * Enforcing a Singleton pattern to share this one logger within the whole
	 * framework. However, if more than one test is firing off; this will become
	 * threaded.
	 * 
	 * @return
	 */
	public static PMLoggerMgr getPmLog() {
		if (instance == null) {
			instance = new PMLoggerMgr();
		}
		return instance;
	}

	/**
	 * Reading logging info from config file
	 */
	/**
	 * Reads the log configurations from a resource file
	 */
	private void readLoggerConfigs() {
		this.confProps = new Properties();
		try {
			confProps.load(new FileReader(configFile));

			// Determining what properties got read
			Set<String> propKeys = confProps.stringPropertyNames();
			int count = 0;
			for (String key : propKeys) {
				testLogger.info("The key " + count++ + "is; " + key + " with value: " + confProps.getProperty(key));
			}

		} catch (IOException e) {
			testLogger.log(Level.SEVERE, "Unable to read log config file", e.getMessage());
			e.printStackTrace();
		}
	}
	
	/** Supplying the actual logger for processes to use.
	 * 
	 */
	public Logger getPmLogger() {
		return this.testLogger;
	}
}
