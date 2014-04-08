package de.embl.cmci.obj.converters;

import java.util.ArrayList;
import java.util.Vector;

import de.embl.cmci.linker.DotLinker.Particle;
import de.embl.cmci.linker.DotLinker.Trajectory;
import de.embl.cmci.obj.AbstractTrack;
import de.embl.cmci.obj.AbstractTracks;
import de.embl.cmci.obj.Node;

/** Abstract class for convertiong instance of Vector<Trajectory> class to Tracks class. 
 *  Vector<Trajectory> class is used in te ParticleTracker plugin. 
 * 
 * @author Kota Miura
 *
 */
public abstract class AbstractVecTrajectoryToTracks {

	
	protected AbstractTracks tracks;
	
	public AbstractVecTrajectoryToTracks(){
		setTracks();
	}

	/** converts 
	 * conversion is for only following elements:
	 * x, y, frame, trackid, node id.
	 * ... might change further, to enable 3D coordinates as well
	 * @param all_traj
	 * @return
	 */
	public void run(Vector<Trajectory> all_traj){
//		AbstractTracks tracks = new Tracks();
		Node n;
		int idnum = 0;
		AbstractTrack track;
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
	public abstract AbstractTrack createTrack();
	
	public abstract void setTracks();
	
	public abstract AbstractTracks getTracks();
	
}