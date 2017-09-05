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
import pm.cluster.utils.KubernetesConnector;

public class PmPersistentVolumeClaim extends PersistentVolumeClaim {

	private static Logger pvcLog = LoggerFactory.getLogger(PmPersistentVolumeClaim.class);
	public static final String kind = "PersistentVolumeClaim";
	private static final String apiVersion = "v1";
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	/**
	 * Convenience constructors:
	 */

	public PmPersistentVolumeClaim() {
		super();
		this.setKind(kind);
		this.setApiVersion(apiVersion);
	}

	public PmPersistentVolumeClaim(String name) {
		this();
		ObjectMeta metadata = new ObjectMeta();
		metadata.setName(name);
		this.setMetadata(metadata);
	}

	public PmPersistentVolumeClaim(ObjectMeta metadata, PersistentVolumeClaimSpec spec) {
		this();
		this.setMetadata(metadata);
		this.setSpec(spec);
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
	public void setPvcSpec(String storageclassname, List<String> accessModes, Map<String, Quantity> capacity,
			String specName) {
		if (this.getSpec() == null) {
			PersistentVolumeClaimSpec specpv = new PersistentVolumeClaimSpec();
			this.setSpec(specpv);
		}
		this.getSpec().setVolumeName(specName);
		this.getSpec().setAccessModes(accessModes);
		this.getSpec().setAdditionalProperty("storageClassName", storageclassname);

		ResourceRequirements resReq = new ResourceRequirements();
		resReq.setLimits(capacity);
		this.getSpec().setResources(resReq);
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
						+ this.getSpec().getVolumeName() +
						"\nnamespace: " + this.getMetadata().getNamespace());
				
				List<String> accesses = this.getSpec().getAccessModes();
				for(String access: accesses) {
					pvcLog.info("{} access: ", access);
				}
				Map<String,Quantity> qtList = new HashMap<String,Quantity>();
				for(Map.Entry<String,Quantity> qt: qtList.entrySet()) {
				pvcLog.info("{} is the capcity", qt);
				}

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

		String pvcName = "testPvcClaim1";
		String storageClassName = "manual";
		String namespace = "default";
		String specName = "pvcTester1";

		List<String> accessModes = new ArrayList<String>();
		accessModes.add("ReadWriteMany");

		Map<String, Quantity> capacity = new HashMap<String, Quantity>();
		capacity.put("storage", new Quantity("3GI"));

		PmPersistentVolumeClaim pvc = new PmPersistentVolumeClaim(pvcName);
		pvc.setPvcSpec(storageClassName, accessModes, capacity, specName);
		pvc.getMetadata().setNamespace(namespace);
		pvc.createPersistentVolumeClaim();

	}
}
