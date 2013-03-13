package emblcmci.obj;

import java.util.ArrayList;

/**
 * Track class represents a track, consisteing of an ArrayList of Nodes. 
 * 
 *
 */
public class Track {
	public double areafracMAX;
	public double areafracMIN;
	//HashMap<Integer, Node> nodes;
	ArrayList<Node> nodes;
	public Track(ArrayList<Node> nodes){
		this.nodes = nodes;
	}
	public ArrayList<Node> getNodes(){
		return nodes;
	}
}