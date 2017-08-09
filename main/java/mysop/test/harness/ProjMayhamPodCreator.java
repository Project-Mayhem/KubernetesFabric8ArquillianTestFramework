package mysop.test.harness;

/**
 * This class creates a Kubernetes Pod.  Once the Pod is defined, it is immutable; and
 * the only access to the pod is through this class interface.
 * 
 * @author asreitz
 *
 */

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;

public class ProjMayhamPodCreator {

	private Pod thePod = null;
	private static String podKind = "Pod";

	/**
	 * There are 3 was to construct a pod in this application.
	 * @param apiVersion
	 * @param metadata
	 * @param spec
	 * @param status
	 */
	public ProjMayhamPodCreator(String apiVersion, ObjectMeta metadata, PodSpec spec, PodStatus status) {
		this.thePod = new Pod();
		this.thePod.setApiVersion(apiVersion);
		this.thePod.setKind(podKind);
		this.thePod.setMetadata(metadata);
		this.thePod.setSpec(spec);
		this.thePod.setStatus(status);
	}

	public ProjMayhamPodCreator(Pod myPod) {
		thePod = myPod;

	}

	public ProjMayhamPodCreator() {
		thePod = new Pod();
	}

	/**
	 * Creates a pod based on a pod already being defined.
	 */
	public void create() {
		if (!this.thePod.equals(null)) {
			PodBuilder builder = new PodBuilder(thePod);
			builder.build();
		}
	}

	/**
	 * The client can only set the Pod's apiVersion if it isn't already set.
	 * 
	 * @param apiVersion
	 */
	public void setPodApiVersion(String apiVersion) {
		if ((thePod.getApiVersion().equals(null)) && (!this.thePod.equals(null))) {
			thePod.setApiVersion(apiVersion);
		}
	}

	/**
	 * The client can only set the Pod's metadata if it isn't already set.
	 * 
	 * @param metadata
	 */

	public void setPodMetadata(ObjectMeta metadata) {
		if ((thePod.getMetadata().equals(null)) && (!this.thePod.equals(null))) {
			thePod.setMetadata(metadata);
		}
	}

	/**
	 * The client can only set the Pod's spec if it isn't already set.
	 * 
	 * @param spec
	 */
	public void setPodSpec(PodSpec spec) {
		if ((thePod.getSpec().equals(null)) && (!this.thePod.equals(null))) {
			thePod.setSpec(spec);
		}
	}

	public void setPodStatus(PodStatus status) {
		if ((thePod.getStatus().equals(null)) && (!this.thePod.equals(null))) {
			thePod.setStatus(status);
		}
	}

}
