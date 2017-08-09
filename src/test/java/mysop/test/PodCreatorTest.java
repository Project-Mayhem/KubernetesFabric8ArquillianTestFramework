package mysop.test;

/**
 * Testing the creation of Pods on the cluster.  Sets up a Pod with containers and tests it.
 * @author asreitz
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.arquillian.kubernetes.Session;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.assertions.KubernetesAssert;
import io.fabric8.kubernetes.client.KubernetesClient;
import mysop.test.harness.PodCreator;
import static io.fabric8.kubernetes.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class PodCreatorTest {

	// Obtaining a reference to the Kubernetes client
	@ArquillianResource
	KubernetesClient client;

	//KubernetesAssert kubeAssert = new KubernetesAssert(client);
	
	// Obtaining a reference to the Services created in this session:
	@ArquillianResource
	Service sessionServices;
	
	//Obtaining a handle to this Kube's Replication Controllers:
	@ArquillianResource
	ReplicationController repCtrl;
	
	//Obtaining a handle to this Kube's Pods:
	@ArquillianResource
	PodList sessionPods;
	
	//Obtaining a handle to this Kube's Session:
	@ArquillianResource
	Session kubeSession;
	
	//Obtaining a reference to a particular service within this session:
	/*@Id("myNewService")
	@ArquillianResource
	Service myService;*/
	
	//Obtaining a reference to a particular replication controller:
	/*@Id("myReplicationController")
	@ArquillianResource
	ReplicationController myRepCtrl;*/
	

	@Test
	public void testPodCreation() {
		String apiVersion = "v1";
		String kind = "Pod";
		String podName = "DatabasePod";
		Map<String,String> podLables= new HashMap<String,String>();
		
		podLables.put("test","clusterTest");
		podLables.put("databaseType","NoSql");
		
		//Setting up the first container
		Container container1 = new Container();
		container1.setImage("/mraad/accumulo");
		container1.setImagePullPolicy("Always");
		
		ContainerPort container1port = new ContainerPort();
		container1port.setProtocol("TCP");
		container1port.setHostPort(9999);
		container1port.setName("accumuloMasterPort");
		container1port.setContainerPort(9999);
		
		ContainerPort container2port = new ContainerPort();
		container2port.setProtocol("TCP");
		container2port.setHostPort(9997);
		container2port.setName("accumuloTabletPort");
		container2port.setContainerPort(9997);
		
		ContainerPort container3port = new ContainerPort();
		container3port.setProtocol("TCP");
		container3port.setHostPort(50095);
		container3port.setName("accumuloMonitorPort");
		container3port.setContainerPort(50095);
		
		ContainerPort container4port = new ContainerPort();
		container4port.setProtocol("TCP");
		container4port.setHostPort(50091);
		container4port.setName("accumuloGarbageCollectorPort");
		container4port.setContainerPort(50091);
		
		ContainerPort container5port = new ContainerPort();
		container5port.setProtocol("TCP");
		container5port.setHostPort(12234);
		container5port.setName("accumuloTracerPort");
		container5port.setContainerPort(12234);
		
		List<ContainerPort> accumuloPorts = new ArrayList<ContainerPort>();
		accumuloPorts.add(container1port);
		accumuloPorts.add(container2port);
		accumuloPorts.add(container3port);
		accumuloPorts.add(container4port);
		accumuloPorts.add(container5port);
		container1.setPorts(accumuloPorts);
		
		Container container2 = new Container();
		container2.setImage("/million12/elasticsearch");
		
		Container container3 = new Container();
		container3.setImage("/mongo");
		container3.setImagePullPolicy("Always");
		
		
		//Pod's spec information that defines the docker containers:
		PodSpec podSpec = new PodSpec();
		List<Container> containerList = new ArrayList<Container>();
		containerList.add(container1);
		containerList.add(container2);
		containerList.add(container3);
		podSpec.setContainers(containerList);
		
		
		//Pod's Objectmetadata:
		ObjectMeta podMetadata = new ObjectMeta();
		podMetadata.setName(podName);
		podMetadata.setLabels(podLables);
		
		Pod myPod = new Pod();
		myPod.setApiVersion(apiVersion);
		myPod.setKind(kind);
		myPod.setMetadata(podMetadata);
		myPod.setSpec(podSpec);
		
		PodCreator testPod = new PodCreator(myPod);
		testPod.create();
		
		//Now did the Pod get created?

	}

}
