package pm.cluster.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pm.cluster.utils.exceptions.ConfigMapUtilsException;

public class ConfigMapUtils {

	private static Logger log = LoggerFactory.getLogger(ConfigMapUtils.class);
	
	/**
	 * Creates a HashMap from a properties file
	 * @param filename
	 * @return Map
	 */
	public static Map<String,String> properties2Map(String filename) throws ConfigMapUtilsException {
		log.debug("Begin properties2ConfigMap["+filename+"]");
		Properties properties = new Properties();
		Map<String, String> propmap = new HashMap<String, String>();
		//load propertyfile from classpath
		try (InputStream stream = ConfigMapUtils.class.getResourceAsStream(filename)) {
			properties.load(stream);
		    for (String key : properties.stringPropertyNames()) {
			    String value = properties.getProperty(key);
			    propmap.put(key, value);
			} 
		    return propmap;
		} catch (IOException e) {
			throw new ConfigMapUtilsException("Unable to read property file [" + filename + "]",e);
		}
	}
}
