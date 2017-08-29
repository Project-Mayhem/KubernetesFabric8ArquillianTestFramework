package pm.cluster.artifacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
/**
 * This class creates a Kubernetes Pod.  Once the Pod is defined, it is immutable; and
 * the only access to the pod is through this class interface.
 * 
 * Definition:  A Pod is the basic building block of Kubernetes–the smallest and simplest 
 * unit in the Kubernetes object model that you create or deploy. A Pod represents a 
 * running process on your cluster. It encapsulates an application container, storage
 * resources, a unique network IP, and options that govern how it's containers should run. 
 * 
 * @author asreitz
 *
 */
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;

public class ProjMayhamPod extends Pod {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private Pod this = null;
	private static final String podKind = "Pod";
	private String apiVer = "v1";
	private static Logger log = LoggerFactory.getLogger(ProjMayhamPod.class);
	public static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	/**
	 * There are 3 was to construct a pod in this application.
	 * 
	 * @param apiVersion
	 * @param metadata
	 * @param spec
	 * @param status
	 */
	public ProjMayhamPod(String apiVersion, ObjectMeta metadata, PodSpec spec, PodStatus status) {
		super(apiVersion, podKind, metadata, spec, status);
	}

	public ProjMayhamPod(Pod myPod) {
		this();
		super.setMetadata(myPod.getMetadata());
		super.setSpec(myPod.getSpec());
		if (!(myPod.getStatus() == null))
			super.setStatus(myPod.getStatus());
	}

	public ProjMayhamPod() {
		super();
		super.setKind(podKind);
		super.setApiVersion(apiVer);

	}

	/**
	 * Creates a pod based on a pod already being defined.
	 */
	public boolean create() throws KubernetesClientException {
		boolean created = false;
		String myNameSpace = "asreitz-test-podcreation";

		if (!(this.getMetadata() == null) && (!(this.getMetadata().getName() == null))) {
			log.info("Attempting to create pod ****");
			if ((this.doesPodExists(this.getMetadata().getName())) == false) {
				log.info("Creating pod " + this.getMetadata().getName());
				// kubeCon.pods().inNamespace(myNameSpace).create(this);
				kubeCon.pods().create(this);

				List<Pod> pods = kubeCon.pods().list().getItems();
				for (Pod pod : pods) {
					if (pod.getMetadata().getName().equalsIgnoreCase(this.getMetadata().getName())) {
						created = true;
						log.info("{} pod got created", this.getMetadata().getName());
					}
				}
			}
		}
		return created;
	}

	/**
	 * Determining if the Pod already exists:
	 */
	private boolean doesPodExists(String podName) {
		boolean exists = false;
		List<Pod> kubePods = kubeCon.pods().list().getItems();
		if (kubePods.contains(this)) {
			this.log.info("The \"{}\" pod already exists; no creation needed.", podName);
		/*
		 * for (Pod pod : kubePods) { log.info("{} pod", pod.getMetadata().getName());
		 * if (pod.getMetadata().getName().equalsIgnoreCase(podName)) { exists = true;
		 * this.log.info("The \"{}\" pod already exists; no creation needed.", podName);
		 * }
		 */
	}return exists;

	}

	/**
	 * The client can only set the Pod's apiVersion if it isn't already set.
	 * 
	 * @param apiVersion
	 */
	public void setPodApiVersion(String apiVersion) {

		if (super.getApiVersion() == null) {
			super.setApiVersion(apiVersion);
		}
	}

	/**
	 * The client can only set the Pod's metadata if it isn't already set.
	 * 
	 * @param metadata
	 */

	public void setPodMetadata(ObjectMeta metadata) {
		if (super.getMetadata() == null) {
			super.setMetadata(metadata);
		}
	}

	public String toString() {

		String allPodLabels = null;
		String podPrint = null;

		if ((this != null) && (this.getMetadata() != null) && this.getMetadata().getLabels() != null) {
			// pull the list into a string
			Set<Map.Entry<String, String>> labelNames = this.getMetadata().getLabels().entrySet();
			for (Entry<String, String> item : labelNames) {
				allPodLabels += item.getKey() + ":" + item.getValue();
			}
		}
		System.out.println("The labels are: " + allPodLabels);

		if (!(this == null)) {
			podPrint = new String("Pod Name: " + this.getMetadata().getName() + "\nKind: " + podKind + "\nApi Version: "
					+ this.getApiVersion() + "\nLabels: ");
		}
		return podPrint;
	}

	public static void main(String args[]) {
		ProjMayhamPod myPod = new ProjMayhamPod();

		String allPodLabels = null;

		// Create pod labels for the metadata
		Map<String, String> myPodLabels = new HashMap<String, String>();
		myPodLabels.put("test", "myspo");
		myPodLabels.put("developer", "anastasia");

		// create pod spec with containers
		PodSpec myPodSpec = new PodSpec();
		Container myPodCont1 = new Container();
		myPodCont1.setImage("elasticsearch");
		myPodCont1.setImagePullPolicy("IfNotPresent");
		myPodCont1.setName("AnastasiaElasticsearch");
		List<Container> cnList = new ArrayList<Container>();
		cnList.add(myPodCont1);
		myPodSpec.setContainers(cnList);
		myPod.setSpec(myPodSpec);

		ObjectMeta myPodMetaData = new ObjectMeta();
		myPodMetaData.setName("AnastaisaPod");
		myPodMetaData.setNamespace("clusterTesterforPod");
		myPodMetaData.setLabels(myPodLabels);
		myPod.setMetadata(myPodMetaData);

		System.out.println("Pod Name: " + myPod.getMetadata().getName());
		System.out.println("Pod APIVersion: " + myPod.getApiVersion());
		System.out.println("Pod Kind: " + myPod.getKind());

		if ((myPod != null) && (myPod.getMetadata() != null) && myPod.getMetadata().getLabels() != null) {
			// pull the list into a string
			Collection<String> labelNames = myPod.getMetadata().getLabels().values();

			int count = 1;
			for (String item : labelNames) {
				if (count == 1) {
					allPodLabels = item;
					count += 1;
				} else {
					allPodLabels += item;
				}
			}
			System.out.println("Pod labels: " + allPodLabels);
		}

		System.out.println("Pod looks like this:  " + myPod.toString());

	}
}
