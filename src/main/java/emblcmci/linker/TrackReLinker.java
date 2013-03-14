package emblcmci.linker;

import java.util.HashMap;
import java.util.Vector;

import emblcmci.linker.AbstractDotLinker.Trajectory;
import emblcmci.obj.Track;
import emblcmci.obj.VecTrajectoryToTracks;
/**
 * Re-evaluat tracks to check  
 * if it will be possible to merge some tracks as a single track. 
 * If possible, then merge some tracks and return an updated set of tracks. 
 * @author miura
 *
 */
public class TrackReLinker {
	HashMap<Integer, Track> tracks;
	
	// Maximum allowd distance between a track end point and a track start point.
	// unit: pixels. 
	int distance_threshould = 20;
	
	// allowed number of missing frames between tracks for interpolation. 
	// unit: frames
	int framegap_allowance = 2;
	
	/**
	 * @param tracks
	 */
	public TrackReLinker(Vector<Trajectory> tracks) {
		super();
		VecTrajectoryToTracks v2t = new VecTrajectoryToTracks();
		this.tracks = v2t.runsimple(tracks);
	}
	
	public HashMap<Integer, Track> relink(){

		return tracks;
	}
	
	
}
