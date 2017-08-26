package pm.cluster.performance.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Create a test suit to determine the client compute (CPU, network bandwidth, storage) limit when
 * deploying the maximum number of pods onto a Kubernetes cluster. In addition,
 * we need to do random read/writes (in 4kb chunks) to understand the device
 * iops per POD.
 * 
 * This implementation will require monitoring the storage appliance metrics
 * to determine storage io latency and capacity while the test is running.
 * 
 * @author asreitz
 *
 */

public class MultiplePodInstanceTest {

	private static final Logger mpTestLog = LoggerFactory.getLogger(MultiplePodInstanceTest.class);

	
	public MultiplePodInstanceTest() {
		
	}
}
