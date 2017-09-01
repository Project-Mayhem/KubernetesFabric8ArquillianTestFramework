package pm.cluster.utils;

import java.io.BufferedReader;
/**
 * Reads configuration properties from config file located in project resource directory.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pm.cluster.performance.test.FioTest;

public class ConfigFileReader {

	private static Logger fileReaderLog = LoggerFactory.getLogger(ConfigFileReader.class);
	private final String separator = "=";
	private final String valuesSeparator = ",";

	public Map<String, List<String>> readConfigFile(String configFileName) {
		Map<String, List<String>> fileConfigs = new HashMap<String, List<String>>();
		File configFile = new File((getClass().getClassLoader().getResource(configFileName)).getFile());
		try {
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			while (reader.ready()) {
				String line = reader.readLine();
				// parsing line into map
				String[] lineValues = line.split(separator);
				String[] values = lineValues[1].split(valuesSeparator);
				List<String> stringValues = new ArrayList<String>();

				for (String newValue : values) {
					stringValues.add(newValue);
				}
				fileConfigs.put(lineValues[0], stringValues);
			}
			// See what did we create by iterating through the values of this map
			
			for(Entry<String, List<String>> aMap: fileConfigs.entrySet()) {
				//put list of values into a string
				String entryValues=null;
				for(String entry: aMap.getValue()) {
					entryValues += entry;
				}
				fileReaderLog.info("Key {} with values {}", aMap.getKey(), entryValues);
			}
		} catch (FileNotFoundException e) {
			fileReaderLog.error("Can't find " + configFileName + " file", e.getStackTrace());
		} catch (IOException e) {
			fileReaderLog.error("Can't read " + configFileName + " file", e.getStackTrace());
			e.printStackTrace();
		}
		return fileConfigs;
	}

	public static void main(String[] args) {
		ConfigFileReader myReader = new ConfigFileReader();
		myReader.readConfigFile("fiotest.config");
	}
}
