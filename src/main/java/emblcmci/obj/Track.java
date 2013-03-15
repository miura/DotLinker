package emblcmci.obj;

import java.util.ArrayList;
import emblcmci.linker.Analyzer;

/**
 * Track class represents a track, consisteing of an ArrayList of Nodes. 
 * 
 * @author Kota Miura
 */
public class Track implements IBioObj{
	int trackID;
	public double areafracMAX;
	public double areafracMIN;
	int frameStart;
	int frameEnd;
	ArrayList<Integer> framelist = new ArrayList<Integer>();
	//HashMap<Integer, Node> nodes;
	ArrayList<Node> nodes;
	
	// parameters related to re-linking. 
	double meanx_s;
	double meany_s;
	double meanx_e;
	double meany_e;
	int candidateNextTrackID;
	
	public Track(ArrayList<Node> nodes){
		this.nodes = nodes;
	}
	public ArrayList<Node> getNodes(){
		return nodes;
	}
	
	/**
	 * modified Visitor pattern, to accept visits of Analyzer
	 */
	@Override
	public void accept(Analyzer analyzer) {
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
		return trackID;
	}
	public void setTrackID(int trackID){
		this.trackID = trackID;
	}
	
}