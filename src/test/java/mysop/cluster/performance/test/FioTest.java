package mysop.cluster.performance.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.fabric8.kubernetes.client.KubernetesClient;

@RunWith(Arquillian.class)
//@RunAsClient
/**
 * Assuming this is actually running from within a namespace in a pod in a k8s cluster
 * @author curtisbates
 *
 */
public class FioTest {
	
	public static final Long ONEHUNDERED_GB = new Long("1000000000");
	
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
