package mysop.cluster.structure.test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import mysop.cluster.artifacts.ProjMayhamNamespace;

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
		public void createService()
		{
			String apiVer = "1.0";
			ObjectMeta nsMeta = new ObjectMeta();
			String nsName = "testHarness.namespace";
			
			nsMeta.setName(nsName);
			
			Namespace nsp = new Namespace();
			nsp.setApiVersion(apiVer);
			nsp.setKind("Service");
			nsp.setMetadata(nsMeta);
			
			//build namespace
			NamespaceBuilder nsBldr = new NamespaceBuilder(nsp);
			nsBldr.build();
			
			assertSame(client.getApiVersion(),apiVer);
			
			//See if namespace exists; by assuming there is a find in Kubernetes
			String testNsName = null;
			NamespaceList namspas = client.namespaces().list();
			List<Namespace> nsItems = namspas.getItems();
			for(Namespace nspace: nsItems) {
				if(nspace.getMetadata().getName().equalsIgnoreCase(nsName))
					testNsName=nspace.getMetadata().getName();
			}
			assertSame(nsName,testNsName);
			
		}
}
