package emblcmci.obj.converters;

import java.util.ArrayList;
import java.util.Vector;

import emblcmci.linker.AbstractDotLinker.Particle;
import emblcmci.linker.AbstractDotLinker.Trajectory;
import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;
import emblcmci.obj.Track;
import emblcmci.obj.Tracks;
/**
 * Converts "ParticleTracker" 2D trajectories to emblcmci.obj.Tracks
 * @author Kota Miura (miura@embl.de)
 *
 */
public class VecTrajectoryToTracks extends AbstractVecTrajectoryToTracks{
	
	public VecTrajectoryToTracks() {
		super();
	}

//	@Override
//	public AbstractTracks createTracks() {
//		return new Tracks();
//	}

	@Override
	public AbstractTrack createTrack() {
		return new Track(new ArrayList<Node>());
	}

	@Override
	public AbstractTracks getTracks() {
		return this.tracks;
	}

	@Override
	public void setTracks() {
		this.tracks = new Tracks();
	}

	public void run(Vector<Trajectory> all_traj, ArrayList<Node> orgnodes){
//		AbstractTracks tracks = new Tracks();
		Node n;
		int id;
		AbstractTrack track;
		for (Trajectory traj : all_traj){
			Particle[] ptcls = traj.getExisting_particles();
			for (int i = 0; i < ptcls.length; i++){
				// Node(double x, double y, int frame, int trackID, int id)
				id = ptcls[i].getParticleID();
				n = getNodefromId(orgnodes, id);
				n.setTrackID(traj.getSerial_number());

				if (tracks.get(n.getTrackID()) == null){
					track = createTrack();
					track.setTrackID(n.getTrackID()); // maybe this is bad because tracks will be merged later. 
					tracks.put(n.getTrackID(), track);
				} else
					track = tracks.get(n.getTrackID());
				track.getNodes().add(n);
			}	
		}
		for (AbstractTrack t : tracks.values()){
		    t.checkFrameList();
		}
	}
	
	public Node getNodefromId(ArrayList<Node> nodes, int id){
		for (Node n : nodes){
			if (n.getId() == id)
				return n;
		}
		return null; 
	}
	
}
