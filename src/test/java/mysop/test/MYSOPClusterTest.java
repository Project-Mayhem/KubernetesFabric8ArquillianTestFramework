package mysop.test;


import io.fabric8.kubernetes.client.KubernetesClient;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.fabric8.kubernetes.assertions.Assertions.assertThat;


@RunWith(Arquillian.class)
@RunAsClient
public class MYSOPClusterTest {

	    @ArquillianResource
	    KubernetesClient client;
	    
	    /**
	     * Assert that the current project's Deployment, ReplicaSet or ReplicationController 
	     * creates at least one pod; that it becomes Ready within a time period (30 seconds by default),
	     * then that the pod keeps being Ready for a period.  This simple test will catches most errors with the 
	     * image or Deployment being invalid or failing to start; the pod starting then failing due
	     * to some configuration issue etc. 
	     *  
	     * @throws Exception
	     */

	    @Test
	    public void testAppProvisionsRunningPods() throws Exception {
	        assertThat(client).deployments().pods().isPodReadyForPeriod();
	    }
}
