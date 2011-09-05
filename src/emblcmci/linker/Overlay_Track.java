package emblcmci.linker;

import java.awt.Frame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

/**
 * Kota Miura
 * 
 */
public class Overlay_Track implements PlugIn {

	//double[][] xycoords; 
	       
	private boolean multicolor = false;
	private int colorid = 0;
	//private HashMap<Integer, Track> Tarcks;

	public void run(String arg) {
	    ImagePlus imp = IJ.getImage();
	    if (imp == null){
	    	IJ.error("no image to plot");
	    	return;
	    }
		Frame tracktable = WindowManager.getFrame("Tracks");
		if (tracktable == null) 
			return;
		TextWindow tt;
		if (!(tracktable instanceof TextWindow))
			return;
		else
			tt = (TextWindow) tracktable;
		ResultsTable trt = tt.getTextPanel().getResultsTable();

		int rowlength = trt.getColumn(0).length;

		HashMap<Integer, Track> Tracks = new HashMap<Integer, Track>();
		Track track;
		Node node;		
		for (int i = 0; i < rowlength; i++){
			node = new Node(
					trt.getValue("Xpos", i), 
					trt.getValue("Ypos", i),
					(int) trt.getValue("Area", i), 
					(int) trt.getValue("frame", i), 
					(int) trt.getValue("TrackID", i),
					i);			
			if (Tracks.get(node.trackID) == null){
				track =new Track(new ArrayList<Node>());
				Tracks.put(node.trackID, track);
			}
			else
				track = Tracks.get(node.trackID);
			track.nodes.add(node);
		
		}
		//this.Tarcks = Tracks;

		// calculate fraction of area to the first time point area
		for (Track v : Tracks.values()) //iterate for tracks
			if (v != null)
				calcAreaFraction(v);
		// get minimum and maximum fraction through all tracks
		double areafracMax = 0;
		double areafracMin =100;
		for (Track v : Tracks.values()){ //iterate for tracks
			if (v != null) {
				if (v.areafracMIN < areafracMin) areafracMin = v.areafracMIN;
				if (v.areafracMAX > areafracMax) areafracMax = v.areafracMAX;
			}
		}
		IJ.log("Area Fraction Minimum: " + areafracMin);
		IJ.log("Area Fraction Maximum: " + areafracMax);
		if (areafracMax > 2.0){
			areafracMax = 2.0;
			IJ.log("... areaFracMax Corrected to:" + areafracMax);
		}
		
		
		if (arg.equals("plot"))
			TrackPlotter(Tracks, imp);
		if (arg.equals("area"))
			AreaPlotter(Tracks, imp, areafracMin, areafracMax);
	}

	public void AreaPlotter(HashMap<Integer, Track> Tracks, ImagePlus imp,
			double areafracMin, double areafracMax){
		int ChosenTrackNumber = (int) IJ.getNumber("Choose a Track (if 0, all tracks)", 1);
		Track track;

		if (ChosenTrackNumber != 0){
			track = Tracks.get(ChosenTrackNumber);
			if (track != null)
				trackAreaColorCoder(imp, track, areafracMin, areafracMax);
			else
				IJ.showMessageWithCancel("No Track", "no such track could be found");
		} else {
			for (Object v : Tracks.values()) //iterate for tracks
				if (v != null)
					trackAreaColorCoder(imp, (Track) v, areafracMin, areafracMax);
		}
		imp.updateAndDraw();

	}
	
	public boolean trackAreaColorCoder(ImagePlus imp, Track track, double areafracMin, double areafracMax){
		int areascale = 0; 
		Iterator<Node> iter = track.nodes.iterator();
		Node n;
		int counter = 0;
		double areaFrac;
		while (iter.hasNext()) {
			n = iter.next();
			// normalize to 255 scale. 0 will be the segmented background, black
			areaFrac = n.areafraction;
			if (areaFrac > areafracMax)
				areaFrac = areafracMax;
			areascale = (int) Math.floor( 
					((areaFrac - areafracMin) 
							/ (areafracMax - areafracMin))* 255 +1);
//			IJ.log(""+counter+": area fraction=" + 
//					n.areafraction + " 255 scaled = " + areascale);
			fillArea(imp, n, areascale);
			counter++;
		}
		return true;
	}
	
	
	public void fillArea(ImagePlus imp, Node n, int areascale){
		if (n == null) return;
		//imp.setSlice(n.getFrame()+1);	//n.frame starts from 0, but slice number starts from 1
		if (imp.getRoi() != null)
			imp.killRoi();
		ImageProcessor ip = imp.getStack().getProcessor(n.getFrame()+1);
		//ip.setRoi((Roi) null);
		PolygonRoi wandroi = wandRoi(ip, n.getX(), n.getY(), n.getFrame()+1);
		//there should be set color here.
		if (wandroi != null){
			ip.setColor(areascale);		//value according to own reference area.
			//wandroi.drawPixels(ip); //this may connect neighboring cells
			ip.fill(wandroi);
		} else {
			IJ.log("Null ROI returned: id" + n.id + 
					" frame:" + n.frame + 
					" AreaFrac:" + n.areafraction + 
					" X:" + n.getX() +
					" Y:" + n.getY() +
					" trackID:" + n.trackID
					);
		}
	}
	
	/**
	 * Does auto-wand at the given coordinate in given slice, and return a polygon ROI.
	 * @param imp: supposed to be stack. 
	 * @param wandx
	 * @param wandy
	 * @param slicenum
	 * @return
	 */
	public PolygonRoi wandRoi(ImageProcessor ip, double wandx, double wandy, int slicenum){
		PolygonRoi wandroi = null;
		Wand wand = new Wand(ip);
		int currentpixvalue = ip.getPixel((int) wandx, (int) wandy);
		if (currentpixvalue == 255){
			wand.autoOutline((int) wandx, (int) wandy, currentpixvalue, currentpixvalue, wand.EIGHT_CONNECTED);
			wandroi = new PolygonRoi(wand.xpoints, wand.ypoints, wand.npoints, Roi.FREEROI);
		}
		return wandroi;
	}
	
	public void calcAreaFraction(Track track){
		Iterator<Node> iter = track.nodes.iterator();
		double area0 = (double) track.nodes.get(0).area;
		double carea;
		//IJ.log("first time point arera: " + area0);
		int counter = 0;
		Node n;
		double minimum = 1000;
		double maximum = 0;
		while (iter.hasNext()) {
			n = iter.next();
			carea = (double) n.area;
			n.areafraction = carea / area0;			
			counter++;
			if (n.areafraction < minimum)
				minimum = n.areafraction;
			
			if (n.areafraction > maximum)
				maximum = n.areafraction;
		}
		track.areafracMIN = minimum;
		track.areafracMAX = maximum;		
	}
	
	
	
	public void TrackPlotter(HashMap<Integer, Track> Tracks, ImagePlus imp){
		int ChosenTrackNumber = (int) IJ.getNumber("Choose a Track (if 0, all tracks)", 1);
		Track track;
		
		if (ChosenTrackNumber != 0){
			track = Tracks.get(ChosenTrackNumber);
			if (track != null){
				plotTrack(track, imp);
			} else {
				IJ.showMessageWithCancel("No Track", "no such track could be found");
			}
		} else {
			multicolor  = true;
			for (Object v : Tracks.values()) //iterate for tracks
				if (v != null)
					plotTrack((Track) v, imp);
		}

	}

	
	public void plotTrack(Track track, ImagePlus imp){
		Color plotcolor = new Color(255, 0, 0); //red
		if (multicolor) {
			int[] rgb = returnPhysicsLut();
			plotcolor = new Color(rgb[0], rgb[1], rgb[2]);
		}
		Iterator<Node> iter = track.nodes.iterator();
		Node n;
		int[] xA = new int[track.nodes.size()];
		int[] yA = new int[track.nodes.size()];

		int counter = 0;
		while (iter.hasNext()) {
			n = iter.next();
			//IJ.log("index:" + n.id + "- " + n.x + "," +n.y);
			xA[counter] = (int) Math.round(n.x);
			yA[counter] = (int) Math.round(n.y);
			counter++;
		}
		Roi roi1 = new PolygonRoi( xA, yA, xA.length, Roi.POLYLINE);
		//		imp.setRoi(roi1);
		//roi1.setStrokeColor(Color.red);
		roi1.setStrokeColor(plotcolor);
		roi1.setStrokeWidth(2.0);
		Overlay overlay = imp.getOverlay();
		if (overlay == null){
			overlay = new Overlay();
			imp.setOverlay(overlay);
		}
		overlay.add(roi1);			
		imp.updateAndDraw() ;
	}
	
	
	public class Node {
		double x;
		double y;
		int area;
		int frame;		
		int trackID;
		int id;
		double areafraction;	//fraction of area compared to the first time point in the trajectory
		
		public Node(double x, double y, int area, int frame, int trackID, int id){
			this.x = x;
			this.y = y;
			this.area = area;
			this.frame = frame;
			this.trackID = trackID;
			this.id = id;			
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

		public int getArea() {
			return area;
		}
		public void setArea(int area) {
			this.area = area;
		}

		public int getFrame() {
			return frame;
		}
		public void setFrame(int frame) {
			this.frame = frame;
		}
		 
	}
	public class Track {
		public double areafracMAX;
		public double areafracMIN;
		//HashMap<Integer, Node> nodes;
		ArrayList<Node> nodes;
		public Track(ArrayList<Node> nodes){
			this.nodes = nodes;
		}
		 
	}
	
	public int[] returnPhysicsLut(){
		int index = (int) Math.floor((Math.random()*256));
		int[] r = new int[]{47,46,45,44,42,41,40,39,37,36,35,33,32,30,29,27,26,24,23,21,19,18,16,14,12,10,9,7,5,3,1,0,0,0,0,0,0,0,0,0,1,2,3,3,4,5,6,7,8,8,9,10,11,12,13,14,15,16,16,17,18,19,20,21,22,23,24,25,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,26,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,23,23,23,23,23,23,23,23,23,23,23,23,23,22,22,22,27,32,37,42,48,53,58,63,69,74,79,85,90,96,101,107,112,118,123,-127,-122,-116,-111,-108,-105,-103,-100,-97,-95,-92,-90,-87,-84,-82,-79,-76,-74,-71,-68,-66,-63,-60,-58,-55,-52,-50,-46,-43,-40,-36,-34,-34,-34,-34,-34,-33,-33,-33,-33,-33,-33,-32,-32,-32,-32,-32,-31,-31,-31,-31,-31,-31,-30,-30,-30,-30,-30,-29,-29,-29,-29,-29,-28,-28,-28,-28,-28,-28,-27,-27,-27,-27,-27,-26,-26,-26,-26,-26,-26,-25,-25,-25,-25,-25,-24,-24,-24,-24,-24,-23};
		int[] g = new int[]{2,2,2,2,2,2,2,2,2,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,1,3,5,7,9,11,13,15,18,20,23,26,28,31,33,36,39,41,44,47,49,52,55,58,60,63,66,69,71,74,77,80,82,85,88,91,94,96,99,102,105,108,111,114,116,119,122,125,-128,-125,-122,-119,-116,-113,-110,-107,-104,-101,-98,-95,-92,-89,-86,-83,-80,-77,-74,-71,-68,-65,-62,-58,-58,-58,-58,-58,-57,-57,-57,-57,-57,-56,-56,-56,-56,-56,-55,-55,-55,-55,-54,-54,-54,-53,-53,-53,-52,-52,-52,-51,-51,-51,-50,-50,-50,-49,-49,-49,-48,-48,-48,-47,-47,-47,-46,-46,-46,-45,-45,-45,-44,-44,-44,-43,-43,-43,-42,-42,-42,-41,-41,-41,-40,-40,-40,-39,-39,-39,-39,-39,-38,-38,-38,-38,-38,-38,-37,-37,-37,-37,-37,-37,-37,-36,-36,-36,-36,-36,-36,-35,-35,-35,-35,-35,-35,-36,-39,-42,-44,-47,-50,-53,-56,-59,-62,-65,-68,-71,-74,-78,-81,-84,-87,-90,-93,-96,-99,-102,-105,-108,-111,-115,-118,-121,-124,-127,126,122,119,116,113,110,106,103,100,97,94,90,87,84,80,77,74,71,67,64,61,57,54,51,47,44,41,37,34};
		int[] b = new int[]{119,120,122,123,124,125,126,127,-128,-127,-125,-124,-123,-122,-121,-120,-119,-118,-117,-115,-114,-113,-112,-111,-110,-109,-108,-107,-105,-104,-103,-102,-101,-100,-99,-98,-96,-95,-94,-93,-92,-91,-90,-89,-88,-87,-87,-86,-85,-84,-83,-82,-81,-80,-79,-78,-77,-76,-75,-74,-73,-72,-71,-70,-69,-68,-67,-66,-65,-65,-65,-65,-64,-64,-64,-64,-64,-63,-63,-63,-63,-63,-62,-62,-62,-62,-62,-61,-61,-61,-61,-61,-60,-60,-60,-60,-60,-59,-59,-59,-59,-59,-58,-61,-64,-66,-69,-72,-75,-78,-80,-83,-86,-89,-91,-94,-97,-100,-103,-106,-108,-113,-118,-123,-127,124,119,114,109,104,99,94,89,84,79,74,69,64,59,54,49,44,39,33,28,23,22,22,22,22,22,22,22,22,22,21,21,21,21,21,21,21,21,21,21,21,21,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,20,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,16,16,16,16,16,16,16,16,16,16,16,16};
		//ArrayList<int[]> rgb = new ArrayList<int[]>();
		int[] rgb = new int[3];
		rgb[0] = r[index]+128;
		rgb[1] = g[index]+128;
		rgb[2] = b[index]+128;
//		if (i == 0){
//			IJ.log("r"+rgb[0]);
//			IJ.log("g"+rgb[1]);
//			IJ.log("b"+rgb[2]);
//		}
		return rgb;
	}
}