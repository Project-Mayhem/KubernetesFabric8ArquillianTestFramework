package pm.cluster.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PodSpec;
import pm.cluster.artifacts.PmNamespace;
import pm.cluster.artifacts.PmPod;

public class PmPodTest {

	public static void main(String args[]) {
		PmPod myPod = new PmPod();
		String allPodLabels = null;
		String ns = "clustertesterforpod";

		// Create pod labels for the metadata
		Map<String, String> myPodLabels = new HashMap<String, String>();
		myPodLabels.put("test", "myspo");
		myPodLabels.put("developer", "anastasia");

		// create pod spec with containers
		PodSpec myPodSpec = new PodSpec();
		Container myPodCont1 = new Container();
		Container myPodCont2 = new Container();
		myPodCont1.setImage("elasticsearch");
		myPodCont1.setImagePullPolicy("Always");
		myPodCont1.setName("anastasiaelasticsearch");
		myPodCont2.setImage("mongodb");
		myPodCont2.setName("mongodb4asreitz");
		List<Container> cnList = new ArrayList<Container>();
		cnList.add(myPodCont1);
		cnList.add(myPodCont2);
		myPodSpec.setContainers(cnList);
		myPod.setSpec(myPodSpec);

		ObjectMeta myPodMetaData = new ObjectMeta();
		myPodMetaData.setName("anastaisapod4");

		// Set Pod's namesapce
		if (!(PmNamespace.doesNamespaceExists(ns))) {
			PmNamespace myPmNs = new PmNamespace();
			ObjectMeta myPmNsMd = new ObjectMeta();
			myPmNsMd.setName(ns);
			myPmNs.setMetaData(myPmNsMd);
		}

		myPodMetaData.setNamespace(ns);
		myPodMetaData.setLabels(myPodLabels);
		myPod.setMetadata(myPodMetaData);

		System.out.println("Pod Name: " + myPod.getMetadata().getName());
		System.out.println("Pod APIVersion: " + myPod.getApiVersion());
		System.out.println("Pod Kind: " + myPod.getKind());

		// Now, create the Pod:
		if (myPod.create()) {
			System.out.println("Pod got created!");
		}
	}
}
