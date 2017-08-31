package pm.cluster.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.utils.KubernetesConnector;

public class FIOPod {

	
	private static final long serialVersionUID = 1L;
	private static final String podKind = "Pod";
	private static Logger log = LoggerFactory.getLogger(FIOPod.class);
	public static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	
	//private ProjMayhamPod pod = null;
	private static String apiVer = "v1";

	public FIOPod () {
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		log.debug("Creating FIOPod");
		PmPod pod = new PmPod();
		
		//Create pod labels for the metadata
		Map<String, String> myPodLabels = new HashMap<String, String>();
		myPodLabels.put("test","pm");
		myPodLabels.put("developer","testHarness");
		
		//create pod spec with containers
		PodSpec podSpec = new PodSpec();
		Container myPodCont1 = new Container();
		myPodCont1.setImage("datawiseio/fio:v0.3");
		myPodCont1.setImagePullPolicy("IfNotPresent");
		myPodCont1.setName("fio");
		List<Container> cnList = new ArrayList<Container>();
		cnList.add(myPodCont1);
		podSpec.setContainers(cnList);
		pod.setSpec(podSpec);
		
		// Create the pod metadata
		ObjectMeta myPodMetaData = new ObjectMeta();
		myPodMetaData.setName("fio-test-pod");
		myPodMetaData.setNamespace("default");
		myPodMetaData.setLabels(myPodLabels);
		pod.setMetadata(myPodMetaData);
		
		
		Map<String, String> m = new HashMap<String, String>();
		
		
		// Create the pod
		Boolean created = pod.create();
		if(!created) 
			log.error("No pod created!");
		log.debug("Namespaces: " + kubeCon.getNamespace());
	}
	
	
}
