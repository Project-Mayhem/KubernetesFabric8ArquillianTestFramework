package cluster.test.java;

<<<<<<< HEAD
import static io.fabric8.kubernetes.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.fabric8.arquillian.kubernetes.Session;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerList;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.assertions.KubernetesAssert;
import io.fabric8.kubernetes.client.KubernetesClient;
=======
import io.fabric8.kubernetes.client.KubernetesClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import io.fabric8.kubernetes.api.model.ServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.fabric8.kubernetes.api.model.Service;
import static io.fabric8.kubernetes.assertions.Assertions.assertThat;

import javax.inject.Named;

import io.fabric8.kubernetes.api.model.ReplicationControllerList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.arquillian.kubernetes.Session;
>>>>>>> c5475fab16b082b177561e2b8ee27882e2f3c1af

@RunWith(Arquillian.class)
public class ClusterTester {

	// Obtaining a reference to the Kubernetes client
	@ArquillianResource
	KubernetesClient client;
<<<<<<< HEAD
	
	KubernetesAssert kubeAssert = new KubernetesAssert(client);
	
	/**
	 * Test that client is alive and populated by the cluster defined in the arquillian xml file.
	 */
	@Test public void testClientAlive() throws Exception
	{
		System.out.println("Here is the client's api version" + client.getApiVersion());
		System.out.println("Here is the kube client's master url: " + client.getMasterUrl().toString());
		System.out.println("Here is the kube's devault namespace " + client.getConfiguration().getNamespace());
	}

	/**
	 * Testing the deployment of a pod
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPodCreation() throws Exception {

		//For Kubernetes Assertions:
		KubernetesAssert kubeAssrt = new KubernetesAssert(client);
		
		//Define required api version
		String apiVersion = "v1";
		// Create a Namespace:
		Namespace testNS = new NamespaceBuilder().withNewMetadata().withName("anastasiatestnamespace")
				.addToLabels("elasticsearch", "test").endMetadata().build();
		
		Map<String,String> podLabels = new HashMap<String,String>();
		podLabels.put("podType","elasticsearch");
		podLabels.put("podreason","test");
		
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

		ObjectMeta podMetadata = new ObjectMeta();
		String testCreateNS = "asreitz-test-podcreation";
		podMetadata.setName(testCreateNS);
		podMetadata.setNamespace(testNS.getMetadata().getName());
		podMetadata.setLabels(podLabels);
		
		Pod testPod = new Pod();
		testPod.setKind("Pod");
		testPod.setMetadata(podMetadata);
		testPod.setSpec(podSpec);
		testPod.setApiVersion(apiVersion);

		PodBuilder podBld = new PodBuilder(testPod);
		podBld.build();
		
		//Asserts that pods become ready and continues to be ready.
		assertThat(client).deployments().pods().isPodReadyForPeriod();
		
		//Aserts that the namespace if valid:
		assertThat(client,testCreateNS);
		
		//Asserts that the total number of pods were created:
		assertThat(client).deployments().pods().getPods().size();
		
		//Asserts that the kubeclient has deployments
		kubeAssrt.deployment(nginxContainer.getName());
		
	}


	/**
	 * Asserts that the provided pod is found.
	 * 
	 * @throws Exception
	 */

	@Test
	public void testPodExists() throws Exception {
		
		String podId = "", podNamespace = "", podName = "oraclelinux-573370123-2dhzk";
		kubeAssert.pod(podName);
	}
=======
>>>>>>> c5475fab16b082b177561e2b8ee27882e2f3c1af

	/**
	 * Asserts that at least one pod gets created, that it becomes Ready within a
	 * time period (30 seconds by default), then that the pod keeps being Ready for
	 * a period (defaults to 10 seconds).
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRunningPodStaysUp() throws Exception {
		assertThat(client).deployments().pods().isPodReadyForPeriod();

	}

	/*
	 * /** Test that this session has at least 1 namespace.
	 */
	@ArquillianResource
	Session session;
<<<<<<< HEAD

=======
>>>>>>> c5475fab16b082b177561e2b8ee27882e2f3c1af
	@Test
	public void testAtLeastOnePod() throws Exception {
		assertThat(client).pods().runningStatus().filterNamespace(session.getNamespace()).hasSize(1);
	}

	/**
	 * To obtain a list of all services created in the current session.
	 */
	@ArquillianResource
	ServiceList sessionServices;

<<<<<<< HEAD
	public void servicesAlive() throws Exception {
		kubeAssert.services().hasToString("hello-minikube");
	}

=======
>>>>>>> c5475fab16b082b177561e2b8ee27882e2f3c1af
	/**
	 * To obtain a record of a particular service that was created in the current
	 * session.
	 */
<<<<<<< HEAD

=======
	
>>>>>>> c5475fab16b082b177561e2b8ee27882e2f3c1af
	@ArquillianResource
	@Named("my-service")
	Service myService;

	/**
	 * To obtain the list of all replication controllers created in the current
	 * session:
	 * 
	 */
	@ArquillianResource
	ReplicationControllerList sessionControllers;

	/**
	 * To obtain a refernce to a particular replication controller created in the
	 * current session:
	 * 
	 */
<<<<<<< HEAD

=======
	
>>>>>>> c5475fab16b082b177561e2b8ee27882e2f3c1af
	@ArquillianResource
	ReplicationController myController;

	/**
	 * To obtain the list of all pods created in the current session:
	 * 
	 */
	@ArquillianResource
	PodList sessionPods;

	/**
	 * To obtain the session.
	 * 
	 */

	@ArquillianResource
	Session mySession;

<<<<<<< HEAD
	
	public static void main(String[] args)
	{
		ClusterTester tester = new ClusterTester();
		try {
			tester.testClientAlive();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
=======
>>>>>>> c5475fab16b082b177561e2b8ee27882e2f3c1af
}
