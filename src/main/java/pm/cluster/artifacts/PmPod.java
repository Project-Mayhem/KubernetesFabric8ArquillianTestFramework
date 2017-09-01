package pm.cluster.artifacts;

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
import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import pm.cluster.utils.KubernetesConnector;

public class PmPod extends Pod {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String podKind = "Pod";
	private String apiVer = "v1";
	private static Logger log = LoggerFactory.getLogger(PmPod.class);
	public static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	/**
	 * There are 3 was to construct a pod in this application.
	 * 
	 * @param apiVersion
	 * @param metadata
	 * @param spec
	 * @param status
	 */
	public PmPod(String apiVersion, ObjectMeta metadata, PodSpec spec, PodStatus status) {
		super(apiVersion, podKind, metadata, spec, status);
	}

	public PmPod(Pod myPod) {
		this();
		super.setMetadata(myPod.getMetadata());
		super.setSpec(myPod.getSpec());
		if (!(myPod.getStatus() == null))
			super.setStatus(myPod.getStatus());
	}

	/**
	 * Must set ObjectMeta, PodSpec, and (optionally, PodStatus) when using this
	 * constructor.
	 */
	public PmPod() {
		super();
		super.setKind(podKind);
		super.setApiVersion(apiVer);

	}

	/**
	 * Creates a pod based on a pod already being defined.
	 */
	public boolean create() throws KubernetesClientException {
		boolean created = false;

		if (!(this.getMetadata() == null) && (!(this.getMetadata().getName() == null))) {

			// determining if the pod is already created
			String podName = this.getMetadata().getName();
			log.info("Determining if POd already exists");

			if (!(doesPodExists(podName))) {
				log.info("Creating {} Pod.", podName);
				log.info("Here is the Pod information: \nApiVersion " + this.getApiVersion() + "\nKind "
						+ this.getKind() + "\nPodName: " + this.getMetadata().getName() + "\nNamespace : "
						+ this.getMetadata().getNamespace());

				// get Labels
				String LabelStringList = ",";
				Map<String, String> podLabels = new HashMap<String, String>(this.getMetadata().getLabels());
				for (Map.Entry<String, String> entry : podLabels.entrySet()) {
					LabelStringList += entry.getKey() + ":" + entry.getValue() + ",";
				}

				// get containers
				String contString = ",";
				List<Container> podConList = new ArrayList<Container>(this.getSpec().getContainers());
				for (Container container : podConList) {
					contString += container.getName() + ", ";
				}
				log.info("Labels are: " + podLabels + "\nContainers are: " + contString);
				this.kubeCon.pods().create(this);

				// verify that pod was created
				if (this.doesPodExists(podName))
					created = true;
				log.info("Pod creation verified!");
			}
		}
		return created;
	}

	/**
	 * Determining if the Pod already exists:
	 */
	public static boolean doesPodExists(String podName) {
		log.info("{} is under investigation for existance", podName);
		boolean exists = false;
		List<Pod> kubePods = kubeCon.pods().list().getItems();
		for (Pod pod : kubePods) {
			log.info("{} pod", pod.getMetadata().getName());
			if ((pod.getMetadata().getName()).equalsIgnoreCase(podName)) {
				exists = true;
				log.info("The \"{}\" pod already exists; no creation needed.", podName);
			}
		}
		return exists;
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
		PmPod myPod = new PmPod();

		String allPodLabels = null;
		String ns = "default";

		// Create pod labels for the metadata
		Map<String, String> myPodLabels = new HashMap<String, String>();
		myPodLabels.put("test", "myspo");
		myPodLabels.put("developer", "anastasia");

		// create pod spec with containers
		PodSpec myPodSpec = new PodSpec();
		Container myPodCont1 = new Container();
		Container myPodCont2 = new Container();
		myPodCont1.setImage("elasticsearch");
		myPodCont1.setImagePullPolicy("Always");
		myPodCont1.setName("anastasiaelasticsearch");
		myPodCont2.setImage("mongo");
		myPodCont2.setName("mongodb4asreitz");
		List<Container> cnList = new ArrayList<Container>();
		cnList.add(myPodCont1);
		cnList.add(myPodCont2);
		myPodSpec.setContainers(cnList);
		myPod.setSpec(myPodSpec);

		ObjectMeta myPodMetaData = new ObjectMeta();
		myPodMetaData.setName("tomasmunson");

		// Set Pod's namesapce
		if (PmNamespace.doesNamespaceExists(ns) == false) {
			log.info("*** creating {} namespace", ns);
			PmNamespace myPmNs = new PmNamespace();
			ObjectMeta myPmNsMd = new ObjectMeta();
			myPmNsMd.setName(ns);
			myPmNs.setMetaData(myPmNsMd);
			myPmNs.createNamespace();
		}

		myPodMetaData.setNamespace(ns);
		myPodMetaData.setLabels(myPodLabels);
		myPod.setMetadata(myPodMetaData);

		System.out.println("Pod Name: " + myPod.getMetadata().getName());
		System.out.println("Pod APIVersion: " + myPod.getApiVersion());
		System.out.println("Pod Kind: " + myPod.getKind());

		// Now, create the Pod:
		if (myPod.create()) {
			System.out.println("Pod got created!");
		}
	}
}
