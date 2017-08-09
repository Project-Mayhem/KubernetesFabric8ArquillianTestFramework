package test.java;

import io.fabric8.kubernetes.client.KubernetesClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceList;
import io.fabric8.kubernetes.api.model.ReplicationControllerList;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.PodList.ApiVersion;
import io.fabric8.kubernetes.api.model.ReplicationController;

import static io.fabric8.kubernetes.assertions.Assertions.assertThat;
import static io.fabric8.kubernetes.assertions.Conditions.hasLabel;
import static io.fabric8.kubernetes.assertions.Conditions.hasName;
import static io.fabric8.kubernetes.api.model.extensions.AbstractDeploymentConditionAssert.*;

import java.util.List;

@RunWith(Arquillian.class)
public class PodTester {

	@ArquillianResource
	KubernetesClient client;

	/**
	 * Ensuring that the Pods were deployed
	 * 
	 * @throws Exception
	 */

	@Test
	public void testRunningPodStaysUp() throws Exception {
		assertThat(client).deployments().pods().isPodReadyForPeriod();
	}

	/**
	 * Testing to validate the pod's namespace.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPodResources() throws Exception {
		assertThat(client).namespace().equalsIgnoreCase("my_namespace");
	}

	/**
	 * Obtaining a list of services and ensuring that they are correct.
	 * 
	 * @throws Exception
	 */
	@ArquillianResource
	ServiceList sessionServices;

	@Test
	public void validateServiceList() throws Exception {
		List<Service> services = sessionServices.getItems();

		for (Service service : services)
			assertThat(client).podsForService(service);
	}

	/**
	 * Ensuring that the replication controllers are valid.
	 * 
	 * @throws Exception
	 */
	@ArquillianResource
	ReplicationControllerList sessionControllers;

	@Test
	public void validReplicationSontrollers() throws Exception {
		List<ReplicationController> repControllers = sessionControllers.getItems();
		for (ReplicationController controller : repControllers) {
			assertThat(client).podsForReplicationController(controller);
		}
	}

	
	/**
	 * Ensures that the apiVersion is not null  
	 *
	 */
	@ArquillianResource
	PodList sessionPods;

	@Test
	public void validateApiVersionNotNull() throws Exception {
		ApiVersion apiVersion = (client).pods().list().getApiVersion();
		assert ApiVersion.valueOf(apiVersion.toString()) != null;
	}

}
