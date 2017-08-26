package pm.cluster.performance.test;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A single test pod will test storage by implementing a 
 * series of random read/writes against a large file.  there will be 3 reads
 * to every 1 4kb size data writes.  The test will run for about 64 concurrent threads 
 * ( or whatever is specified in the FioConfigs file).
 * 
 * @author asreitz
 * 
 * TEST CRITERIA:  Gather r/w iops and note any peculiar CPU load on the minion(s).
 *
 */

@RunWith(Arquillian.class)
public class StorageReadWriteTest {
	
	private static Logger LOG = LoggerFactory.getLogger(StorageReadWriteTest.class); 
	
	
	

}
