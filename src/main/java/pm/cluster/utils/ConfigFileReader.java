package pm.cluster.utils;

import java.io.BufferedReader;
/**
 * Reads configuration properties from config file located in project resource directory.
 * @author Anastasia
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
				List<String> stringValues = new ArrayList<String>();
				// parsing line into map
				String[] lineValues = line.split(separator);
				String key = lineValues[0];
				String value = lineValues[1];

				List<String> listValues = new ArrayList<String>();
				if (value.contains(valuesSeparator)) {
					String[] values = value.split(valuesSeparator);
					for (String listEntry : values) {
						fileReaderLog.info("adding to list: " + key + ":" + listEntry);
						listValues.add(listEntry);
					}
					fileConfigs.put(key, listValues);
				} else {
					listValues.add(value);
					fileConfigs.put(key, listValues);
				}
			}
			fileReaderLog.info("Here is what the config file says:\n");
			int count = 1;
			for (Map.Entry<String, List<String>> entry : fileConfigs.entrySet()) {
				fileReaderLog.info("Entry " + count++ + "key: " + entry.getKey() + " with these values:");
				List<String> receivedValues = entry.getValue();
				for (String val : receivedValues) {
					fileReaderLog.info(val);
				}
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
		// myReader.readConfigFile("fiotest.config");
		// myReader.readConfigFile("persistentVolume.config");
		myReader.readConfigFile("persistentVolumeNFS.config");
	}
}
