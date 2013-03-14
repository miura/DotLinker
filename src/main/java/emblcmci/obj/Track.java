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
	public Track(ArrayList<Node> nodes){
		this.nodes = nodes;
	}
	public ArrayList<Node> getNodes(){
		return nodes;
	}
	
	void detectFrameBounds(){
		for (Node n : nodes)
			framelist.add(n.getFrame());
		Object objmin = Collections.min(framelist);
		frameStart = (Integer) objmin;
		Object objmax = Collections.max(framelist);
		frameEnd = (Integer) objmax;
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