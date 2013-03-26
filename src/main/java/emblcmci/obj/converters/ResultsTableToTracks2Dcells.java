package emblcmci.obj.converters;

import java.util.ArrayList;
import java.util.Collections;

import ij.measure.ResultsTable;
import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;
import emblcmci.obj.Track2Dcells;
import emblcmci.obj.Tracks2Dcells;

/**
 * Converts data in Results Table to HashMap of individual Track.
 * Each track is a class with Node objects stroed as an AraryList.
 * 
 * Implementation: + Area version, 
 * requires xy coordinates, frame number, TrackID & Area Info (see the code)
 * 
 * @param trt
 * @return
 */

public class ResultsTableToTracks2Dcells extends AbstractResultsTableToTracks {

	public ResultsTableToTracks2Dcells(ResultsTable trt) {
		super(trt);
		// TODO Auto-generated constructor stub
	}

//	@Override
//	public AbstractTracks run(ResultsTable trt) {
//		boolean Areadata_Exists = false;
//		if (trt == null){
//			IJ.error("no track data available");
//			return null;
//		}
//
//		if (Areadata_Exists){
//			int rowlength = trt.getColumn(0).length;
//			if (rowlength < 10){		//this 10 is just meaning too few data number. 
//				IJ.error("... it seems that there are very few data available in the table");
//				return null;
//			}
//			Tracks2Dcells tracks = new Tracks2Dcells();
//			Track2Dcells track;
//			Node node;		
//			for (int i = 0; i < rowlength; i++){
//					node = generateNodeWithArea(trt, i);
//				if (tracks.get(node.getTrackID()) == null){
//					track =new Track2Dcells(new ArrayList<Node>());
//					tracks.put(node.getTrackID(), track);
//				} else
//					track = tracks.get(node.getTrackID());
//				track.getNodes().add(node);
//
//			}
//			// calculate some of track parameters. 
//			for (Track2Dcells v : tracks.values()) {//iterate for tracks
//				if (v != null) {
//					//				v.detectFrameBounds();
//					//				v.calcMeanPositionBeginning();
//					if (Areadata_Exists)
//						// calculate fraction of area to the first time point area
//						calcAreaFractionMinMax(v);
//					//calcAreaFraction(v); // commented out, since this value is now calculated in DotLinker
//				}
//			}
//			return tracks;
//		} else
//			return null;
//	}
	
	public void calcAreaFractionMinMax(Track2Dcells track){
		ArrayList<Double> af = new ArrayList<Double>();
		for (Node n :  track.getNodes())
			af.add(n.getAreaFraction());
		Object minobj = Collections.min(af);
		Object maxobj = Collections.max(af);
		track.setAreafracMIN( (Double) minobj);
		track.setAreafracMAX( (Double) maxobj);		
	}

	@Override
	Node generateNode(Integer i) {
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

	@Override
	AbstractTracks createTracks() {
		return new Tracks2Dcells();
	}

	@Override
	AbstractTrack createTrack() {
		return new Track2Dcells(new ArrayList<Node>());
	}

	@Override
	boolean checkHeaders() {
		if (	trt.getColumnHeadings().contains("Xpos") &&
				trt.getColumnHeadings().contains("Ypos") &&
				trt.getColumnHeadings().contains("frame") &&
				trt.getColumnHeadings().contains("TrackID") &&
				trt.getColumnHeadings().contains("Area") && 
				trt.getColumnHeadings().contains("AreaFraction")
			){
			return true;
		} else
			return false;
	}

	@Override
	boolean checkHeaderLength(int rowlength) {
		if (rowlength > 6)
			return true;
		else
			return false;
	}

	@Override
	void calcTrackParameters() {
		// TODO Auto-generated method stub
		
	}


}
