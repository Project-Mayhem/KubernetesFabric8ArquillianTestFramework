package pm.cluster.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ConfigMap;

public class PodUtils {

	private static Logger log = LoggerFactory.getLogger(PodUtils.class);
	
	/**
	 * Creates a configMap for use by a pod from a properties file
	 * @param filename
	 * @return ConfigMap
	 */
	public static ConfigMap properties2ConfigMap(String filename) throws Exception {
		log.debug("Begin properties2ConfigMap["+filename+"]");
		Properties properties = new Properties();
		Map<String, String> propmap = new HashMap<String, String>();
		//load propertyfile from classpath
		try (InputStream stream = PodUtils.class.getResourceAsStream(filename)) {
			properties.load(stream);
		    for (String key : properties.stringPropertyNames()) {
			    String value = properties.getProperty(key);
			    propmap.put(key, value);
			}
		    ConfigMap m = new ConfigMap();
		    m.setData(propmap);
		    return m;
		} catch (IOException e) {
			throw new Exception("Unable to read property file [" + filename + "]");
		}
	}
}
