package de.embl.cmci.obj.converters;

import java.util.ArrayList;
import java.util.Collections;

import de.embl.cmci.obj.AbstractTrack;
import de.embl.cmci.obj.AbstractTracks;
import de.embl.cmci.obj.Node;
import de.embl.cmci.obj.Track2Dcells;
import de.embl.cmci.obj.Tracks2Dcells;

import ij.measure.ResultsTable;

/**
 * Converts data in Results Table to Tracks instance holding multiple Track instances.
 * Each Track is a class with Node objects stroed as an AraryList.
 * 
 * Implementation: + Area version, 
 * requires xy coordinates, frame number, TrackID & Area Info (see the code)
 * ... initially coded for Tina, for Drosophila epidermis multicellular tracking. 
 *   
 * @param trt: ImageJ results table instance. 
 * @return
 */

public class ResultsTableToTracks2Dcells extends AbstractResultsTableToTracks {

	public ResultsTableToTracks2Dcells(ResultsTable trt) {
		super(trt);
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
//					 calculate some of track parameters. 
			for (AbstractTrack t : this.tracks.values()) {//iterate for tracks
				if ((t != null) && (t instanceof Track2Dcells)) {
					Track2Dcells t2dcell = (Track2Dcells) t;
							// calculate fraction of area to the first time point area
					calcAreaFractionMinMax(t2dcell);
				//calcAreaFraction(v); // commented out, since this value is now calculated in DotLinker
				}
			}
//			return tracks;
		
	}

	public void calcAreaFractionMinMax(Track2Dcells track){
		ArrayList<Double> af = new ArrayList<Double>();
		for (Node n :  track.getNodes())
			af.add(n.getAreaFraction());
		Object minobj = Collections.min(af);
		Object maxobj = Collections.max(af);
		track.setAreafracMIN( (Double) minobj);
		track.setAreafracMAX( (Double) maxobj);		
	}



}
