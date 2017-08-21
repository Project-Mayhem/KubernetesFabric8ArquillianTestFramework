package mysop.test.harness;

/**
 * This class provides the ability to create a namespace; kubernetes virtual
 * cluster.
 * 
 * Definition: Kubernetes supports multiple virtual clusters backed by the same
 * physical cluster. These virtual clusters are called namespaces.  MYSOP uses
 * namespaces to manage resource quotas between teams and/or projects.  By default,
 * objects in the same namespace will have the same access control policies.
 * 
 * @author asreitz
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceSpec;
import io.fabric8.kubernetes.api.model.NamespaceStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;

public class ProjMayhamNamespace extends Namespace {

	private static Logger LOG = LoggerFactory.getLogger(ProjMayhamNamespace.class);
	private Namespace nasp = null;
	public static final String kind = "Namespace";

	/**
	 * Instantiates a new namespace that can be customized and built 
	 * through this class interface.
	 */
	public ProjMayhamNamespace() {
		this.nasp = new Namespace();
	}

	/**
	 * Instantiates a new namespace based on specific build artifacts.
	 * @param apiVer
	 * @param metadata
	 * @param naspSpec
	 * @param naspStatus
	 */
	public ProjMayhamNamespace(String apiVer, ObjectMeta metadata, 
			NamespaceSpec naspSpec, NamespaceStatus naspStatus) {
		this.nasp = new Namespace(apiVer, this.kind, metadata, naspSpec, naspStatus);

	}
	
	/**
	 * Assigns this class namespace to another existing namespace.
	 */
	
	public ProjMayhamNamespace(Namespace clientNasp) {
		this.nasp=clientNasp;
	}
	
	public void createThisNamespace() {
		NamespaceBuilder nsBldr = new NamespaceBuilder(this.nasp);
		nsBldr.build();
	}
	
	/**
	 * Retries this namespace apiVersion.
	 * @return the namepsace apiVersion
	 */
	
	public String getApiVersion()
	{
		String apiVersion= null;
		if((!this.nasp.equals(null)) && (!this.nasp.getApiVersion().equals(null))) {
			apiVersion = this.nasp.getApiVersion();
		}
		return apiVersion;
	}
	
	/**
	 * Retrieves this namespace Object metadata; allowing the client to set metadata information. 
	 * @return
	 */
	public ObjectMeta getObjectMetadata()
	{
		ObjectMeta obm = null;
		if((!this.nasp.equals(null)) && (!this.nasp.getMetadata().equals(null))) {
			obm = this.nasp.getMetadata();
		}
		return obm;
	}
	
	/**
	 * Sets this namespace metadata.
	 * @param metaData
	 */
	public void setMetaData(ObjectMeta metaData) {
		if((!this.nasp.equals(null)) && (this.nasp.getMetadata().equals(null))) {
			this.nasp.setMetadata(metaData);
		}
	}
	
	/**
	 * Sets the namespace spec.
	 * @param spec
	 */
	public void setNSSpec(NamespaceSpec spec) {
		if((!this.nasp.equals(null)) && (this.nasp.getSpec().equals(null))) {
			this.nasp.setSpec(spec);
		}
	}
	
	/**
	 * Sets the namespace status.
	 * @param status - The status of the namespace.
	 */
	public void setNSStatus(NamespaceStatus status)
	{
		if((!this.nasp.equals(null)) && (this.nasp.getStatus().equals(null))) {
			this.nasp.setStatus(status);
		}
	}
}
