package mysop.test;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import mysop.test.harness.ProjMayhemService;


@RunWith(Arquillian.class)
public class ServiceCreatorTest {
	
    private static Logger LOG = LoggerFactory.getLogger(PodCreatorTests.class); 

	// Obtaining a reference to the Kubernetes client
	@ArquillianResource
	KubernetesClient client;

	// Obtaining a reference to the Services created in this session:
	@ArquillianResource
	Service sessionServices;
	
	@Test
	public void createService()
	{
		String apiVer = "1.0";
		
		Service testSrv = new ProjMayhemService();
		testSrv.setApiVersion(apiVer);
		testSrv.setKind("Service");
		testSrv.s
		
	}
	
	
	//Obtaining a reference to a particular service within this session:
	/*@Id("myNewService")
	@ArquillianResource
	Service myService;
	@Test
	public void myServiceTest(){
	}*/
}
