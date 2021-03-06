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
import pm.cluster.utils.ConfigFileReader;
import pm.cluster.utils.KubernetesConnector;

public class PmNamespace extends Namespace {

	private static final long serialVersionUID = -2259057698490908817L;
	private static Logger log = LoggerFactory.getLogger(PmNamespace.class);
	public static final String kind = "Namespace";
	private static String apiVersion = "v1";
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	/**
	 * Instantiates a new namespace that can be customized and built through this
	 * class interface.
	 */
	public PmNamespace() {
		super();
		super.setApiVersion(this.apiVersion);
		super.setKind(kind);
	}

	/**
	 * @param name
	 *            The name for this namespace
	 */
	public PmNamespace(String name, Map<String, String> nsLabels) {
		this();
		ObjectMeta metadata = new ObjectMeta();
		metadata.setName(name);
		if (!(nsLabels == null)) {
			metadata.setLabels(nsLabels);
		}
		this.setMetaData(metadata);
	}

	/**
	 * Builds a namespace with values from a config file
	 */
	public PmNamespace(String configFile) {
		Map<String, String> labelRequest = new HashMap<String, String>();
		ConfigFileReader fileReader = new ConfigFileReader();
		Map<String, List<String>> nsConfigs = fileReader.readConfigFile(configFile);
		ObjectMeta metadata = new ObjectMeta();

		if (nsConfigs.containsKey("nsLabels")) {
			List<String> labels = nsConfigs.get("nsLabels");
			for (String label : labels) {
				String[] labelentry = label.split(":");
				log.info("adding {} label", labelentry[0] + " " + labelentry[1]);
				labelRequest.put(labelentry[0].trim(), labelentry[1].trim());
			}
			metadata.setLabels(labelRequest);
		}
		// setting the namespace name:
		List<String> nameList = nsConfigs.get("name");
		// pulling only the first entry; only need one name
		log.info("the pv name is {}", nameList.get(0));
		metadata.setName(nameList.get(0));
		this.setMetaData(metadata);
	}

	/**
	 * Instantiates a new namespace based on specific build artifacts.
	 * 
	 * @param apiVer
	 * @param metadata
	 * @param naspSpec
	 * @param naspStatus
	 */
	public PmNamespace(String apiVer, ObjectMeta metadata, NamespaceSpec naspSpec, NamespaceStatus naspStatus) {
		super(apiVer, kind, metadata, naspSpec, naspStatus);

		log.info("received the follwing for this namespace: \napiVersion:" + apiVer + "\nMetaData: " + metadata
				+ "\namepsapce Spec" + naspSpec.toString() + "\nNamespace status" + naspStatus.getPhase());
	}

	/**
	 * Assigns this class namespace to another existing namespace.
	 */

	public PmNamespace(Namespace clientNasp) {
		this();
		super.setMetadata(clientNasp.getMetadata());
		super.setSpec(clientNasp.getSpec());
		if (clientNasp.getStatus() != null) {
			super.setStatus(clientNasp.getStatus());
		}
	}

	/**
	 * Creates a namespace in the kube client.
	 * 
	 * @return
	 * @throws KubernetesClientException
	 */
	public boolean createNamespace() throws KubernetesClientException {
		boolean created = false;
		if ((this.getMetadata() != null) && (this.getMetadata().getName() != null)) {
			String namespaceName = this.getMetadata().getName();
			log.info("Namespace exists = " + (Boolean.toString(this.doesNamespaceExists(namespaceName)))
					+ " so creating it.");
			if (!(this.doesNamespaceExists(namespaceName))) {
				kubeCon.namespaces().create(this);

				// verify the creation of this namespace:
				if (this.doesNamespaceExists(namespaceName))
					created = true;
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
	public static boolean doesNamespaceExists(String ns) {
		boolean exists = false;

		List<Namespace> kubeNamespaces = kubeCon.namespaces().list().getItems();
		if (!kubeNamespaces.isEmpty()) {
			for (Namespace nsr : kubeNamespaces) {
				log.info("{} namespace", nsr.getMetadata().getName());
				if (nsr.getMetadata().getName().equalsIgnoreCase(ns)) {
					exists = true;
					log.info("{} namespace already exists; no creation needed.", ns);
				}
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
		if ((this.getMetadata() == null)) {
			log.info("Setting the {} metadata: ", metaData.getName());
			this.setMetadata(metaData);
		}
	}
	
	/**
	 * Sets this namesapce's name.
	 * @param name
	 */

	public void setNsName(String name) {
		if (this.getMetadata() == null) {
			ObjectMeta mtdt = new ObjectMeta();
			this.setMetadata(mtdt);
		}
		this.getMetadata().setName(name);
	}

	/**
	 * Sets the namespace spec.
	 * 
	 * @param spec
	 */
	public void setNSSpec(NamespaceSpec spec) {
		if ((this.getSpec() == null)) {
			this.setSpec(spec);
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
		if (this.getStatus() == null) {
			this.setStatus(status);
			log.info("Set namespace status");
		}
	}

	public static void main(String[] args) {
		PmNamespace myNS = new PmNamespace("namespace.config");
		/*
		 * ObjectMeta nsMD = new ObjectMeta(); nsMD.setName("larougenamespace");
		 * 
		 * // adding labels: Map<String, String> nsLabels = new HashMap<String,
		 * String>(); nsLabels.put("nsType", "elasticsearch"); nsLabels.put("nsreason",
		 * "test"); nsMD.setLabels(nsLabels); myNS.setMetaData(nsMD);
		 */
		myNS.createNamespace();
	}
}
