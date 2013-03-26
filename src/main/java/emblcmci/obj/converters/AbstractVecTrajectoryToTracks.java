package emblcmci.obj.converters;

import java.util.ArrayList;
import java.util.Vector;

import emblcmci.linker.AbstractDotLinker.Particle;
import emblcmci.linker.AbstractDotLinker.Trajectory;
import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;

public abstract class AbstractVecTrajectoryToTracks {

	
	/** converts 
	 * conversion is for only following elements:
	 * x, y, frame, trackid, node id.
	 * ... might change further, to enable 3D coordinates as well
	 * @param all_traj
	 * @return
	 */
	public AbstractTracks run(Vector<Trajectory> all_traj){
//		AbstractTracks tracks = new Tracks();
		AbstractTracks tracks = createTracks();
		Node n;
		AbstractTrack track;
		int idnum = 0;
		for (Trajectory traj : all_traj){
			Particle[] ptcls = traj.getExisting_particles();
			for (int i = 0; i < ptcls.length; i++){
				// Node(double x, double y, int frame, int trackID, int id)
				n = new Node(
						ptcls[i].getX(), 
						ptcls[i].getY(), 
						ptcls[i].getFrame(), 
						traj.getSerial_number(), idnum++);	

				if (tracks.get(n.getTrackID()) == null){
					track = createTrack(new ArrayList<Node>());
					track.setTrackID(n.getTrackID()); // maybe this is bad because tracks will be merged later. 
					tracks.put(n.getTrackID(), track);
				} else
					track = (AbstractTrack) tracks.get(n.getTrackID());
				track.getNodes().add(n);
			}	
		}
		return tracks;	
	}
	public abstract AbstractTracks createTracks();
	
	public abstract AbstractTrack createTrack(ArrayList<Node> nodes);
	
}