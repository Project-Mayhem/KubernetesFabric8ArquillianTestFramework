package pm.cluster.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ConfigMap;

public class PodUtilsTest {

	private static Logger log = LoggerFactory.getLogger(PodUtilsTest.class);
	protected String propertiesFile = "no.properties";
	
	@Before
	public void setUp() throws Exception {
		propertiesFile = "/fiotest.config";
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProperties2ConfigMap() {
		try {
			ConfigMap m = PodUtils.properties2ConfigMap(propertiesFile);
			assertNotNull(m);
			log.debug("Properties file contents: " + m.toString());
		} catch (Exception e) {
			fail("Error populating configmap: " + e.getMessage());
		}
	}

}
