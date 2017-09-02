package pm.cluster.artifacts;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.KubernetesClient;
import pm.cluster.artifacts.exceptions.PMConfigMapException;
import pm.cluster.utils.ConfigMapUtils;
import pm.cluster.utils.KubernetesConnector;

public class PMConfigMap extends ConfigMap {

	private static final long serialVersionUID = 1L;
	private static final String kind = "ConfigMap";
	private String apiVer = "v1";
	private static Logger log = LoggerFactory.getLogger(PMConfigMap.class);
	public static KubernetesClient kubeCon = KubernetesConnector.getKubeClient();
	
	/**
	 * 
	 * @param configfilepathandName - relative path to propertyfile in classpath
	 * @param configMapName
	 */
	public PMConfigMap(String configfilepathandName, String configMapName, String namespace) throws PMConfigMapException {
		super();
		log.debug("Begin PMConfigMap Constructor(configfilepathandName, configMapName");
		this.setApiVersion(apiVer);
		this.setKind(kind);
		this.setMetadata(new ObjectMeta());
		this.getMetadata().setName(configMapName);
		this.getMetadata().setNamespace(namespace);
		try {
			this.setData(ConfigMapUtils.properties2Map(configfilepathandName));
		} catch (Exception e) {
			throw new PMConfigMapException("Unable set configMap data for: [" + configfilepathandName + "]",e);
		}	
	}
	
	public Boolean create() throws Exception {
		log.info("contents are: " + this.toString());
		Boolean created = false;
		kubeCon.configMaps().create(this);

		// verify that pod was created
		if (doesConfigMapExist(this.getMetadata().getName())) {
			created = true;
			log.info("ConfigMap creation verified!");
		} else
			log.info("ConfigMap exists already");
		return created;
	}
	
	/**
	 * Determining if the Pod already exists:
	 */
	public static boolean doesConfigMapExist(String configMapName) {
		log.info("{} is under investigation for existance", configMapName);
		boolean exists = false;
		List<ConfigMap> configMaps = kubeCon.configMaps().list().getItems();
		for (ConfigMap map : configMaps) {
			log.info("{} configMap", map.getMetadata().getName());
			if ((map.getMetadata().getName()).equalsIgnoreCase(configMapName)) {
				exists = true;
				log.info("The \"{}\" configMap already exists; no creation needed.", configMapName);
			}
		}
		return exists;
	}
}
