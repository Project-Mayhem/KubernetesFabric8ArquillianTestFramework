package pm.cluster.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigMapUtilsTest {

	private static Logger log = LoggerFactory.getLogger(ConfigMapUtilsTest.class);
	protected String propertiesFile = "no.properties";
	protected String volumeMountName = "test-properties";
	
	@Before
	public void setUp() throws Exception {
		propertiesFile = "/fiotest.config";
		volumeMountName = "fiotest-configmap";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProperties2ConfigMap() {
		try {
			Map<String,String> m = ConfigMapUtils.properties2Map(propertiesFile);
			assertNotNull(m);
			log.debug("Properties file contents: " + m.toString());
		} catch (Exception e) {
			fail("Error populating configmap: " + e.getMessage());
		}
	}

}
