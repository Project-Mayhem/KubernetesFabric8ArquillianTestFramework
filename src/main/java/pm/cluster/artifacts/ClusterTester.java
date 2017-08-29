package pm.cluster.artifacts;

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
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.client.KubernetesClient;

public class ClusterTester {

	// class Logger
	private static final Logger LOG = LoggerFactory.getLogger(ClusterTester.class);
	// Obtaining a reference to the Kubernetes client
	private KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	// Default values
	public String apiVersion = "1.0";

	public void testClientAlive() throws Exception {
		LOG.info("Here is the client's api version" + kubeCon.getApiVersion());
		LOG.info("Here is the kube client's master url: " + kubeCon.getMasterUrl().toString());
		LOG.info("Here is the kube's devault namespace " + kubeCon.getConfiguration().getNamespace());
		NamespaceList nslistObj = kubeCon.namespaces().list();
		List<Namespace> nsList = nslistObj.getItems();
		for (Namespace ns : nsList) {
			LOG.info(ns.getMetadata().getName());
		}
	}

	/**
	 * Testing the deployment of a pod
	 * 
	 * @throws Exception
	 */
	public void podCreation() throws Exception {

		// Define required api version
		String apiVersion = "v1";

		List<Container> podContainers = new ArrayList<Container>();
		Container elasticSearchContainer = new Container();
		elasticSearchContainer.setImage("million12/elasticsearch:latest");
		elasticSearchContainer.setName("elasticsearch-test");
		elasticSearchContainer.setImagePullPolicy("Always");
		podContainers.add(elasticSearchContainer);

		Container nginxContainer = new Container();
		nginxContainer.setImage("nginx");
		nginxContainer.setName("nginx-test");
		nginxContainer.setImagePullPolicy("IfNotPresent");
		//ContainerPort ngxContainerPort = new ContainerPort();
		//ngxContainerPort.setContainerPort(80);
		//List<ContainerPort> ngxContainerPorts = new ArrayList<ContainerPort>();
		//ngxContainerPorts.add(ngxContainerPort);
		//nginxContainer.setPorts(ngxContainerPorts);
		podContainers.add(nginxContainer);

		// -- POD SPEC
		PodSpec podSpec = new PodSpec();
		podSpec.setContainers(podContainers);

		// --Create POD Namespace
		PmNamespace myPodNS = new PmNamespace();

		// Setting pods attributes
		Map<String, String> podLabels = new HashMap<String, String>();
		podLabels.put("podType", "elasticsearch");
		podLabels.put("podreason", "test");
		myPodNS.setApiVersion(apiVersion);
		String myPodNSID = "asreitz-test-podcreation";

		ObjectMeta myPodNSMetDat = new ObjectMeta();
		myPodNSMetDat.setName(myPodNSID);

		// creating labels for the namespace meta data
		Map<String, String> nsMetaDataLabels = new HashMap<String, String>();
		nsMetaDataLabels.put("test", "podCreation");
		nsMetaDataLabels.put("project", "myspo");
		nsMetaDataLabels.put("database", "elasticsearch");
		nsMetaDataLabels.put("loadbalancer", "nginx");
		myPodNSMetDat.setLabels(nsMetaDataLabels);
		myPodNSMetDat.setNamespace(myPodNSID);
		myPodNS.createNamespace();

		// Now for the POD
		LOG.debug("Created the namespace for the pod");

		this.LOG.debug("The {} namespace has {} # of labels ", myPodNS.getNamespace().getMetadata().getName(),
				myPodNS.getNamespace().getMetadata().getLabels().size());

		// --Create POD

		ProjMayhamPod testPod = new ProjMayhamPod();
		//testPod.setKind("Pod");
		testPod.setMetadata(myPodNSMetDat);
		testPod.setSpec(podSpec);
		//testPod.setApiVersion(apiVersion);

		// LOG.info(testPod.toString());

		// Now, lets create the pod:
		try {
			testPod.create();
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("Unable to create Pod due to: " + ex.getMessage());
			LOG.info("Unable to create Pod due to: " + ex.getMessage());
		}

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
			tester.podCreation();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
