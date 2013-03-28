package emblcmci.obj;

import ij.gui.Roi;
import ij.process.ImageProcessor;

/**
 * Node class represents a single cell (particle) in a single time point.
 * All cell parameters will be stored in this object
 *
 */
public class Node {
	double x;
	double y;
	int area;
	int frame;		
	int trackID;
	private int id;
	double areafraction;	//fraction of area compared to the first time point in the trajectory

	ImageProcessor orgip;	// binary image of the node
	ImageProcessor binip;	// binary image of the node
	Roi orgroi;				// roi in the original frame: for knowing the original coordinates and size. 

	public boolean toRemove = false;
	public Node(double x, double y, int frame,  int id){
		this.x = x;
		this.y = y;
		this.frame = frame;
		this.setId(id);
	}
	
	public Node(double x, double y, int frame, int trackID, int id){
		this.x = x;
		this.y = y;
		this.frame = frame;
		this.trackID = trackID;
		this.setId(id);	
	}
		
	public Node(double x, double y, int area, int frame, int trackID, double areafraction, int id){
		this.x = x;
		this.y = y;
		this.area = area;
		this.frame = frame;
		this.trackID = trackID;
		this.areafraction = areafraction;
		this.setId(id);			
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getTrackID(){
		return trackID;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}

	public double getAreaFraction(){
		return areafraction;
	}

	public void setAreaFraction(double areafraction){
		this.areafraction = areafraction;
	}
	public int getFrame() {
		return frame;
	}
	public void setFrame(int frame) {
		this.frame = frame;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public ImageProcessor getOrgip() {
		return orgip;
	}

	public void setOrgip(ImageProcessor orgip) {
		this.orgip = orgip;
	}	
	
	public ImageProcessor getBinip() {
		return binip;
	}

	public void setBinip(ImageProcessor binip) {
		this.binip = binip;
	}

	public Roi getOrgroi() {
		return orgroi;
	}

	public void setOrgroi(Roi orgroi) {
		this.orgroi = orgroi;
	}
	 
}
