package de.embl.cmci.linker.plotter;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Iterator;

import de.embl.cmci.obj.AbstractTrack;
import de.embl.cmci.obj.AbstractTracks;
import de.embl.cmci.obj.Node;
import de.embl.cmci.obj.Tracks;
import de.embl.cmci.obj.converters.AbstractResultsTableToTracks;


import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;
import ij.text.TextWindow;

public abstract class AbstractViewDynamics {
	
	ResultsTable trackResultsTable;
	protected ImagePlus imp;
	protected boolean multicolor = false;	

	/**
	 * Constructor. 
	 * @param imp: an image stack which will be plotted.
	 */
	public AbstractViewDynamics(ImagePlus imp){
		this.imp = imp;
	}
	
	/** 
	 * returns ResultsTable object according to the Window name passed as an argument 
	 * @param TableName: Default would be "Tracks", TextWindow with data of Tracks. 
	 * @return
	 */	
	public ResultsTable getTrackTable(String TableName){
		Frame tracktable = WindowManager.getFrame(TableName);
		if (tracktable == null) 
			return null;
		TextWindow tt;
		if (!(tracktable instanceof TextWindow))
			return null;
		else
			tt = (TextWindow) tracktable;
		ResultsTable trt = tt.getTextPanel().getResultsTable();
		
		return trt;
	}
	
	/**
	 * Does the plotting of tracks. 
	 * 
	 * In dialog, choosing track ID will do the plotting of that single track. 
	 * choosing 0 as the trackID will do plotting of all tracks in the data table.
	 * 
	 * Color of the tracks are randomely chosen from LUT "physics".
	 * 
	 * @TODO progressive drawing of tracks. 
	 */
	public void plotTracks(){
		plotTracks(this.imp);
	}	

	/**
	 * Does plotting to a specific ImagePlus object based on the currently open ResultsTable
	 * @param outimp
	 */
	public void plotTracks(ImagePlus outimp){
		ResultsTable trt = getTrackTable("Tracks");
		AbstractResultsTableToTracks rttracks = convertResultsTable(trt);
		rttracks.run();
		AbstractTracks tracks = rttracks.getTracks();
		trackPlotter(tracks, outimp);
	}
	
	public abstract AbstractResultsTableToTracks convertResultsTable(ResultsTable trt);

	public void plotTracks(Tracks tracks, ImagePlus outimp){
		trackPlotter(tracks, outimp);
	}
	
	public void trackPlotter(AbstractTracks tracks, ImagePlus imp){

		int defaultID = 1;
		//if there is a pointROI, then search for the track closest to the ROI.
		if (imp.getRoi() != null){
			Roi pntroi = imp.getRoi();
			defaultID = tracks.getTrackClosesttoPointROI(pntroi);
			imp.killRoi();
		}		
		int ChosenTrackNumber = (int) IJ.getNumber("Choose a Track (if 0, all tracks)", defaultID);

		AbstractTrack track;

		if (ChosenTrackNumber != 0){
			track = tracks.get(ChosenTrackNumber);
			if (track != null){
				//plotTrack(track, imp);
        plotProgressiveTrack(track, imp);
			} else {
				IJ.showMessageWithCancel("No Track", "no such track could be found");
			}
		} else 
			trackAllPlotter(tracks, imp);
	}

	public void trackAllPlotter(AbstractTracks tracks, ImagePlus imp){
			multicolor  = true;
			IJ.log("Plotting All Tracks...");
			for (AbstractTrack v : tracks.values()) //iterate for tracks
				if (v != null){
					//plotTrack((Track) v, imp);
					TrackLabeling.doAlable(imp, v);
					plotProgressiveTrack(v, imp);
				}
	}
	
	/**
	 * simply plot tracks as a static full track above image. 
	 * @param track
	 * @param imp
	 */
	public void plotTrack(AbstractTrack track, ImagePlus imp){
		Color plotcolor = new Color(255, 0, 0); //red
		if (multicolor) {
			int[] rgb = returnPhysicsLut();
			plotcolor = new Color(rgb[0], rgb[1], rgb[2]);
		}
		Iterator<Node> iter = track.getNodes().iterator();
		Node n;
		int[] xA = new int[track.getNodes().size()];
		int[] yA = new int[track.getNodes().size()];

		int counter = 0;
		while (iter.hasNext()) {
			n = iter.next();
			IJ.log("index:" + n.getId() + "- " + n.getX() + "," +n.getY());
			xA[counter] = (int) Math.round(n.getX());
			yA[counter] = (int) Math.round(n.getY());
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
		if (imp.isVisible())
			imp.updateAndDraw();
		else
			imp.show();
	}
	
	/**
	 * frame starts form 1
	 * @param track
	 * @param imp
	 */
	public void plotProgressiveTrack(AbstractTrack track, ImagePlus imp){
		Color plotcolor = new Color(255, 0, 0); //red
		if (multicolor) {
			IJ.run(imp, "RGB Color", "");
			int[] rgb = returnPhysicsLut();
			plotcolor = new Color(rgb[0], rgb[1], rgb[2]);
		}
		int startframe = track.getFrameStart();
		int endframe = track.getFrameEnd();
		int framenum = endframe - startframe + 1;
//		IJ.log("Track:" + track.getNodes().get(0).getTrackID() +
//				" frames:" + framenum);
		ArrayList<Node> nodes = track.getNodes();
		int sx, sy,thisframe;
		int nodecount = 0;
		ImageProcessor ip;
		Node n;
		Polygon poly = new Polygon();
		PolygonRoi proi = null;
//		for (int i = 1; i < nodes.size(); i++){
		for (int i = 0; i < framenum; i++){
			thisframe = i + startframe + 1;
			ip = imp.getStack().getProcessor(thisframe);			
			if (track.getFramelist().contains(thisframe)){
				n = nodes.get(nodecount);
				//poly = new Polygon();
				//for (int j = 0; j < nodecount+1; j++){
					//sx = (int) nodes.get( j ).getX();
					//sy = (int) nodes.get( j ).getY();
					sx = (int) n.getX();
					sy = (int) n.getY();
					poly.addPoint(sx, sy);
				//}
				nodecount = nodes.indexOf(n) + 1;
			} 
//			if (poly != null){
				if (poly.npoints > 1){
					proi = new PolygonRoi(poly, Roi.POLYLINE);
					proi.setStrokeColor(plotcolor);
					ip.drawRoi(proi);				
				}
//			}
		}

		if (imp.isVisible())
			imp.updateAndDraw();
		else
			imp.show();
	}
	
	/**
	 * Returns random RGB color array based on Physics LUT.
	 * Used for coloring tracks.   
	 * It might be better directly look for LUT file. 
	 * @return
	 */
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
