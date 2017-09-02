import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FIOTestRunner {
	
	/**
	 * This will create 1 to many FIO containers with persistent volume claims.  The specifics for this test will read the fiotest.config resource file.
	 */

	public static final String configFileName = "fiotest.config";
	
	private static Logger LOG = LoggerFactory.getLogger(FIOTestRunner.class); 
	private ClassLoader classLoader = getClass().getClassLoader();
	private File configFile = new File(classLoader.getResource(configFileName).getFile());
	
	public void init() {
		//read contents of file
	}
	
	private void readConfigFile() {
		
		
	}
	
	
}
