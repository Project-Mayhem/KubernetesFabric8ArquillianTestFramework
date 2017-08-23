package mysop.cluster.performance.test;

/**
 * Creating a suit of test that will:
 * 1) create a persistent storage claim 
 * 2) create a PodNamespace for the Pod
 * 3) deploy an image and attached (mount) the persistent storage volume to the pod
 * 4) register the persistant storage volume for use in the namespace
 * 5) write data to the storage volume in the pod
 * 
 * The creation and integration of these Kubernetes objects will be verified 
 * using Arquillian-cube.  
 * 
 * TEST CRITERIA:  A persistent volume claim is created, a pod is created with 
 * the persistent volume claim, and data can be written to and read from the storage.
 * 
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.client.KubernetesClient;
import mysop.cluster.structure.test.NamespaceTests;

@RunWith(Arquillian.class)
@RunAsClient
/**
 * Assuming this is actually running from within a namespace in a pod in a k8s cluster
 * @author curtisbates
 *
 */
public class FioTest {
	
	public static final Long ONEHUNDERED_GB = new Long("1000000000");
	private static Logger fioTestLog = LoggerFactory.getLogger(FioTest.class); 
	
	@ArquillianResource
	KubernetesClient client;

	/**
	 * Checks to see if the expected persistent volume exists - the pod should have a
	 * 100GB volume available at /data
	 */
	@Test
	public void testPersistentVolumeExists() throws Exception{
		System.out.println(client.getConfiguration());
		Long totalSpace;	
		File data = new File("/data");
		if(!data.isDirectory())
			throw new Exception("Location not a directory");
		totalSpace = data.getTotalSpace();
		assertThat(totalSpace).isEqualByComparingTo(ONEHUNDERED_GB);
	}
}
