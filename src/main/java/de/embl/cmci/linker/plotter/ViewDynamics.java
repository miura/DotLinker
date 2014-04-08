package de.embl.cmci.linker.plotter;
/**
 * ViewDynamics.java
 * 
 * A Plugin core for use in ImageJ/Fiji.
 * 
 *      
 * @author Kota Miura
 * Centre for Molecular and Cellular Imaging, EMBL Heidelberg, Germany
 * 20110905
 * 
 */


import java.awt.Color;

import de.embl.cmci.obj.Node;
import de.embl.cmci.obj.Track;
import de.embl.cmci.obj.Tracks;
import de.embl.cmci.obj.converters.AbstractResultsTableToTracks;
import de.embl.cmci.obj.converters.ResultTableToTracks;


import ij.ImagePlus;
import ij.gui.Line;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;

public class ViewDynamics extends AbstractViewDynamics{


	
	public ViewDynamics(ImagePlus imp) {
		super(imp);
	}

	/** 
	 * Used for testing track-track gap linking capabilities. 
	 * 
	 * @param tracks
	 * @param imp
	 */
	public void trackGapLinkPlotter(Tracks tracks, ImagePlus imp){
		multicolor  = true;
		for (Object v : tracks.values()) //iterate for tracks
			if (v != null)
				plotLinkedGaps(tracks, (Track) v, imp);
	}
	

	/**
	 * for development purpose, 
	 * check which tracks are linked
	 */
	public void plotLinkedGaps(Tracks tracks, Track t, ImagePlus imp){
		Color plotcolor = new Color(255, 0, 0); //red
		int sx, sy, ex, ey;
		int nextID;
		int drawstartframe, drawendframe;
		Line linkline;
		Node startnode, endnode;
		if (t.getCandidateNextTrackID() > 0){
			startnode = t.getEndNode();
			nextID = t.getCandidateNextTrackID();
			endnode = tracks.get(nextID).getStartNode();
			
			sx = (int) Math.round(startnode.getX());
			sy = (int) Math.round(startnode.getY());
			drawstartframe = startnode.getFrame();

			ex = (int) Math.round(endnode.getX());
			ey = (int) Math.round(endnode.getY());
			drawendframe = endnode.getFrame();
			linkline = new Line(sx, sy, ex, ey);
			ImageProcessor ip;
			for (int i = drawstartframe; i < drawendframe; i++){
				ip = imp.getStack().getProcessor(i);
				ip.setColor(plotcolor);
				ip.setLineWidth(10);
				ip.draw(linkline);
			}
			imp.updateAndDraw();
		}
	}

	@Override
	public AbstractResultsTableToTracks convertResultsTable(ResultsTable trt) {
		return new ResultTableToTracks(trt);
	}

}
