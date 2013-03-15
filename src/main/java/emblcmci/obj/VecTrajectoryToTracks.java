package emblcmci.obj;

import java.util.ArrayList;
import java.util.Vector;

import emblcmci.linker.AbstractDotLinker.Particle;
import emblcmci.linker.AbstractDotLinker.Trajectory;
/**
 * Converts "ParticleTracker" trajectories to emblcmci.obj.Tracks
 * @author Kota Miura (miura@embl.de)
 *
 */
public class VecTrajectoryToTracks {
	
	/** converts only following elements:
	 * x, y, frame, trackid, node id.
	 * 
	 * @param all_traj
	 * @return
	 */
	public Tracks runsimple(Vector<Trajectory> all_traj){
		Tracks tracks = new Tracks();
		Node n;
		Track track;
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
					track =new Track(new ArrayList<Node>());
					tracks.put(n.getTrackID(), track);
				} else
					track = tracks.get(n.getTrackID());
				track.getNodes().add(n);
			}	
		}
		return tracks;	
	}
}
