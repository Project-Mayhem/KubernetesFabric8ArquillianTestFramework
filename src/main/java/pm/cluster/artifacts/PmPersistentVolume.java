package pm.cluster.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Creates a PersistentVolume based on client'ss request for storage
 * 
 * e.g., yaml:
 * 
 * apiVersion: v1
  kind: PersistentVolume
  metadata:
    name: pv0003
  spec:
    capacity:
      storage: 5Gi
    accessModes:
      - ReadWriteOnce
    persistentVolumeReclaimPolicy: Recycle
    storageClassName: slow
    nfs:
      path: /tmp
      server: 172.17.0.2
 */
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.HostPathVolumeSource;
import io.fabric8.kubernetes.api.model.NFSVolumeSource;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeSpec;
import io.fabric8.kubernetes.api.model.PersistentVolumeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.utils.KubernetesConnector;

public class PmPersistentVolume extends PersistentVolume {

	private static final long serialVersionUID = -2259057698490908817L;
	private static Logger pvLog = LoggerFactory.getLogger(PmPersistentVolume.class);
	public static final String kind = "PersistentVolume";
	private static final String apiVersion = "v1";
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	private String server = "localhost";

	/**
	 * Constructors provides 4 ways to start creating a PersistentVolume
	 */
	public PmPersistentVolume() {
		super();
		super.setApiVersion(apiVersion);
		super.setKind(kind);
	}

	public PmPersistentVolume(PersistentVolume clientPv) {
		this();
		if (!(clientPv.getMetadata() == null))
			this.setMetadata(clientPv.getMetadata());
		if (!(clientPv.getSpec() == null))
			this.setSpec(clientPv.getSpec());
		if (!(clientPv.getStatus() == null))
			this.setStatus(clientPv.getStatus());
	}

	public PmPersistentVolume(String name) {
		this();
		this.setPVName(name);
	}

	public void setPVName(String name) {
		if (!(this.getMetadata() == null)) {
			this.getMetadata().setName(name);
		} else {
			ObjectMeta metdat = new ObjectMeta();
			metdat.setName(name);
			this.setMetadata(metdat);
		}
	}

	public void setPvMetadata(ObjectMeta metadata) {
		if (this.getMetadata() == null) {
			this.setMetadata(metadata);
		}
	}

	public void setPVSpec(PersistentVolumeSpec pvSpec) {
		if (this.getSpec() == null) {
			this.setSpec(pvSpec);
		}
	}

	public void setPVStatus(PersistentVolumeStatus clientPvStatus) {
		if (this.getStatus() == null) {
			this.setStatus(clientPvStatus);
		}
	}

	/**
	 * For NFS storage type
	 * 
	 * @param path
	 * @param readOnly
	 * @param server
	 * @param capacity
	 * @param reclaimPolicy
	 * @param accessModes
	 * @param storageClass
	 */
	public void setNFS(String path, Boolean readOnly, String server, Map<String, Quantity> capacity,
			String reclaimPolicy, List<String> accessModes, String storageClass) {

		NFSVolumeSource nfs = new NFSVolumeSource(path, readOnly, server);

		// Set Capactiy:
		PersistentVolumeSpec spec = new PersistentVolumeSpec();
		spec.setCapacity(capacity);
		spec.setNfs(nfs);

		// Either "Retained", "Recycled" or "Deleted"
		spec.setPersistentVolumeReclaimPolicy(reclaimPolicy);

		/**
		 * Access Modes options are: "ReadWriteOnce", "ReadOnlyMany", "ReadWriteMany".
		 * On the CLI, these access modes are abbreviated as: RWO, ROX,RWX;
		 * respectively.
		 */
		spec.setAccessModes(accessModes);

		// Assign spec to this object:
		this.setSpec(spec);
	}

	/**
	 * Defines a hostPath volume for use by pods.
	 * 
	 * @param storageClassName
	 * @param capacity
	 * @param accessModes
	 * @param labels
	 * @param reclaimPolicy
	 * @param path
	 */
	public void hostPathPV(String storageClassName, Map<String, Quantity> capacity, List<String> accessModes,
			Map<String, String> labels, String reclaimPolicy, String path) {

		this.getMetadata().setLabels(labels);

		HostPathVolumeSource hpVs = new HostPathVolumeSource(path);
		if (this.getSpec() == null) {
			PersistentVolumeSpec spec = new PersistentVolumeSpec();
			this.setPVSpec(spec);
		}

		if (this.getMetadata() == null) {
			ObjectMeta metadata = new ObjectMeta();
			this.setMetadata(metadata);
		}

		this.getMetadata().setLabels(labels);
		this.getSpec().setHostPath(hpVs);
		this.getSpec().setPersistentVolumeReclaimPolicy(reclaimPolicy);
		this.getSpec().setCapacity(capacity);
		this.getSpec().setAccessModes(accessModes);
		this.getSpec().setAdditionalProperty("storageClassName", new String("manual"));
	}

	/**
	 * Determines if a volume already exists
	 * 
	 * @param volName
	 * @return
	 */
	public static boolean doesVolumeExists(String volName) {
		boolean exists = false;

		List<PersistentVolume> kubeVolumeList = kubeCon.persistentVolumes().list().getItems();
		if (kubeVolumeList != null) {
			for (PersistentVolume kubeVolume : kubeVolumeList) {
				if ((kubeVolume.getMetadata().getName()).equalsIgnoreCase(volName)) {
					pvLog.info("{} volume exists", volName);
					exists = true;
				}
			}
			if (!(exists))
				pvLog.info("{} volume does not exists!", volName);
		}
		return exists;
	}

	public void setVolumeMounts(List<VolumeMount> mounts) {

	}

	/**
	 * Creates a volume disk
	 * 
	 * @return
	 */
	public boolean createPersistentVolume() {
		boolean created = false;
		if ((this.getMetadata() != null) && (this.getMetadata().getName() != null)) {
			{
				if (!(this.doesVolumeExists(this.getMetadata().getName()))) {
					kubeCon.persistentVolumes().create(this);
				}

				// Verify that the volume got created:
				created = this.doesVolumeExists(this.getMetadata().getName());
			}
		}
		return created;
	}

	// ------------- Quick Test:

	public static void main(String args[]) {

		// Instantiate with kind, api, and name
		String pvName = "asreitzpvtest54";
		PmPersistentVolume persVol = new PmPersistentVolume(pvName);

		// Add Labels
		Map<String, String> pvLabels = new HashMap<String, String>();
		pvLabels.put("type", "localhost");
		pvLabels.put("testing", "storage");

		String reclaimPolicy = "Retain";
		String storageClassName = "manual";
		String path = "/tmp/data/tigger";

		Map<String, Quantity> capacities = new HashMap<String, Quantity>();
		capacities.put("storage", new Quantity("6Gi"));

		List<String> accessModes = new ArrayList<String>();
		accessModes.add("ReadWriteMany");

		persVol.hostPathPV(storageClassName, capacities, accessModes, pvLabels, reclaimPolicy, path);

		if (persVol.createPersistentVolume())
			System.out.println(pvName + " is created");
	}
}
