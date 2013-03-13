package emblcmci.linker;

import java.util.Vector;

import emblcmci.linker.AbstractDotLinker.Trajectory;

public class TrackReLinker {
	Vector<Trajectory> Tracks;
	
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
		Tracks = tracks;
	}
	
	public Vector<Trajectory> Relink(Vector<Trajectory> tracks){
		for (Trajectory t1 : tracks){
			
		}
		return tracks;
	}
	
	
}
