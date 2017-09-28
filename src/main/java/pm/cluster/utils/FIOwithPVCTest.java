package pm.cluster.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.artifacts.PmNamespace;
import pm.cluster.artifacts.PmPersistentVolume;
import pm.cluster.artifacts.PmPersistentVolumeClaim;
import pm.cluster.artifacts.PmPod;

public class FIOwithPVCTest {

	/**
	 * This will create 1 to many FIO containers with persistent volume claims. The
	 * specifics for this test will read the fiotest.config resource file.
	 */

	public static final String configFileName = "fiotest.config";
	private static Logger trLog = LoggerFactory.getLogger(FIOwithPVCTest.class);
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	public static void runTest() {
		String perVolConfig = "persistentVolume.config";
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

		// Create the test Pod in the namespace
		String podConfig = "pods.config";
		PmPod myPod = new PmPod(podConfig);
		if (myPod.create())
			trLog.info("{} pod got created ", myPod.getMetadata().getName());
		
		String fioPodConf = "fioPod.config";
		PmPod fioPod = new PmPod(fioPodConf);
		if(fioPod.create())
			trLog.info("{} pod got created",fioPod.getMetadata().getName());

	}

	public static void main(String[] args) {

		FIOwithPVCTest runner = new FIOwithPVCTest();
		runner.runTest();

	}

}
