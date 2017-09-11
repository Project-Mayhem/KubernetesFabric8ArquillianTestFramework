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
import pm.cluster.artifacts.PmPod;
import pm.cluster.utils.KubernetesConnector;

public class FIOTestRunner {

	/**
	 * This will create 1 to many FIO containers with persistent volume claims. The
	 * specifics for this test will read the fiotest.config resource file.
	 */

	public static final String configFileName = "fiotest.config";

	private static Logger trLog = LoggerFactory.getLogger(FIOTestRunner.class);
	private ClassLoader classLoader = getClass().getClassLoader();
	private File configFile = new File(classLoader.getResource(configFileName).getFile());
	private static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();

	public void runTest() {

		PmPod myPod = new PmPod();

		String allPodLabels = null;
		String ns = "default";
		String imageName = "datawiseio/fio";
		String podName = "fioTest";
		int numberOfRuns = 4;
		String pvVolClaim = "pvcTester12";

		int count = 0;
		while (count < 4) {
			// Create pod labels for the metadata
			Map<String, String> myPodLabels = new HashMap<String, String>();
			myPodLabels.put("test", "myspo");
			myPodLabels.put("type", "fio");

			// create pod spec with containers
			PodSpec myPodSpec = new PodSpec();
			Container myPodCont1 = new Container();
			
			myPodCont1.setImage(imageName);
			myPodCont1.setImagePullPolicy("Always");
			myPodCont1.setName(podName + count);

			List<Container> cnList = new ArrayList<Container>();
			cnList.add(myPodCont1);
			myPodSpec.setContainers(cnList);
			
			//Set PersistentVolumes
			List<Volume> volumes = new ArrayList<Volume>();
			Volume podVolCl = new Volume();
			podVolCl.setName("teststorage" + count);
			PersistentVolumeClaimVolumeSource volSrc = new PersistentVolumeClaimVolumeSource();
			kubeCon.persistentVolumeClaims().load(url);
			kubeCon.persistentVolumeClaims().lo
			podVolCl.setPersistentVolumeClaim(pvcTester1);
			
			myPodSpec.setVolumes(volumes);
			
			myPod.setSpec(myPodSpec);

			ObjectMeta myPodMetaData = new ObjectMeta();
			myPodMetaData.setName();

			// Set Pod's namesapce
			if (PmNamespace.doesNamespaceExists(ns) == false) {
				trLog.info("*** creating {} namespace", ns);
				PmNamespace myPmNs = new PmNamespace();
				ObjectMeta myPmNsMd = new ObjectMeta();
				myPmNsMd.setName(ns);
				myPmNs.setMetaData(myPmNsMd);
				myPmNs.createNamespace();
			}

			myPodMetaData.setNamespace(ns);
			myPodMetaData.setLabels(myPodLabels);
			myPod.setMetadata(myPodMetaData);
		}

	}

}
