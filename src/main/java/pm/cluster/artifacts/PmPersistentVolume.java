package pm.cluster.artifacts;

import java.io.File;
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
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.NFSVolumeSource;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeSpec;
import io.fabric8.kubernetes.api.model.PersistentVolumeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.utils.ConfigFileReader;
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

	public PmPersistentVolume(File resourceFile) {
		this();
		ConfigFileReader reader = new ConfigFileReader();
		pvLog.info("{} is the name of resource file.", resourceFile.getName());
		Map<String, List<String>> pvConfigs = reader.readConfigFile(resourceFile.getName());
		HashMap<String, List<String>> mapper = new HashMap<String, List<String>>(pvConfigs);
		ObjectMeta metaData = new ObjectMeta();
		PersistentVolumeSpec pvSpec = new PersistentVolumeSpec();
		this.setSpec(pvSpec);

		for (Entry<String, List<String>> entry : pvConfigs.entrySet()) {
			// switch(entry.getKey()) { case "name": this.setMetadata(entry.getValue()); { }

			switch (entry.getKey()) {
			case "name":
				List<String> nameList = entry.getValue();
				for (String name : nameList) {
					metaData.setName(name);
					this.setMetadata(metaData);
				}
				break;
			case "capacityStorage":
				List<String> capacityStorList = entry.getValue();
				Map<String, Quantity> myStorage = new HashMap<String, Quantity>();
				for (String storage : capacityStorList) {
					myStorage.put("storage", new Quantity(storage));
				}
				pvSpec.setCapacity(myStorage);
			case "accessMode":
				List<String> modes = entry.getValue();
				pvSpec.setAccessModes(modes);
				break;
			case "nfs":
				NFSVolumeSource nfsVS = new NFSVolumeSource();
				nfsVS.setPath("/tmp");
				nfsVS.setServer("localhost");
				this.getSpec().setNfs(nfsVS);
			    break;
			case "persistentVolumeReclaimPolicy":
				List<String> policyStrings = entry.getValue();
				for(String reclaimPol:policyStrings) {
				pvSpec.setPersistentVolumeReclaimPolicy(reclaimPol);
				}
			}
		}
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
	 * Determines if a volume already exists
	 * 
	 * @param volName
	 * @return
	 */
	public static boolean doesVolumeExists(String volName) {
		boolean exists = false;

		List<PersistentVolume> kubeVolumeList = kubeCon.persistentVolumes().list().getItems();
		for (PersistentVolume kubeVolume : kubeVolumeList) {
			if ((kubeVolume.getMetadata().getName()).equalsIgnoreCase(volName)) {
				pvLog.info("{} volume already exists", volName);
				exists = true;
			}
		}
		if (!(exists))
			pvLog.info("{} volume does not exists!", volName);
		return exists;
	}

	public void setVolumeMounts(List<VolumeMount> mounts) {

	}

	public boolean createPersistentVolume() {
		boolean created = false;

		return created;
	}

	public static void main(String args[]) {

		PmPersistentVolume persVol = new PmPersistentVolume(new File("persistentVolume.Config"));

	}
}
