package mysop.cluster.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ClusterTester {

	//class Logger
	private static Logger LOG = LoggerFactory.getLogger(ClusterTester.class);
	// Obtaining a reference to the Kubernetes client
	private KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	//Default values
	public String apiVersion = "1.0";
	


	public void testClientAlive() throws Exception {
		LOG.debug("Here is the client's api version" + kubeCon.getApiVersion());
		LOG.debug("Here is the kube client's master url: " + kubeCon.getMasterUrl().toString());
		LOG.debug("Here is the kube's devault namespace " + kubeCon.getConfiguration().getNamespace());
	}

	/**
	 * Testing the deployment of a pod
	 * 
	 * @throws Exception
	 */
	public void PodCreation() throws Exception {

		// Define required api version
		String apiVersion = "v1";
		// Create a Namespace:
		Namespace testNS = new NamespaceBuilder().withNewMetadata().withName("anastasiatestnamespace")
				.addToLabels("elasticsearch", "test").endMetadata().build();

		Map<String, String> podLabels = new HashMap<String, String>();
		podLabels.put("podType", "elasticsearch");
		podLabels.put("podreason", "test");

		List<Container> podContainers = new ArrayList<Container>();
		Container elasticSearchContainer = new Container();
		elasticSearchContainer.setImage("million12/elasticsearch:latest");
		elasticSearchContainer.setName("elasticsearch-test");
		podContainers.add(elasticSearchContainer);

		Container nginxContainer = new Container();
		nginxContainer.setImage("nginx");
		nginxContainer.setName("nginx-test");
		ContainerPort ngxContainerPort = new ContainerPort();
		ngxContainerPort.setContainerPort(80);
		List<ContainerPort> ngxContainerPorts = new ArrayList<ContainerPort>();
		ngxContainerPorts.add(ngxContainerPort);
		nginxContainer.setPorts(ngxContainerPorts);
		podContainers.add(nginxContainer);

		PodSpec podSpec = new PodSpec();
		podSpec.setContainers(podContainers);

		String myPodNSID = "asreitz-test-podcreation";
		
		//--Create POD Namespace 
		//Now create the Namespace so that it can be used:
		ProjMayhamNamespace myPodNS = new ProjMayhamNamespace(); 
		
		ObjectMeta myPodNSMetDat = new ObjectMeta();
		myPodNSMetDat.setName(myPodNSID);
		//creating labels for the namespace meta data
		Map<String,String> nsMetaDataLabels = new HashMap<String,String>();
		nsMetaDataLabels.put("test","podCreation");
		nsMetaDataLabels.put("project", "myspo");
		
		myPodNSMetDat.setLabels(nsMetaDataLabels);
	
		myPodNSMetDat.setLabels(podLabels);
		myPodNS.setMetaData(myPodNSMetDat);
		kubeCon.namespaces().create(myPodNS);
		LOG.debug("Created the namespace for the pod");
		
		

		//--Create POD
		ProjMayhamPod testPod = new ProjMayhamPod();
		testPod.setKind("Pod");
		testPod.setMetadata(myPodNSMetDat);
		testPod.setSpec(podSpec);
		testPod.setApiVersion(apiVersion);

		kubeCon.pods().create(testPod);

		// Log if the Pod got created:
		PodList podList = kubeCon.pods().list();
		List<Pod> pods = podList.getItems();
		for (Pod pea : pods) {
			if (pea.getMetadata().getName().equalsIgnoreCase(testPod.getMetadata().getName()))
				LOG.debug(testPod.getMetadata().getClusterName() + "pod was created.");
		}
	}

	public static void main(String[] args) {
		ClusterTester tester = new ClusterTester();
		try {
			tester.testClientAlive();
			tester.PodCreation();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
