import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.artifacts.PmNamespace;
import pm.cluster.artifacts.PmPersistentVolume;
import pm.cluster.artifacts.PmPersistentVolumeClaim;
import pm.cluster.artifacts.PmPod;
import pm.cluster.utils.KubernetesConnector;

public class FIOTestRunner {

	/**
	 * This will create 1 to many FIO containers with persistent volume claims. The
	 * specifics for this test will read the fiotest.config resource file.
	 */

	public static final String configFileName = "fiotest.config";

	private static Logger trLog = LoggerFactory.getLogger(FIOTestRunner.class);
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	public void runTest() {
		String perVolConfig = "persistenVolume.config";
		String perVolClaimConfig = "persistentVolumeClaim.config";

		PmPersistentVolume psVol = new PmPersistentVolume(perVolConfig);
		if (psVol.createPersistentVolume())
			trLog.info("{} persistent volume was created", psVol.getMetadata().getName());

		PmPersistentVolumeClaim psVolClaim = new PmPersistentVolumeClaim(perVolClaimConfig);
		if (psVolClaim.createPersistentVolumeClaim())
			trLog.info("{} persistent volume claim was created", psVolClaim.getMetadata().getName());

		// Create a test namespace for the test resources
		String nsConfig = "namespace.config";
		PmNamespace testNs = new PmNamespace(nsConfig);
		if (testNs.createNamespace())
			trLog.info("{} namesapce was created", testNs.getMetadata().getName());

		//Create the test Pod in the namespace
		PmPod myPod = new PmPod();

	}

}
