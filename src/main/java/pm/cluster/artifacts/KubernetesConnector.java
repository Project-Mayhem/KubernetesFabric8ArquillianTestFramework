package pm.cluster.artifacts;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * Provides a connection to the Kubernetes Client
 * 
 * @author Anastasia
 *
 */
public class KubernetesConnector {

	public static KubernetesClient getKubeClient(String kubeMasterUrl) {
		return new DefaultKubernetesClient(kubeMasterUrl);
	}

	public static KubernetesClient getKubeClient(Config clientConfig) {
		return new DefaultKubernetesClient(clientConfig);
	}
	
	public static KubernetesClient getKubeClient() {
		//since url is not specified, usign the default url for minikube
		return getKubeClient("https://192.168.99.100:8443");
	}
}