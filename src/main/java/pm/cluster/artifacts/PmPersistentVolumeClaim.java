package pm.cluster.artifacts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.utils.KubernetesConnector;

public class PmPersistentVolumeClaim extends Volume {

	private static final long serialVersionUID = -2259057698490908817L;
	private static Logger log = LoggerFactory.getLogger(PmPersistentVolumeClaim.class);
	public static final String kind = "PersistentVolume";
	private static String apiVersion = "v1";
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	public PmPersistentVolumeClaim(String name) {
		Volume pmVolume = new Volume();
		pmVolume.setName(name);
	}

	/**
	 * Determines if a volume already exists
	 * @param volName
	 * @return
	 */
	public static boolean doesVolumeExists(String volName) {
		boolean exists = false;

		List<PersistentVolume> kubeVolumeList = kubeCon.persistentVolumes().list().getItems();
		for (PersistentVolume kubeVolume : kubeVolumeList) {
			if ((kubeVolume.getMetadata().getName()).equalsIgnoreCase(volName)) {
				log.info("{} volume already exists", volName);
				exists = true;
			}
		}
		if (!(exists))
			log.info("{} volume does not exists!", volName);
		return exists;
	}
	
	public void setVolumeMounts(List<VolumeMount> mounts) {
		
	}
	
	public boolean createPersistentVolume() {
		boolean created = false;
		
		return created;
	}
	
	public static void main(String args[]) {
		
	}
}
