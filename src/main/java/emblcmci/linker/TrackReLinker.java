package emblcmci.linker;

import ij.IJ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.CoordTwoD;
import emblcmci.obj.Node;
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
public class TrackReLinker extends LinkAnalyzer {
	AbstractTracks tracks;
	Tracks updatedTracks;
	
	/**
	 * Frame numbers to sdample from track starting or ending terminal
	 * to calculate their aavarage positions.  
	 */
	final int TERMINAL_SAMPLING_FRAME_NUMBER = 3;
	
	/** Maximum allowd distance between a track end point and a track start point.
	/* unit: pixels. 
	 */
	int distance_threshould = 10;
	
	// allowed number of missing frames between tracks for interpolation. 
	// unit: frames (within initially constracted tracks)
	int framegap_allowance = 2;
	
	/**
	 * Maximum range of frame gap that allows to link gaps. 
	 */
	private int ALLOWE_FRAME_DIFFERENCE = 5;
	
//	/**
//	 * @param tracks
//	 */
//	public TrackReLinker(Vector<Trajectory> tracks) {
//		super();
	//		VecTrajectoryToTracks v2t = new VecTrajectoryToTracks();
	//		this.tracks = v2t.runsimple(tracks);
	//	}
	
	/**
	 * @param aLLOWE_FRAME_DIFFERENCE
	 */
	public TrackReLinker(int aLLOWE_FRAME_DIFFERENCE) {
		super();
		ALLOWE_FRAME_DIFFERENCE = aLLOWE_FRAME_DIFFERENCE;
	}


	/**
	 * 
	 */
	@Override 
	public void analyze(AbstractTrack t) {
		Integer estimatedNextId;
		estimatedNextId = findLargeGapsButSimilarPositions(t, this.tracks);
		if (estimatedNextId > -1)
			t.setCandidateNextTrackID(estimatedNextId);
		else
			// no successive track. 
			t.setCandidateNextTrackID(-1);
		
	}
		



	@Override 
	public void analyze(AbstractTracks ts) {
		this.tracks = ts;
		for (AbstractTrack t : ts.values()) {
			// preparation: calculate some of track parameters.
			calcMeanPositionBeginning( t );			
		}
		for (AbstractTrack t : ts.values()) //iterate for tracks
				t.accept(this);
		updateTracks(ts);
	}

	/**
	 * Calculate average positions of the track starting points and endpoints. 
	 */
	void calcMeanPositionBeginning(AbstractTrack t){
		int sampleNum = TERMINAL_SAMPLING_FRAME_NUMBER;
		double meanx_s;
		double meany_s;
		double meanx_e;
		double meany_e;		
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
	public Integer findLargeGapsButSimilarPositions(AbstractTrack t, AbstractTracks ts){
		double endstartdist;
		int framedifference;
		//HashMap<Integer, Double> candidateList = new HashMap<Integer, Double>();
		HashMap<Integer, Integer> candidateList = new HashMap<Integer, Integer>();

		Integer minid = -1;
		for (Integer candidateID : ts.keySet()){
			AbstractTrack candidate = ts.get(candidateID);
			framedifference = candidate.getFrameStart() - t.getFrameEnd();
			//select target track that starts later than the current track
			if ((framedifference > 0) && (framedifference < ALLOWE_FRAME_DIFFERENCE )){
				// select tracks within certain distance
				endstartdist = endstartDistance(t, candidate);
				if (endstartdist < distance_threshould)
					candidateList.put(candidateID, candidate.getFramelist().size()); // new idea
					//candidateList.put(candidateID, endstartdist);
					
			} 	
		}
		if (candidateList.size() > 0){
			IJ.log("### Track: " + t.getTrackID());		
			//Object obj = Collections.min(candidateList.values());
			Object obj = Collections.max(candidateList.values());
			//minid = getKeyByValue(candidateList, (Double) obj);
			minid = getKeyByValue(candidateList, (Integer) obj);
			//IJ.log("   Minimum Distance" + Double.toString((Double) obj));
			IJ.log("   longest track in the surrounding" + Double.toString((Integer) obj));			
			IJ.log("   ...track" + minid);
			IJ.log("   end frame: " + t.getFrameEnd());
			IJ.log("   start frame: " + ts.get(minid).getFrameStart());
			IJ.log("---");
			for (Integer ids : candidateList.keySet())
				IJ.log(ids + ": " + candidateList.get(ids));
		} 
		
		return minid;
	}
	/**
	 * calculate Distance in XY plane between end point of a track (t) and
	 * start point of a track (target)
	 * @param t
	 * @param candidate
	 * @return
	 */
	double endstartDistance(AbstractTrack t, AbstractTrack candidate){
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
	public AbstractTracks interpolateGaps(){
		
		return tracks;
	}
	
	public AbstractTracks getInterplatedTracks(){
		
		return tracks;
	}
	

	public AbstractTracks associateLineages(){

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
	
	/** update tracks according to the merging candidate list. 
	 * 
	 * @param ts
	 * @param t
	 * @param imp
	 */
	public void updateTracks(AbstractTracks ts){
		Iterator<AbstractTrack> iter = ts.iterator();
		ArrayList<Integer> removedlist = new ArrayList<Integer>();
		while(iter.hasNext()){
			AbstractTrack currentT = iter.next();
			if ((currentT.getCandidateNextTrackID() > 0) &&
			(!removedlist.contains(currentT.getTrackID()))) {
				ArrayList<Integer> tlist = new ArrayList<Integer>();
				tlist.add(currentT.getCandidateNextTrackID());
				tlist = getTrackLists(tlist, ts);
				for (Integer id : tlist)
					if (id > 0) {
						currentT.concatTracks(ts.get(id));
						removedlist.add(id);
					}
				currentT.checkFrameList();
				IJ.log(Integer.toString(currentT.getTrackID()) + " Merged with:" + 
					tlist.toString() 
					+ " Nodes: " + currentT.getNodes().size() 
					+ " framespan:" + ( currentT.getFrameEnd() - currentT.getFrameStart() + 1));
			}
		}
		for (Integer id : removedlist)
			ts.removeTrack(id);
		IJ.log("Merged and removed:" + removedlist.toString());
	}
	
	/**
	 * recursively explore the "track threads" for listing tracks to merge. 
	 * @TODO this could be globally optimized as well. 
	 * 
	 * @param tlist
	 * @param tracks
	 * @return
	 */
	public ArrayList<Integer> getTrackLists(ArrayList<Integer> tlist, AbstractTracks tracks){
		AbstractTrack thistrack = tracks.get(tlist.get(tlist.size() - 1));
		if (thistrack.getCandidateNextTrackID() > 0){
			tlist.add(thistrack.getCandidateNextTrackID());
			getTrackLists(tlist, tracks);
		} 
		return tlist;
	}

	
}
