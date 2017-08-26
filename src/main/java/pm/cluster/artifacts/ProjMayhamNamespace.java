package pm.cluster.artifacts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class provides the ability to create a namespace; kubernetes virtual
 * cluster.
 * 
 * Definition: Kubernetes supports multiple virtual clusters backed by the same
 * physical cluster. These virtual clusters are called namespaces.  MYSOP uses
 * namespaces to manage resource quotas between teams and/or projects.  By default,
 * objects in the same namespace will have the same access control policies.
 * 
 * @author asreitz
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceSpec;
import io.fabric8.kubernetes.api.model.NamespaceStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;

public class ProjMayhamNamespace extends Namespace {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2259057698490908817L;
	private static Logger log = LoggerFactory.getLogger(ProjMayhamNamespace.class);
	private Namespace nasp = null;
	public static final String kind = "Namespace";
	private String apiVersion = "v1";
	KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	/**
	 * Instantiates a new namespace that can be customized and built through this
	 * class interface.
	 */
	public ProjMayhamNamespace() {
		this.nasp = new Namespace();
		this.nasp.setApiVersion(this.apiVersion);
		this.nasp.setKind(kind);
	}

	/**
	 * Instantiates a new namespace based on specific build artifacts.
	 * 
	 * @param apiVer
	 * @param metadata
	 * @param naspSpec
	 * @param naspStatus
	 */
	public ProjMayhamNamespace(String apiVer, ObjectMeta metadata, NamespaceSpec naspSpec, NamespaceStatus naspStatus) {
		this.nasp = new Namespace(apiVer, kind, metadata, naspSpec, naspStatus);

		log.debug("received the follwing for this namespace: \napiVersion:" + apiVer + "\nMetaData: " + metadata
				+ "\namepsapce Spec" + naspSpec.toString() + "\nNamespace status" + naspStatus.getPhase());
	}

	/**
	 * Assigns this class namespace to another existing namespace.
	 */

	public ProjMayhamNamespace(Namespace clientNasp) {
		this.nasp = clientNasp;
	}

	/**
	 * Creates a namespace in the kube client.
	 * 
	 * @return
	 * @throws KubernetesClientException
	 */
	public boolean createNamespace() throws KubernetesClientException {
		boolean created = false;
		if ((nasp.getMetadata() != null) & (nasp.getMetadata().getName() != null)) {
			
			log.info((Boolean.toString(this.doesNamespaceExists(nasp.getMetadata().getName()))));
			if ((this.doesNamespaceExists(nasp.getMetadata().getName())) == false) {

				log.debug("Creating namespace " + nasp.getMetadata().getName());
				kubeCon.namespaces().create(this.nasp);

				// verify the creation of this namespace:
				List<Namespace> kubeNamespaces = kubeCon.namespaces().list().getItems();

				if (kubeNamespaces.contains(nasp)) {
					created = true;
					log.info("Created {} namespace in {} kube cluster", nasp.getMetadata().getName(),
							kubeCon.getConfiguration().getMasterUrl());
				}
			}
		}
		return created;
	}

	/**
	 * Check to see if the namespace that is being requested for creation already
	 * exists.
	 * 
	 * @param ns
	 * @return
	 */
	private boolean doesNamespaceExists(String ns) {
		boolean exists = false;

		List<Namespace> kubeNamespaces = kubeCon.namespaces().list().getItems();

		for (Namespace nsr : kubeNamespaces) {
			log.info("{} namespace", nsr.getMetadata().getName());
			if (nsr.getMetadata().getName().equalsIgnoreCase(ns)) {
				exists = true;
				log.info("{} namespace already exists; no creation needed.", ns);
			}
		}
		return exists;
	}

	/**
	 * Sets this namespace metadata.
	 * 
	 * @param metaData
	 */
	public void setMetaData(ObjectMeta metaData) {
		if ((!(this.nasp == null)) && (this.nasp.getMetadata() == null)) {
			log.info("Setting the {} metadata: ", metaData.getName());
			this.nasp.setMetadata(metaData);
		}
	}

	/**
	 * Sets the namespace spec.
	 * 
	 * @param spec
	 */
	public void setNSSpec(NamespaceSpec spec) {
		if ((!(this.nasp == null)) && (this.nasp.getSpec() == null)) {
			this.nasp.setSpec(spec);
			log.info("Set namespace spec");
		}
	}

	/**
	 * Sets the namespace status.
	 * 
	 * @param status
	 *            - The status of the namespace.
	 */
	public void setNSStatus(NamespaceStatus status) {
		if ((!(this.nasp == null)) && (this.nasp.getStatus() == null)) {
			this.nasp.setStatus(status);
			log.info("Set namespace status");
		}
	}

	/**
	 * providing access to the namespace
	 */
	public Namespace getNamespace() {
		return this.nasp;
	}

	public static void main(String[] args) {
		ProjMayhamNamespace myNS = new ProjMayhamNamespace();
		ObjectMeta nsMD = new ObjectMeta();
		nsMD.setName("curtistestnamespace");

		// adding labels:
		Map<String, String> nsLabels = new HashMap<String, String>();
		nsLabels.put("nsType", "elasticsearch");
		nsLabels.put("nsreason", "test");
		nsMD.setLabels(nsLabels);
		myNS.setMetaData(nsMD);
		myNS.createNamespace();
	}
}
