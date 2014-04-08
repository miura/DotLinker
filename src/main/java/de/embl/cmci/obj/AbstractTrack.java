package emblcmci.obj;

import java.util.ArrayList;
import java.util.Collections;

import de.embl.cmci.linker.LinkAnalyzer;


public abstract class AbstractTrack implements ILinkAnalysisElement{
	int trackID;
	int frameStart; // frame starts from 1
	int frameEnd;
	ArrayList<Integer> framelist;// = new ArrayList<Integer>();
	//HashMap<Integer, Node> nodes;
	ArrayList<Node> nodes;
	
	// parameters related to re-linking. 
	double meanx_s;
	double meany_s;
	double meanx_e;
	double meany_e;
	int candidateNextTrackID;
	
	/**
	 * If this track is a merged track, original tracks will be 
	 * listed in this field parameter. 
	 */
	ArrayList<Integer> srcTracks;
	
	public AbstractTrack(ArrayList<Node> nodes){
		this.nodes = nodes;
		if (this.nodes.size() > 0){
			checkFrameList();
		}
	}
	public ArrayList<Node> getNodes(){
		return nodes;
	}
	
	/**
	 * modified Visitor pattern, to accept visits of Analyzer
	 */
	@Override
	public void accept(LinkAnalyzer analyzer) {
		analyzer.analyze(this);
	}
	
	public void setTrackTerminalPositions(
			double meanx_s2, double meany_s2, 
			double meanx_e2, double meany_e2){
		this.meanx_s = meanx_s2;
		this.meany_s = meany_s2;
		this.meanx_e = meanx_e2;
		this.meany_e = meany_e2;		
	}
	
//	/**
//	 * unused. Merged track is a new instance. 
//	 * @param t2
//	 * @return
//	 */
//	public AbstractTrack mergeTracks(AbstractTrack t2){
//		ArrayList<Node> mergedNode = new ArrayList<Node>();
//		mergedNode.addAll(this.getNodes());
//		mergedNode.addAll(t2.getNodes());
//		AbstractTrack mergedTrack = new AbstractTrack(mergedNode);		
//		return mergedTrack;
//	}
	/**
	 * Concatenates a track to the end of the current track. 
	 * @param t2
	 * @return
	 */
	public boolean concatTracks(AbstractTrack t2){
		this.getNodes().addAll(t2.getNodes());
		
		// keep the merged track information. 
		if (srcTracks == null)
			srcTracks = new ArrayList<Integer>();
		srcTracks.add(t2.getTrackID());
		return true;
	}	
	
	public void checkFrameList(){
		AbstractTrack t = this;
		//if (t.getFramelist().size() == 0)
		if (framelist == null)
			framelist = new ArrayList<Integer>();
		if (t.getNodes().size() != t.getFramelist().size()){
			framelist.clear();
			for (Node n : t.getNodes())
				framelist.add(n.getFrame());
		}
		detectFrameBounds();
	}
	
	/**
	 * a clean up method, after all track merging is done, reset the identity of each node's 
	 * belonging TrackID to correct ones. 
	 */
	public void resetNodeTrackIDs(){
		for (Node n : nodes){
			n.setTrackID(this.trackID);
		}
	}
	/**
	 * preparation for evaluating tracks. 
	 * Store start frame and end frame of a track in the Track object. 
	 * @param t
	 */
	public void detectFrameBounds(){
		int frameStart;
		int frameEnd;
		Object objmin = Collections.min(this.getFramelist());
		frameStart = (Integer) objmin;
		Object objmax = Collections.max(this.getFramelist());
		frameEnd = (Integer) objmax;
		this.setFrameStart(frameStart);
		this.setFrameEnd(frameEnd);
	}
	
	public CoordTwoD getTrackStartMeanPosition(){
		return (new CoordTwoD(this.meanx_s, this.meany_s));
	}
	public CoordTwoD getTrackEndMeanPosition(){
		return (new CoordTwoD(this.meanx_e, this.meany_e));
	}
	public ArrayList<Integer> getFramelist() {
		return framelist;
	}
	public int getFrameStart() {
		return frameStart;
	}
	public void setFrameStart(int frame){
		this.frameStart = frame;
	}
	public int getFrameEnd() {
		return frameEnd;
	}
	public void setFrameEnd(int frame){
		this.frameEnd = frame;
	}
	public int getTrackID(){
		return this.trackID;
	}
	public void setTrackID(int trackID){
		this.trackID = trackID;
	}
	public int getCandidateNextTrackID() {
		return candidateNextTrackID;
	}
	public void setCandidateNextTrackID(int candidateNextTrackID) {
		this.candidateNextTrackID = candidateNextTrackID;
	}
	
	public Node getStartNode(){
		return nodes.get(0);
	}
	public Node getEndNode(){
		return nodes.get(nodes.size()-1);
	}
	
}