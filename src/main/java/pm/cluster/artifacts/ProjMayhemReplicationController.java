package pm.cluster.artifacts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerBuilder;
import io.fabric8.kubernetes.api.model.ReplicationControllerSpec;
import io.fabric8.kubernetes.api.model.ReplicationControllerStatus;

/**
 * This is a ProjMayham Kubernetes ReplicationController. It is immutable once
 * it is defined.
 * 
 * Definition: A ReplicationController ensures that a specified number of pod
 * replicas are running at any one time. In other words, a ReplicationController
 * makes sure that a pod or a homogeneous set of pods is always up and
 * available.
 * 
 * @author asreitz
 *
 */

public class ProjMayhemReplicationController extends ReplicationController {

	private ReplicationController repCntrl = null;
	private static final String kubeKind = "ReplicationController";
	private static Logger LOG = LoggerFactory.getLogger(ProjMayhemReplicationController.class);

	public ProjMayhemReplicationController() {
		this.repCntrl = new ReplicationController();
	}

	public ProjMayhemReplicationController(ReplicationController clientRepController) {
		this.repCntrl = clientRepController;
	}

	public ProjMayhemReplicationController(String apiVer, ObjectMeta metadata, ReplicationControllerSpec repSpec,
			ReplicationControllerStatus repStatus) {
		this.repCntrl = new ReplicationController(apiVer, kubeKind, metadata, repSpec, repStatus);
	}

	public void createRepController() {
		if (this.repCntrl.equals(null)) {
			ReplicationControllerBuilder repCntrlBld = new ReplicationControllerBuilder(repCntrl);
			repCntrlBld.build();
		}
	}

	public void setRepContrlApiVersion(String apiVer) {
		if ((this.repCntrl.getApiVersion().equals(null)) && (!this.repCntrl.equals(null))) {
			repCntrl.setApiVersion(apiVer);
		}
	}

	public void setRepCntrlObjectMeta(ObjectMeta metadata) {
		if ((this.repCntrl.getMetadata().equals(null)) && (!this.repCntrl.equals(null))) {
			repCntrl.setMetadata(metadata);
		}
	}

	public void setRepCntrlSpec(ReplicationControllerSpec repSpec) {
		if ((this.repCntrl.getSpec().equals(null)) && (!this.repCntrl.equals(null))) {
			repCntrl.setSpec(repSpec);
		}
	}

	public void setRepCntrlSstatus(ReplicationControllerStatus status) {
		if ((this.repCntrl.getStatus().equals(null)) && (!this.repCntrl.equals(null))) {
			repCntrl.setStatus(status);
		}
	}
}
