package pm.cluster.artifacts;


import static org.junit.Assert.assertTrue;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.artifacts.PmNamespace;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class PmNamespaceTests {
		
	    private static Logger LOG = LoggerFactory.getLogger(PmNamespaceTests.class); 
	    private static final String kind = "testNamespace";

		// Obtaining a reference to the Kubernetes client
		@ArquillianResource
		KubernetesClient client;

		// Obtaining a reference to the Namespaces created in this session:
		@ArquillianResource
		Namespace namespace;
		
		@Test
		public void createNamespace() {
			PmNamespace myNS = new PmNamespace();
			ObjectMeta nsMD = new ObjectMeta();
			nsMD.setName("testnamespace");

			myNS.setMetaData(nsMD);
			myNS.createNamespace();
			assertTrue(client.namespaces().list().getItems().contains(myNS));
		}
}
