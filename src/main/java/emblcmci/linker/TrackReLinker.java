package emblcmci.linker;

import ij.IJ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import emblcmci.obj.CoordTwoD;
import emblcmci.obj.Node;
import emblcmci.obj.Track;
import emblcmci.obj.Tracks;

/**
 * Re-evaluat tracks to check  
 * if it will be possible to merge some tracks as a single track. 
 * If possible, then merge some tracks and return an updated set of tracks. 
 * 
 * ConcreteElement in the Modified Visitor pattern.
 * 
 * @author miura
 *
 */
public class TrackReLinker extends Analyzer {
	Tracks tracks;
	Tracks updatedTracks;
	
	/**
	 * Frame numbers to sdample from track starting or ending terminal
	 * to calculate their aavarage positions.  
	 */
	final int TERMINAL_SAMPLING_FRAME_NUMBER = 3;
	
	/** Maximum allowd distance between a track end point and a track start point.
	/* unit: pixels. 
	 */
	int distance_threshould = 20;
	
	// allowed number of missing frames between tracks for interpolation. 
	// unit: frames
	int framegap_allowance = 2;
	
//	/**
//	 * @param tracks
//	 */
//	public TrackReLinker(Vector<Trajectory> tracks) {
//		super();
//		VecTrajectoryToTracks v2t = new VecTrajectoryToTracks();
//		this.tracks = v2t.runsimple(tracks);
//	}
	/**
	 * 
	 */
	@Override 
	public void analyze(Track t) {
		findLargeGapsButSimilarPositions(t, this.tracks);
	}	

	@Override 
	public void analyze(Tracks ts) {
		this.tracks = ts;
		for (Track t : ts.values()) {
			// preparation: calculate some of track parameters.
			detectFrameBounds(t);
			calcMeanPositionBeginning(t);			
		}
		for (Track t : ts.values()) //iterate for tracks
				t.accept(this);				
	}
	/**
	 * preparation for evaluating tracks. 
	 * Store start frame and end frame of a track in the Track object. 
	 * @param t
	 */
	void detectFrameBounds(Track t){
		int frameStart;
		int frameEnd;
		checkFrameList(t);
		Object objmin = Collections.min(t.getFramelist());
		frameStart = (Integer) objmin;
		Object objmax = Collections.max(t.getFramelist());
		frameEnd = (Integer) objmax;
		t.setFrameStart(frameStart);
		t.setFrameEnd(frameEnd);
	}
	
	void checkFrameList(Track t){
		if (t.getFramelist().size() == 0)
			for (Node n : t.getNodes())
				t.getFramelist().add(n.getFrame());		
	}
	
	/**
	 * Calculate average positions of the track starting points and endpoints. 
	 */
	void calcMeanPositionBeginning(Track t){
		int sampleNum = TERMINAL_SAMPLING_FRAME_NUMBER;
		double meanx_s;
		double meany_s;
		double meanx_e;
		double meany_e;		
		checkFrameList(t);
		ArrayList<Node> nodes = t.getNodes();
		if (t.getFramelist().size() < sampleNum){
			meanx_s = nodes.get(0).getX();
			meany_s = nodes.get(0).getY();
			meanx_e = nodes.get(nodes.size()-1).getX();
			meany_e = nodes.get(nodes.size()-1).getY();
			
		} else {
			meanx_s = 0;
			meany_s = 0;
			meanx_e = 0;
			meany_e = 0;
			int i, j;
			for (i = 0; i < sampleNum; i++){
				j = nodes.size()-1 - i; 
				meanx_s += nodes.get(i).getX();
				meany_s += nodes.get(i).getY();
				meanx_e += nodes.get(j).getX();
				meany_e += nodes.get(j).getY();
			}
			meanx_s /= sampleNum;
			meany_s /= sampleNum;
			meanx_e /= sampleNum;
			meany_e /= sampleNum;			
		}
		t.setTrackTerminalPositions(meanx_s, meany_s, meanx_e, meany_e);
	}
	/**
	 * find successive track that actually is continued and merge does two tracks. 
	 * @return trackID number of the next track that should be linkable. 
	 */
	public Integer findLargeGapsButSimilarPositions(Track t, Tracks ts){
		double endstartdist;
		HashMap<Integer, Double> candidateList = new HashMap<Integer, Double>();
		Integer minid = -1;
		for (Integer candidateID : ts.keys()){
			Track candidate = ts.get(candidateID);
			if (candidate.getFrameStart() > t.getFrameEnd()) { //if the target track starts later than the current track
				endstartdist = endstartDistance(t, candidate);
				if (endstartdist < distance_threshould)
					candidateList.put(candidateID, endstartdist);
			} 	
		}
		if (candidateList.size() > 0){
			IJ.log(t.getTrackID() + "---");
			Object obj = Collections.min(candidateList.values());
			IJ.log("Minimum Distance" + (String) obj);
			 minid = getKeyByValue(candidateList, (Double) obj);
			IJ.log("track" + minid);
			IJ.log("---");
			for (Integer ids : candidateList.keySet())
				IJ.log(ids + ": " + candidateList.get(ids));
		} else 
			IJ.log(t.getTrackID() + ": No Candidate Found");
		
		return minid;
	}
	/**
	 * calculate Distance in XY plane between end point of a track (t) and
	 * start point of a track (target)
	 * @param t
	 * @param candidate
	 * @return
	 */
	double endstartDistance(Track t, Track candidate){
		CoordTwoD e = t.getTrackEndMeanPosition();
		CoordTwoD s = candidate.getTrackStartMeanPosition();
		return dist(e.x, e.y, s.x, s.y);
	}
	double dist(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow( (x1 - x2) , 2) + Math.pow( (y1 - y2) , 2));
	}
	
	/**
	 * fill tracks with continuous nodes by interplation of absent time points. 
	 * @return
	 */
	public Tracks interpolateGaps(){
		
		return tracks;
	}
	
	public Tracks getInterplatedTracks(){
		
		return tracks;
	}
	

	public Tracks associateLineages(){

		return tracks;
	}
	
	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}


	
}
