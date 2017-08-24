package mysop.cluster.artifacts;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;

/**
 * Connects client to a running Elasticsearch instances.
 * 
 * @author asreitz
 *
 */


import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MSPOElasticsearchClientConnector {

	private static Logger elSearchLog = LoggerFactory.getLogger(MSPOElasticsearchClientConnector.class);
   
	
public static Client getConnection() throws UnknownHostException {
    TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host1"), 9300))
    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("host2"), 9300));
    
    return client;
    }
    
    public static Client getConnection(String clusterName) {
    	Settings settings = Settings.builder()
    	        .put("cluster.name", clusterName).build();
    	TransportClient client = new PreBuiltTransportClient(settings);
    	return client;
    }
    
}
