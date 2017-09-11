package pm.cluster.artifacts;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Anastasia
 * 
 * Requests and creates a persistentvolume claim to the Kubernetes cluster.
 * 
 */

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.utils.ConfigFileReader;
import pm.cluster.utils.KubernetesConnector;

public class PmPersistentVolumeClaim extends PersistentVolumeClaim {

	private static Logger pvcLog = LoggerFactory.getLogger(PmPersistentVolumeClaim.class);
	public static final String kind = "PersistentVolumeClaim";
	private static final String apiVersion = "v1";
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	private Map<String, List<String>> configs = null;

	/**
	 * Convenience constructors:
	 */

	public PmPersistentVolumeClaim() {
		super();
		this.setKind(kind);
		this.setApiVersion(apiVersion);
	}

	public PmPersistentVolumeClaim(String configFileName) {
		ConfigFileReader confReader = new ConfigFileReader();
		this.configs = confReader.readConfigFile(configFileName);
		this.mapConfigs();
	}

	public PmPersistentVolumeClaim(ObjectMeta metadata, PersistentVolumeClaimSpec spec) {
		this();
		this.setMetadata(metadata);
		this.setSpec(spec);
	}

	private void mapConfigs() {
		Map<String, Quantity> resourceRequests = new HashMap<String, Quantity>();
		String provisioner = null, hostDir = null, storageClassName = null, reclaimPolicy = null;
		List<String> accessModesList = new ArrayList<String>();
		Map<String, String> pvLabels = new HashMap<String, String>();

		if (!(this.configs == null)) {
			for (Map.Entry<String, List<String>> config : configs.entrySet()) {
				switch (config.getKey()) {
				case "name":
					List<String> name = config.getValue();
					this.setPVCName(name.get(0).trim()); // Only pulling the first name; only need one!
					break;
				case "resources": // may be a list of resources
					List<String> resourcesList = config.getValue();
					for (String resource : resourcesList) {
						String[] resReqs = resource.split(":");
						pvcLog.debug("key value is : ", resReqs[0]);
						pvcLog.debug("value is : ", resReqs[1]);
						resourceRequests.put(resReqs[0].trim(), new Quantity(resReqs[1].trim()));
					}
					break;
				case "storageClassName": // only one; pulling first one
					List<String> storageClassNameList = config.getValue();
					storageClassName = storageClassNameList.get(0).trim();
					break;
				case "accessModes": // can be multiple
					accessModesList = config.getValue();
					break;
				case "labels":
					List<String> labelList = config.getValue();
					for (String labelEntry : labelList) {
						String[] set = labelEntry.split(":");
						pvLabels.put(set[0].trim(), set[1].trim());
						if (this.getMetadata() == null) {
							ObjectMeta mtdt = new ObjectMeta();
							this.setMetadata(mtdt);
						}
						this.getMetadata().setLabels(pvLabels);
						break;
					}
				}
			}
		} else {
			pvcLog.info("Config file has not been provided; no configs available!");
		}

		this.setPvcSpec(storageClassName, accessModesList, resourceRequests);

	}

	/**
	 * Setting the PVC spec.
	 * 
	 * @param spec
	 */
	public void setPvcSpec(PersistentVolumeClaimSpec spec) {
		this.setSpec(spec);
	}

	/**
	 * Overloading to provide the user to supply the minimally required data for a
	 * PersistentVolumeClaim.
	 * 
	 * @param storageclassname
	 * @param accessModes
	 * @param capacity
	 */
	// public void setPvcSpec(String storageclassname, List<String> accessModes,
	// String typeReq, String capacity) {
	public void setPvcSpec(String storageclassname, List<String> accessModes, Map<String, Quantity> resources) {
		PersistentVolumeClaimSpec specpv = null;

		if (this.getSpec() == null) {
			specpv = new PersistentVolumeClaimSpec();
			this.setSpec(specpv);
		}

		// set the access mode
		this.getSpec().setAccessModes(accessModes);

		// set the storage requests
		// Map<String, Quantity> storageReq = new HashMap<String, Quantity>();
		// storageReq.put(typeReq, new Quantity(capacity));
		ResourceRequirements resReq = new ResourceRequirements();
		resReq.setRequests(resources);
		this.getSpec().setResources(resReq);
		this.setSpec(specpv);

		pvcLog.info("" + this.getMetadata().getName() + "\nAPI Version: " + this.getApiVersion() + "\nKibnd: "
				+ this.getKind() + "\n  " + this.getSpec());
	}

	/**
	 * Sets the Pvc name.
	 * 
	 * @param pvcName
	 *            The requested name for the pvc.
	 */
	public void setPVCName(String pvcName) {
		if ((this.getMetadata() != null) && (this.getMetadata().getName() == null)) {
			this.getMetadata().setName(pvcName);
		} else {
			ObjectMeta metadata = new ObjectMeta();
			metadata.setName(pvcName);
			this.setMetadata(metadata);
		}
	}

	/**
	 * Setting the PVC metadata object.
	 * 
	 * @param meta
	 */
	public void setPVCMetadata(ObjectMeta meta) {
		this.setMetadata(meta);
	}

	/**
	 * Determines if the PersistentVolumeClaim already exists.
	 * 
	 * @param volName
	 * @return
	 */
	public static boolean doesPVClaimExists(String volName) {
		boolean exists = false;

		if ((kubeCon.persistentVolumeClaims().list().getItems()) != null) {
			List<PersistentVolumeClaim> kubeVolumeList = kubeCon.persistentVolumeClaims().list().getItems();
			for (PersistentVolumeClaim kubeVolume : kubeVolumeList) {
				if ((kubeVolume.getMetadata().getName()).equalsIgnoreCase(volName)) {
					pvcLog.info("{} persiseent volume claim exists", volName);
					exists = true;
				}
			}
			if (!(exists))
				pvcLog.info("{} persistent volume claim does not exists!", volName);
		}
		return exists;
	}

	public boolean createPersistentVolumeClaim() {
		boolean created = false;
		// kubeCon.persistentVolumeClaims().create(this);
		// created = true;

		if ((this.getMetadata().getName() != null) && (this.getSpec() != null)) {
			if (!(this.doesPVClaimExists(this.getMetadata().getName()))) {
				pvcLog.info("The PVC looks like this: " + "\napiVerion: " + this.getApiVersion() + "\nkind: "
						+ this.getKind() + "\nName:" + this.getMetadata().getName() + "\nVolume Name: "
						+ this.getSpec().getVolumeName() + "\nnamespace: " + this.getMetadata().getNamespace());

				List<String> accesses = this.getSpec().getAccessModes();
				for (String access : accesses) {
					pvcLog.info("{} access: ", access);
				}
				Map<String, Quantity> qtList = new HashMap<String, Quantity>();
				for (Map.Entry<String, Quantity> qt : qtList.entrySet()) {
					pvcLog.info("{} is the capcity", qt);
				}
				this.getMetadata().setNamespace("default");
				;
				kubeCon.persistentVolumeClaims().create(this);
			}
		}

		// Verify that pvc got created:
		List<PersistentVolumeClaim> pvcList = kubeCon.persistentVolumeClaims().list().getItems();
		for (PersistentVolumeClaim claim : pvcList) {
			if ((claim.getMetadata().getName()).equalsIgnoreCase(this.getMetadata().getName())) {
				created = true;
			}
		}
		return created;
	}

	// quick test
	public static void main(String args[]) {
/*
		String storageClassName = "standard";
		String claimName = "anastasiapvclaim130claim";
		String resourceReqType = "storage";
		String storageCapacityValue = "13Gi";
		List<String> accessModes = new ArrayList<String>();
		accessModes.add("ReadWriteMany");

		Map<String, Quantity> capacity = new HashMap<String, Quantity>();
		capacity.put("storage", new Quantity(storageCapacityValue));

		// PmPersistentVolumeClaim pvc = new PmPersistentVolumeClaim(pvcName);
		PmPersistentVolumeClaim pvc = new PmPersistentVolumeClaim();
		pvc.setPVCName(claimName);
		pvc.setPvcSpec(storageClassName, accessModes, resourceReqType, storageCapacityValue);
		pvc.createPersistentVolumeClaim(); */
		String confsFileName="persistentVolumeClaim.config";
		PmPersistentVolumeClaim pvc = new PmPersistentVolumeClaim(confsFileName);

	}
}
