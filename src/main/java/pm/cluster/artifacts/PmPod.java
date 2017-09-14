package pm.cluster.artifacts;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * This class creates a Kubernetes Pod.  Once the Pod is defined, it is immutable; and
 * the only access to the pod is through this class interface.
 * 
 * Definition:  A Pod is the basic building block of Kubernetes–the smallest and simplest 
 * unit in the Kubernetes object model that you create or deploy. A Pod represents a 
 * running process on your cluster. It encapsulates an application container, storage
 * resources, a unique network IP, and options that govern how it's containers should run. 
 * 
 * @author asreitz
 *
 */
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.model.Volumes;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimVolumeSource;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import pm.cluster.utils.ConfigFileReader;
import pm.cluster.utils.KubernetesConnector;

public class PmPod extends Pod {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String podKind = "Pod";
	private String apiVer = "v1";
	private static Logger log = LoggerFactory.getLogger(PmPod.class);
	public static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	private Map<String, List<String>> myConfigs = null;

	/**
	 * There are 3 was to construct a pod in this application.
	 * 
	 * @param apiVersion
	 * @param metadata
	 * @param spec
	 * @param status
	 */
	public PmPod(String apiVersion, ObjectMeta metadata, PodSpec spec, PodStatus status) {
		super(apiVersion, podKind, metadata, spec, status);
	}

	public PmPod(Pod myPod) {
		this();
		super.setMetadata(myPod.getMetadata());
		super.setSpec(myPod.getSpec());
		if (!(myPod.getStatus() == null))
			super.setStatus(myPod.getStatus());
	}

	/**
	 * Reading pod config file for pod creation information
	 * 
	 */
	public PmPod(String configFileName) {
		ConfigFileReader reader = new ConfigFileReader();
		this.myConfigs = reader.readConfigFile(configFileName);
		this.mapConfigs();
	}

	/**
	 * Must set ObjectMeta, PodSpec, and (optionally, PodStatus) when using this
	 * constructor.
	 */
	public PmPod() {
		super();
		super.setKind(podKind);
		super.setApiVersion(apiVer);

	}

	/**
	 * Maps this pod's configs to it's custom keys
	 * 
	 * * name=fiotestpod labels=test:fio,devop:asreitz image=datawiseio/fio
	 * imagePullPolicy=Always containerName=areitzfio namespace=fiotestnsmeadowgate
	 */
	private void mapConfigs() {
		Map<String, String> podLabels = new HashMap<String, String>();

		if (!(this.myConfigs == null)) {
			for (Map.Entry<String, List<String>> config : myConfigs.entrySet()) {
				switch (config.getKey()) {
				case "name":
					List<String> name = config.getValue();
					this.getPodMetadata().setName(name.get(0).trim()); // Only pulling the first name; only need one!
					break;
				case "image": // may be a list of resources
					List<String> image = config.getValue();
					List<Container> cnList = new ArrayList<Container>();
					Container imageCnt = new Container();
					imageCnt.setImage(image.get(0).trim());
					cnList.add(imageCnt);
					this.getPodSpec().setContainers(cnList);
					log.info("{} is the image name", this.getSpec().getContainers().get(0));
					break;
				case "labels":
					List<String> labelList = config.getValue();
					for (String labelEntry : labelList) {
						String[] set = labelEntry.split(":");
						podLabels.put(set[0].trim(), set[1].trim());
					}
					this.getPodMetadata().setLabels(podLabels);
					break;
				case "containerName":
					List<String> nameList = config.getValue();
					Container targetCnt = this.getPodSpec().getContainers().get(0);
					targetCnt.setName(nameList.get(0));
					log.info("{} is the container's name", targetCnt.getName());
					break;
				case "imagePullPolicy":
					List<String> pullPolicy = config.getValue();
					Container cont = this.getPodSpec().getContainers().get(0);
					cont.setImagePullPolicy(pullPolicy.get(0));
					break;
				case "namespace":
					List<String> namespaces = config.getValue();
					String namespace = namespaces.get(0);
					log.info("{} is the namespace", namespace);
					if (!PmNamespace.doesNamespaceExists(namespace)) {
						PmNamespace podNs = new PmNamespace();
						podNs.setNsName(namespace);
						podNs.createNamespace();
					}
					this.getMetadata().setNamespace(namespace);
					break;
				case "containerVolumeMount":
					List<String> volmountsList = config.getValue();
					String mnt = volmountsList.get(0);
					// split the vol name from the path
					String[] volPair = mnt.split(":");
					log.info("{} is the volume name and {} is the volume's path", volPair[0], volPair[1]);

					List<VolumeMount> containerVolMntList = new ArrayList<VolumeMount>();
					VolumeMount vol = new VolumeMount();
					vol.setMountPath(volPair[1]);
					vol.setName(volPair[0]);
					containerVolMntList.add(vol);
					this.getSpec().getContainers().get(0).setVolumeMounts(containerVolMntList);
				case "envars":
					List<String> envarList = config.getValue();
					List<EnvVar> envVars = new ArrayList<EnvVar>(0);
					
					for (String envPair : envarList) {
						String[] pair = envPair.split(":");
						EnvVar var = new EnvVar();
						log.info("*******{} is the name with {} value",pair[0],pair[1]);
						var.setName(pair[0]);
						var.setValue(pair[1]);
						envVars.add(var);
					}
					this.getSpec().getContainers().get(0).setEnv(envVars);
				case "pvcClaim":
					List<String> volumePtdata = config.getValue();
					String pvcdata = volumePtdata.get(0);
					String[] pvcPathNameAndClaimName = pvcdata.split(":");
					log.info("****{} is the path name and \n {} is the pvclaim name", pvcPathNameAndClaimName[0],
							pvcPathNameAndClaimName[1]);
					Volume pathVol = new Volume();
					pathVol.setName(pvcPathNameAndClaimName[0]);
					pathVol.setPersistentVolumeClaim(
							new PersistentVolumeClaimVolumeSource(pvcPathNameAndClaimName[1], false));
					List<Volume> vols = new ArrayList<Volume>();
					vols.add(pathVol);
					this.getSpec().setVolumes(vols);
					break;
				}
			}
			log.info("The pod's data is this:\n" + this.getApiVersion() + " api version\n" + this.getKind() + " kind\n"
					+ this.getMetadata().getName() + " name\n" + this.getPodSpec().getContainers().get(0).getImage()
					+ "is the image");
			this.create();
		} else

		{
			log.info("Config file has not been provided; no configs available!");
		}
		
	}

	/**
	 * Ensures that this pod's spec exists
	 */
	private PodSpec getPodSpec() {
		if (this.getSpec() == null) {
			PodSpec spec = new PodSpec();
			this.setSpec(spec);
		}
		return this.getSpec();
	}

	/**
	 * Ensures that this pod's metadata exists
	 */
	private ObjectMeta getPodMetadata() {
		if (this.getMetadata() == null) {
			ObjectMeta meta = new ObjectMeta();
			this.setMetadata(meta);
		}
		return this.getMetadata();
	}

	/**
	 * Creates a pod based on a pod already being defined.
	 */
	public boolean create() throws KubernetesClientException {
		boolean created = false;

		if (!(this.getMetadata() == null) && (!(this.getMetadata().getName() == null))) {

			// determining if the pod is already created
			String podName = this.getMetadata().getName();
			log.info("Determining if POd already exists");
			log.info("{} is the namespace for this pod", this.getMetadata().getNamespace().trim());
			if (!(doesPodExists(podName, this.getMetadata().getNamespace().trim()))) {
				log.info("Creating {} Pod.", podName);
				log.info("Here is the Pod information: \nApiVersion " + this.getApiVersion() + "\nKind "
						+ this.getKind() + "\nPodName: " + this.getMetadata().getName() + "\nNamespace : "
						+ this.getMetadata().getNamespace());

				// get Labels
				String LabelStringList = ",";
				Map<String, String> podLabels = new HashMap<String, String>(this.getMetadata().getLabels());
				for (Map.Entry<String, String> entry : podLabels.entrySet()) {
					LabelStringList += entry.getKey() + ":" + entry.getValue() + ",";
				}

				// get containers
				String contString = ",";
				List<Container> podConList = new ArrayList<Container>(this.getSpec().getContainers());
				for (Container container : podConList) {
					contString += container.getName() + ", ";
				}
				log.info("Labels are: " + podLabels + "\nContainers are: " + contString);
				this.kubeCon.pods().create(this);

				// verify the pod got created
				if (this.doesPodExists(podName, this.getMetadata().getNamespace().trim()))
					created = true;
				log.info("Pod creation verified!");
			}
		}
		return created;
	}

	/**
	 * Determining if the Pod already exists:
	 */
	public static boolean doesPodExists(String podName, String namespace) {
		log.info("{} is under investigation for existance", podName);
		log.info("{} is the namespace for this pod", namespace);
		boolean exists = false;
		List<Pod> kubePods = kubeCon.pods().inNamespace(namespace).list().getItems();
		for (Pod pod : kubePods) {
			log.info("{} pod", pod.getMetadata().getName());
			if ((pod.getMetadata().getName()).equalsIgnoreCase(podName)) {
				exists = true;
				log.info("The \"{}\" pod already exists; no creation needed.", podName);
			}
		}
		return exists;
	}

	/**
	 * The client can only set the Pod's apiVersion if it isn't already set.
	 * 
	 * @param apiVersion
	 */
	public void setPodApiVersion(String apiVersion) {

		if (super.getApiVersion() == null) {
			super.setApiVersion(apiVersion);
		}
	}

	/**
	 * Convenience method to set pod spec for storage specifics
	 */
	public void setPodSepc(PodSpec pdSpec) {
		this.setPodSepc(pdSpec);
	}

	/**
	 * The client can only set the Pod's metadata if it isn't already set.
	 * 
	 * @param metadata
	 */

	public void setPodMetadata(ObjectMeta metadata) {
		if (super.getMetadata() == null) {
			super.setMetadata(metadata);
		}
	}

	public String toString() {

		String allPodLabels = null;
		String podPrint = null;

		if ((this != null) && (this.getMetadata() != null) && this.getMetadata().getLabels() != null) {
			// pull the list into a string
			Set<Map.Entry<String, String>> labelNames = this.getMetadata().getLabels().entrySet();
			for (Entry<String, String> item : labelNames) {
				allPodLabels += item.getKey() + ":" + item.getValue();
			}
		}
		System.out.println("The labels are: " + allPodLabels);

		if (!(this == null)) {
			podPrint = new String("Pod Name: " + this.getMetadata().getName() + "\nKind: " + podKind + "\nApi Version: "
					+ this.getApiVersion() + "\nLabels: ");
		}
		return podPrint;
	}

	// quick testing:
	public static void main(String args[]) {
		PmPod myPod = new PmPod("pods.config");
		/*
		 * PmPod myPod = new PmPod();
		 * 
		 * String allPodLabels = null; String ns = "default";
		 * 
		 * // Create pod labels for the metadata Map<String, String> myPodLabels = new
		 * HashMap<String, String>(); myPodLabels.put("test", "myspo");
		 * myPodLabels.put("developer", "anastasia");
		 * 
		 * // create pod spec with containers PodSpec myPodSpec = new PodSpec();
		 * Container myPodCont1 = new Container(); Container myPodCont2 = new
		 * Container(); myPodCont1.setImage("elasticsearch");
		 * myPodCont1.setImagePullPolicy("Always");
		 * myPodCont1.setName("anastasiaelasticsearch"); myPodCont2.setImage("mongo");
		 * myPodCont2.setName("mongodb4asreitz"); List<Container> cnList = new
		 * ArrayList<Container>(); cnList.add(myPodCont1); cnList.add(myPodCont2);
		 * myPodSpec.setContainers(cnList); myPod.setSpec(myPodSpec);
		 * 
		 * ObjectMeta myPodMetaData = new ObjectMeta();
		 * myPodMetaData.setName("tomasmunson");
		 * 
		 * // Set Pod's namesapce if (PmNamespace.doesNamespaceExists(ns) == false) {
		 * log.info("*** creating {} namespace", ns); PmNamespace myPmNs = new
		 * PmNamespace(); ObjectMeta myPmNsMd = new ObjectMeta(); myPmNsMd.setName(ns);
		 * myPmNs.setMetaData(myPmNsMd); myPmNs.createNamespace(); }
		 * 
		 * myPodMetaData.setNamespace(ns); myPodMetaData.setLabels(myPodLabels);
		 * myPod.setMetadata(myPodMetaData);
		 * 
		 * System.out.println("Pod Name: " + myPod.getMetadata().getName());
		 * System.out.println("Pod APIVersion: " + myPod.getApiVersion());
		 * System.out.println("Pod Kind: " + myPod.getKind());
		 */
		// Now, create the Pod:
		if (myPod.create()) {
			System.out.println("Pod got created!");
		}
	}
}
