package emblcmci.obj;

import ij.IJ;
import ij.measure.ResultsTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Converts data in table to HashMap of individual Track.
 * Each track is a class with Node objects stroed as an AraryList.  
 * 
 * @param trt
 * @return
 */

public class ResultTableToTracks {

	public HashMap<Integer, Track> run(ResultsTable trt){
		boolean Areadata_Exists = false;
		if (trt == null){
			IJ.error("no track data available");
			return null;
		}
		int rowlength = trt.getColumn(0).length;
		if (rowlength < 10){		//this 10 is just meaning too few data number. 
			IJ.error("... it seems that there are very few data available in the table");
			return null;
		}
		if (trt.getColumnHeadings().contains("Area") && trt.getColumnHeadings().contains("AreaFraction"))
			Areadata_Exists = true;
		
		HashMap<Integer, Track> Tracks = new HashMap<Integer, Track>();
		Track track;
		Node node;		
		for (int i = 0; i < rowlength; i++){
			if (Areadata_Exists)
				node = generateNodeWithArea(trt, i);
			else
				node = generateNode(trt, i);	

			if (Tracks.get(node.getTrackID()) == null){
				track =new Track(new ArrayList<Node>());
				Tracks.put(node.getTrackID(), track);
			} else
				track = Tracks.get(node.getTrackID());
			track.getNodes().add(node);
		
		}
		// calculate some of track parameters. 
		for (Track v : Tracks.values()) {//iterate for tracks
			if (v != null) {
//				v.detectFrameBounds();
//				v.calcMeanPositionBeginning();
				if (Areadata_Exists)
				// calculate fraction of area to the first time point area
					calcAreaFractionMinMax(v);
				//calcAreaFraction(v); // commented out, since this value is now calculated in DotLinker
			}
		}
		return Tracks;
	}

	Node generateNode(ResultsTable trt, int i){
		Node node = new Node(
				trt.getValue("Xpos", i), 
				trt.getValue("Ypos", i),
				(int) trt.getValue("frame", i), 
				(int) trt.getValue("TrackID", i),
				i);
		return node;
	}
	
	Node generateNodeWithArea(ResultsTable trt, int i){
		Node node = new Node(
				trt.getValue("Xpos", i), 
				trt.getValue("Ypos", i),
				(int) trt.getValue("Area", i), 
				(int) trt.getValue("frame", i), 
				(int) trt.getValue("TrackID", i),
				(double) trt.getValue("AreaFraction", i),
				i);
		return node;
	}

//	public void calcAreaFraction(Track track){
//		Iterator<Node> iter = track.getNodes().iterator();
//		double area0 = (double) track.getNodes().get(0).getArea();
//		double carea;
//		//IJ.log("first time point arera: " + area0);
//		Node n;
//		double minimum = 1000;
//		double maximum = 0;
//		while (iter.hasNext()) {
//			n = iter.next();
//			carea = (double) n.getArea();
//			n.setAreaFraction( carea / area0);			
//			if (n.getAreaFraction() < minimum)
//				minimum = n.getAreaFraction();
//			
//			if (n.getAreaFraction() > maximum)
//				maximum = n.getAreaFraction();
//		}
//		track.areafracMIN = minimum;
//		track.areafracMAX = maximum;		
//	}
	
	public void calcAreaFractionMinMax(Track track){
		ArrayList<Double> af = new ArrayList<Double>();
		for (Node n :  track.getNodes())
			af.add(n.getAreaFraction());
		Object minobj = Collections.min(af);
		Object maxobj = Collections.max(af);
		track.areafracMIN = (Double) minobj;
		track.areafracMAX = (Double) maxobj;		
	}
	

}