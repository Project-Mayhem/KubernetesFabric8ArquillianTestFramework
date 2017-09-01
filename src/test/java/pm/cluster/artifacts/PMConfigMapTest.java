package pm.cluster.artifacts;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.utils.KubernetesConnector;

public class PMConfigMapTest {

	private static final long serialVersionUID = 1L;
	private static final String kind = "ConfigMap";
	private String apiVer = "v1";
	private static Logger log = LoggerFactory.getLogger(PMConfigMapTest.class);
	public static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	
	protected String configfilepathandName = "/fio4krandrw.properties";
	protected String namespace = "default";
	protected String configMapName = "fio4krandwr";
	
	@Before
	public void setUp() throws Exception {
		//log.debug("Checking that namespace[" + namespace + "] exists");
	}

	@After
	public void tearDown() throws Exception {
		log.debug("Deleting all configmaps");
		kubeCon.configMaps().delete();
	}

	@Test
	/**
	 * Testing PMConfigMap constructor
	 */
	public void testPMConfigMap() {
		log.debug("Begin testPMConfigMap");
		
		try {
			PMConfigMap map = new PMConfigMap(configfilepathandName, configMapName, namespace);
			assertNotNull(map);
			log.debug("Creating configmap: " + configMapName);
			Boolean creationStatus = map.create();
			assertTrue(creationStatus);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail("Error creating configmap: " + e.getMessage());
		}
	}

}
