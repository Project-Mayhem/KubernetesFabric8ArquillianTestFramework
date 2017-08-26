package pm.artifacts.unittests;


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
import pm.cluster.artifacts.ProjMayhamNamespace;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class NamespaceTests {
		
	    private static Logger LOG = LoggerFactory.getLogger(NamespaceTests.class); 
	    private static final String kind = "Namespace";

		// Obtaining a reference to the Kubernetes client
		@ArquillianResource
		KubernetesClient client;

		// Obtaining a reference to the Services created in this session:
		@ArquillianResource
		Namespace namespace;
		
		@Test
		public void createNamespace() {
			ProjMayhamNamespace myNS = new ProjMayhamNamespace();
			ObjectMeta nsMD = new ObjectMeta();
			nsMD.setName("testnamespace");

			myNS.setMetaData(nsMD);
			myNS.createNamespace();
			assertTrue(client.namespaces().list().getItems().contains(myNS));
		}
}
