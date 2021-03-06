package pm.cluster.artifacts;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceBuilder;
import io.fabric8.kubernetes.api.model.ServiceFluentImpl;
import io.fabric8.kubernetes.api.model.ServiceSpec;
import io.fabric8.kubernetes.api.model.ServiceStatus;
import io.fabric8.kubernetes.api.model.StatusBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.utils.KubernetesConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will test the creation and validation of a Kubernetes service.
 * 
 * Definition of a Kubernetes Service: A Kubernetes Service is an abstraction
 * which defines a logical set of Pods and a policy by which to access them -
 * sometimes called a micro-service.
 * 
 * Types of Services that can be created: - clusterip - externalname -
 * loadbalancer - nodeport
 * 
 * @author asreitz
 *
 */

public class PmService extends Service {

	private static final String kubeKind = "Service";
	private static final String apiVersion = "v1";
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	private static Logger LOG = LoggerFactory.getLogger(PmService.class);

	/**
	 * These constructors provide 3 ways to create a mysop kubernetes service.
	 */

	public PmService() {
		super();
		this.setApiVersion(apiVersion);
		this.setKind(kubeKind);
	}

	public PmService(String apiVer, ObjectMeta serviceMetadata, ServiceSpec serviceSpec, ServiceStatus serviceStatus) {
		super(apiVer, kubeKind, serviceMetadata, serviceSpec, serviceStatus);

	}

	public PmService(Service srv) {
		this();
		this.setMetadata(srv.getMetadata());
		this.setSpec(srv.getSpec());
		if (!(this.getStatus() == null)) {
         this.setStatus(srv.getStatus());
		}
	}

	/**
	 * Creates the defined service and deploys it.
	 */
	/*public void createThisSrv() {
		if (!(this.getMetadata()==null)) &&  {

			//determine if the service is already created
		}
	}

	/**
	 * Only able to set the service's api version if it hasn't been set.
	 * 
	 * @param apiVer
	 *            - The proposed service api version to set.
	 */
	/*public void setSrvApi(String apiVer) {
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

	/*public void setSrvObjectMetaData(ObjectMeta metadata) {
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
/*	public void setSrvServiceSpec(ServiceSpec srvSpc) {
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
/*	public void setSrvStatus(ServiceStatus status) {
		if (!this.service.equals(null)) {
			this.service.setStatus(status);
		}
	}*/
}
