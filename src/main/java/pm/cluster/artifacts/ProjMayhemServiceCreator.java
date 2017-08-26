package myspo.cluster.artifacts;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.ServiceStatus;

/**
 * This class will test the creation and validation of a Kubernetes service.
 * 
 * Definition of a Kubernetes Service: A Kubernetes Service is an abstraction
 * which defines a logical set of Pods and a policy by which to access them -
 * sometimes called a micro-service.
 * 
 * The service object that gets created is encapsulated in this class and can
 * only be manipulated through the class interface. Outside of being able to
 * reset this service's status, this service is immutable.
 * 
 * @author asreitz
 *
 */

public class ProjMayhemServiceCreator {

	private Service service = null;
	private static String kubeKind = "Service";

	/**
	 * These constructors provide 3 ways to create a mysop kubernetes service.
	 */

	public ProjMayhemServiceCreator() {
		this.service = new Service();
	}

	public ProjMayhemServiceCreator(String apiVer, ObjectMeta serviceMetadata, ServiceSpec serviceSpec,
			ServiceStatus serviceStatus) {
		this.service = new Service(apiVer, kubeKind, serviceMetadata, serviceSpec, serviceStatus);

	}

	public ProjMayhemServiceCreator(Service srv) {
		this.service = srv;
	}

	/**
	 * Creates the defined service and deploys it.
	 */
	public void createThisSrv() {
		if (!this.service.equals(null)) {
			ServiceBuilder srvBld = new ServiceBuilder(this.service);
			srvBld.build();
		}
	}

	/**
	 * Only able to set the service's api version if it hasn't been set.
	 * 
	 * @param apiVer
	 *            - The proposed service api version to set.
	 */
	public void setSrvApi(String apiVer) {
		if ((service.getApiVersion().equals(null)) && (!this.service.equals(null))) {
			service.setApiVersion(apiVer);
		}
	}

	/**
	 * Only able to set the service's metadata if it hasn't already been set.
	 * 
	 * @param metadata
	 *            - The proposed service metadata to set.
	 */

	public void setSrvObjectMetaData(ObjectMeta metadata) {
		if ((service.getMetadata().equals(null)) && (!this.service.equals(null))) {
			this.service.setMetadata(metadata);
		}
	}

	/**
	 * Only able to set the service's spec section if it isn't already defined.
	 * 
	 * @param -
	 *            serviceSpec is the ServiceSpec object for the service to be set.
	 */
	public void setSrvServiceSpec(ServiceSpec srvSpc) {
		if ((this.service.getSpec().equals(null)) && (!this.service.equals(null))) {
			this.service.setSpec(srvSpc);
		}
	}

	/**
	 * Sets or resets the service status
	 * 
	 * @param srvStatus
	 *            - sets the service status
	 */
	public void setSrvStatus(ServiceStatus status) {
		if (!this.service.equals(null)) {
			this.service.setStatus(status);
		}
	}
}
