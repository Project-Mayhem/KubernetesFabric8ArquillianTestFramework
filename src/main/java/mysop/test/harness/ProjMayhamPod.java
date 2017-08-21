package mysop.test.harness;

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

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;

import org.slf4j.Logger; 
import org.slf4j.LoggerFactory;

public class ProjMayhamPod extends Pod{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Pod thePod = null;
	private static final String podKind = "Pod";
	private static Logger LOG = LoggerFactory.getLogger(ProjMayhamPod.class); 

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
