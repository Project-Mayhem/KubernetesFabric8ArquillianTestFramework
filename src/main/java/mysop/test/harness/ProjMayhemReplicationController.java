package mysop.test.harness;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ReplicationController;
import io.fabric8.kubernetes.api.model.ReplicationControllerBuilder;
import io.fabric8.kubernetes.api.model.ReplicationControllerSpec;
import io.fabric8.kubernetes.api.model.ReplicationControllerStatus;

/**
 * This is a ProjMayham Kubernetes ReplicationController. It is immutable once
 * it is defined.
 * 
 * @author asreitz
 *
 */

public class ProjMayhemReplicationController {

	private ReplicationController repCntrl = null;
	private String kubeKind = "ReplicationController";

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
