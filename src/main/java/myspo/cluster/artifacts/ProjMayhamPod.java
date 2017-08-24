package myspo.cluster.artifacts;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;

public class ProjMayhamPod extends Pod {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Pod thePod = null;
	private static final String podKind = "Pod";
	private static Logger podLog = LoggerFactory.getLogger(ProjMayhamPod.class);
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
		this.thePod = new Pod();
		this.thePod.setApiVersion(apiVersion);
		this.thePod.setKind(podKind);
		this.thePod.setMetadata(metadata);
		this.thePod.setSpec(spec);
		this.thePod.setStatus(status);
	}

	public ProjMayhamPod(Pod myPod) {
		thePod = myPod;

	}

	public ProjMayhamPod() {
		thePod = new Pod();
	}

	/**
	 * Creates a pod based on a pod already being defined.
	 */
	public boolean create() throws KubernetesClientException {
		boolean created = false;

		if ((!(this.thePod == null)) && (!(this.thePod.getMetadata() == null))
				&& (!(this.thePod.getMetadata().getName() == null))) {
			if ((this.doesPodExists(thePod.getMetadata().getName())) == false) {
				podLog.debug("Creating pod " + thePod.getMetadata().getName());
				kubeCon.pods().create(this.thePod);

				List<Pod> pods = kubeCon.pods().list().getItems();
				for (Pod pod : pods) {
					if (pod.getMetadata().getName().equalsIgnoreCase(thePod.getMetadata().getName())) {
						created = true;
						this.podLog.info("{} pod got created", this.thePod.getMetadata().getName());
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

		for (Pod pod : kubePods) {
			podLog.info("{} pod", pod.getMetadata().getName());
			if (pod.getMetadata().getName().equalsIgnoreCase(podName)) {
				exists = true;
				this.podLog.info("The \"{}\" pod already exists; no creation needed.", podName);
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
		if ((thePod.getApiVersion() == null) && (!(this.thePod == null))) {
			thePod.setApiVersion(apiVersion);
		}
	}

	/**
	 * The client can only set the Pod's metadata if it isn't already set.
	 * 
	 * @param metadata
	 */

	public void setPodMetadata(ObjectMeta metadata) {
		if ((thePod.getMetadata() == null) && (!(this.thePod == null))) {
			thePod.setMetadata(metadata);
		}
	}

	/**
	 * The client can only set the Pod's spec if it isn't already set.
	 * 
	 * @param spec
	 */
	public void setPodSpec(PodSpec spec) {
		if ((thePod.getSpec() == null) && (!(this.thePod == null))) {
			thePod.setSpec(spec);
		}
	}

	public void setPodStatus(PodStatus status) {
		if ((thePod.getStatus() == null) && (!(this.thePod == null))) {
			thePod.setStatus(status);
		}
	}

	/**
	 * Providing access to the pod.
	 * 
	 * @return
	 */

	public Pod getPod() {
		return this.thePod;
	}

	/**
	 * Returning the view of all parts of the Pod:
	 */
	public String toString() {
		String allPodLabels = null;

		if (!(this.thePod == null)) {
		//	Map<String, String> podLabels = thePod.getMetadata().getLabels();
			//for (Map.Entry<String, String> entry : podLabels.entrySet()) {
				//allPodLabels = allPodLabels + entry;
			//}
		}
		return new String("Pod Name: " + thePod.getMetadata().getName() + "\nKind: " + podKind + "\nApi Version: "
				+ thePod.getApiVersion() + "\nNamespace: " + thePod.getMetadata().getNamespace() + "\nLabels: "
				+ allPodLabels);
	}
}
