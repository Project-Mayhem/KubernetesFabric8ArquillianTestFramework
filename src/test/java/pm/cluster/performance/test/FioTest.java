package pm.cluster.performance.test;

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

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpec;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimSpecBuilder;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.client.KubernetesClient;

@RunWith(Arquillian.class)
@RunAsClient
/**
 * Assuming this is actually running from within a namespace in a pod in a k8s cluster
 * @author curtisbates
 * 
 * Docker Image: datawiseio/fio:v0.3
 * If using this image, the following start.sh script executes as image entrypoint.  Notice the
 * variable injection from supplied environment varibales...
 * 
 * #!/bin/bash

export FIO_JOB_FILE=${FIO_JOB_FILE:-/config/job.fio}
export JOB_NAME=${JOB_NAME:-fio}
export NJ=${NJ:-8}
export QD=${QD:-16}
export BLOCK_SIZE=${BLOCK_SIZE:-4k}
export DEV=${DEV:-/data/testfile}
export DIRECT=${DIRECT:-1}
export SIZE=${SIZE:-5g}
export RW=${RW:-read}
export RT=${RT:-300}
export STATUS_INTERVAL=${STATUS_INTERVAL:-30}
export STATUS_FILE=${STATUS_FILE:-/data/${JOB_NAME}.out}

exec /usr/bin/fio $FIO_JOB_FILE --status-interval ${STATUS_INTERVAL} --output ${STATUS_FILE}

 * 
 *
 */
public class FioTest {
	public static final String apiVersion = "v1";
	public static final String ONE_GB = "10GB";
	public static final String ONEHUNDERED_GB = "100GB";
	private static Logger fioTestLog = LoggerFactory.getLogger(FioTest.class); 
	
	@ArquillianResource
	KubernetesClient client;

	public void setup() {
		PersistentVolumeClaim claim = new PersistentVolumeClaim();
		claim.setApiVersion(apiVersion);
		claim.setKind("standard");
		PersistentVolumeClaimSpec spec = new PersistentVolumeClaimSpec();
		spec.setVolumeName("testVolume");
		ResourceRequirements r = new ResourceRequirements();
		
		claim.setSpec(spec);
		
	}
	
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
