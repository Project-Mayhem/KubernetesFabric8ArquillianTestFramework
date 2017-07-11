package cluster.test.java;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import io.fabric8.kubernetes.api.model.ServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import io.fabric8.kubernetes.api.model.Service;
import static io.fabric8.kubernetes.assertions.Assertions.assertThat;

import javax.inject.Named;

import io.fabric8.kubernetes.api.model.ReplicationControllerList;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.arquillian.kubernetes.Session;

@RunWith(Arquillian.class)
public class ClusterTester {

	// Obtaining a reference to the Kubernetes client
	@ArquillianResource
	KubernetesClient client;

	/**
	 * Asserts that at least one pod gets created, that it becomes Ready within a
	 * time period (30 seconds by default), then that the pod keeps being Ready for
	 * a period (defaults to 10 seconds).
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRunningPodStaysUp() throws Exception {
		assertThat(client).deployments().pods().isPodReadyForPeriod();

	}

	/*
	 * /** Test that this session has at least 1 namespace.
	 */
	@ArquillianResource
	Session session;
	@Test
	public void testAtLeastOnePod() throws Exception {
		assertThat(client).pods().runningStatus().filterNamespace(session.getNamespace()).hasSize(1);
	}

	/**
	 * To obtain a list of all services created in the current session.
	 */
	@ArquillianResource
	ServiceList sessionServices;

	/**
	 * To obtain a record of a particular service that was created in the current
	 * session.
	 */
	
	@ArquillianResource
	@Named("my-service")
	Service myService;

	/**
	 * To obtain the list of all replication controllers created in the current
	 * session:
	 * 
	 */
	@ArquillianResource
	ReplicationControllerList sessionControllers;

	/**
	 * To obtain a refernce to a particular replication controller created in the
	 * current session:
	 * 
	 */
	
	@ArquillianResource
	ReplicationController myController;

	/**
	 * To obtain the list of all pods created in the current session:
	 * 
	 */
	@ArquillianResource
	PodList sessionPods;

	/**
	 * To obtain the session.
	 * 
	 */

	@ArquillianResource
	Session mySession;

}
