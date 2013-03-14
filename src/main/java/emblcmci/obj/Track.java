package emblcmci.obj;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Track class represents a track, consisteing of an ArrayList of Nodes. 
 * 
 *
 */
public class Track {
	public double areafracMAX;
	public double areafracMIN;
	int frameStart;
	int frameEnd;
	ArrayList<Integer> framelist = new ArrayList<Integer>();
	//HashMap<Integer, Node> nodes;
	ArrayList<Node> nodes;
	
	// parameters related to re-linking. 
	final double sampleframenumber = 3;
	double meanx_s;
	double meany_s;
	double meanx_e;
	double meany_e;
	
	
	public Track(ArrayList<Node> nodes){
		this.nodes = nodes;
	}
	public ArrayList<Node> getNodes(){
		return nodes;
	}	
	void detectFrameBounds(){
		checkFrameList();
		Object objmin = Collections.min(framelist);
		frameStart = (Integer) objmin;
		Object objmax = Collections.max(framelist);
		frameEnd = (Integer) objmax;
	}
	
	void checkFrameList(){
		if (framelist.size() == 0)
			for (Node n : nodes)
				framelist.add(n.getFrame());		
	}

	/**
	 * Calculate average positions of the track starting points and endpoints. 
	 */
	void calcMeanPositionBeginning(){
		checkFrameList();
		if (framelist.size() < sampleframenumber){
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
			for (i = 0; i < sampleframenumber; i++){
				j = nodes.size()-1 - i; 
				meanx_s += nodes.get(i).getX();
				meany_s += nodes.get(i).getY();
				meanx_e += nodes.get(j).getX();
				meany_e += nodes.get(j).getY();
			}
			meanx_s /= sampleframenumber;
			meany_s /= sampleframenumber;
			meanx_e /= sampleframenumber;
			meany_e /= sampleframenumber;			
		}
		
	}
	
	
	public ArrayList<Integer> getFramelist() {
		return framelist;
	}
	public int getFrameStart() {
		return frameStart;
	}
	public int getFrameEnd() {
		return frameEnd;
	}
}