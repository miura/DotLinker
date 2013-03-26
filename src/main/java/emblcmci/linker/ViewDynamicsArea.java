package emblcmci.linker;

import java.awt.Font;
import java.awt.Frame;
import java.util.Iterator;

import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;
import emblcmci.obj.Track;
import emblcmci.obj.Track2Dcells;
import emblcmci.obj.Tracks2Dcells;
import emblcmci.obj.converters.AbstractResultsTableToTracks;
import emblcmci.obj.converters.ResultTableToTracks;
import emblcmci.obj.converters.ResultsTableToTracks2Dcells;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.measure.ResultsTable;
import ij.plugin.CanvasResizer;
import ij.process.ImageProcessor;

public class ViewDynamicsArea extends AbstractViewDynamics {

	public ViewDynamicsArea(ImagePlus imp) {
		super(imp);
	}
	
	/**
	 * Does the color-coded plotting of area dynamics.
	 * Each cell (particle) will be wand-auto-selected and filled with color code 
	 * corresponding to relative increase/decrease in area against the first time point area.
	 * 
	 * In dialog, choosing track ID will do the processing for that track. 
	 * choosing 0 as the trackID will do processing for all tracks in the data table.   
	 */
	public void plotAreaDynamics(){
		ResultsTable trt = getTrackTable("Tracks");
		ResultsTableToTracks2Dcells rttracks = new ResultsTableToTracks2Dcells(trt);
		rttracks.run();
		AbstractTracks abstracks = rttracks.getTracks();
		if (abstracks instanceof Tracks2Dcells){
			Tracks2Dcells tracks = (Tracks2Dcells) abstracks;
			// get minimum and maximum fraction through all tracks
			double areafracMax = 0;
			double areafracMin =100;
			for (AbstractTrack v : tracks.values()){ //iterate for tracks
				if ((v != null) && (v instanceof Track2Dcells)) {
					Track2Dcells t = (Track2Dcells) v;
					if (t.getAreafracMIN() < areafracMin) 
						t.setAreafracMIN( areafracMin );
					if (t.getAreafracMAX() > areafracMax) t.setAreafracMAX( areafracMax );
				}
			}
			IJ.log("Area Fraction Minimum: " + areafracMin);
			IJ.log("Area Fraction Maximum: " + areafracMax);
			if (areafracMax > 2.0){
				areafracMax = 2.0;
				IJ.log("... areaFracMax Corrected to:" + areafracMax);
			}

			areaPlotter(tracks, imp, areafracMin, areafracMax);		
			//addAreaColorScale(imp, areafracMin, areafracMax);
		}

	}
	
	/**
	 * Plotting of Color Coded area, relative to the area for the first time point in each track. 
	 * All tracks will be plotted if track "0" is selected. 
	 * Otherwise, user-input track (by track ID) will be plotted. 
	 * When there is pointROI in the image stack, then the trackID closest to the 
	 * selected position will be returned as the default value 
	 * 
	 * @param Tracks: hashmap of Tracks. 
	 * @param imp: image stack to be plotted
	 * @param areafracMin: maximum of all the area fraction value in Tracks. 
	 * @param areafracMax: minimum of all the area fraction value in Tracks. 
	 */
	public void areaPlotter(Tracks2Dcells tracks, ImagePlus imp,
			double areafracMin, double areafracMax){

		int defaultID = 1;
		//if there is a pointROI, then search for the track closest to the ROI.
		if (imp.getRoi() != null){
			Roi pntroi = imp.getRoi();
			//Tracks2Dcells track = new Tracks2Dcells();
			//tracks.setTracks(tracks);	//@TODO currently a work around, should change the argument of this method. 
			defaultID = tracks.getTrackClosesttoPointROI(pntroi);
			imp.killRoi();
		}
		int ChosenTrackNumber = (int) IJ.getNumber("Choose a Track (if 0, all tracks)", defaultID);
		Track2Dcells track;

		if (ChosenTrackNumber != 0){
			track = (Track2Dcells) tracks.get(ChosenTrackNumber);
			if (track != null)
				trackAreaColorCoder(imp, track, areafracMin, areafracMax);
			else
				IJ.showMessageWithCancel("No Track", "no such track could be found");
		} else {
			for (AbstractTrack v : tracks.values()) //iterate for tracks
				if (v != null)
					trackAreaColorCoder(imp, v, areafracMin, areafracMax);
		}
		imp.updateAndDraw();

	}

	//@TODO type unsafe!
	public boolean trackAreaColorCoder(ImagePlus imp, AbstractTrack track, double areafracMin, double areafracMax){
		int areascale = 0; 
		Iterator<Node> iter = track.getNodes().iterator();
		Node n;
		double areaFrac;
		while (iter.hasNext()) {
			n = iter.next();
			// normalize to 255 scale. 0 will be the segmented background, black
			areaFrac = n.getAreaFraction();
			if (areaFrac > areafracMax)
				areaFrac = areafracMax;
			areascale = (int) Math.floor( 
					((areaFrac - areafracMin) 
							/ (areafracMax - areafracMin))* 255 +1);
//			IJ.log(""+counter+": area fraction=" + 
//					n.areafraction + " 255 scaled = " + areascale);
			fillArea(imp, n, areascale);
		}
		return true;
	}
	
	/**
	 * Adds color scale bar to the stack painted with area dynamics.  
	 * Resizes canvas so that the scale could be placed in the right side of original stack.
	 * 
	 * ...way it is done is a bit ugly so this should be redone...  
	 * @param imp
	 */
	public void addAreaColorScale(){
		ResultsTable trt = getTrackTable("Tracks");
		ResultsTableToTracks2Dcells rttracks = new ResultsTableToTracks2Dcells(trt);
		rttracks.run();
		AbstractTracks abstracks = rttracks.getTracks();
		if (abstracks instanceof Tracks2Dcells){
			Tracks2Dcells tracks = (Tracks2Dcells) abstracks;
			// get minimum and maximum fraction through all tracks
			// this part is common to the others
			double areafracMax = 0;
			double areafracMin =100;
			for (AbstractTrack v : tracks.values()){ //iterate for tracks
				if ((v != null) && (v instanceof Track2Dcells)) {
					Track2Dcells t = (Track2Dcells) v;
					if (t.getAreafracMIN() < areafracMin) 
						t.setAreafracMIN( areafracMin );
					if (t.getAreafracMAX() > areafracMax) t.setAreafracMAX( areafracMax );
				}
			}
			IJ.log("Area Fraction Minimum: " + areafracMin);
			IJ.log("Area Fraction Maximum: " + areafracMax);
			if (areafracMax > 2.0){
				areafracMax = 2.0;
				IJ.log("... areaFracMax Corrected to:" + areafracMax);
			}

			//resizing canvas, adding extra space in the left side. 
			CanvasResizer cr = new CanvasResizer();
			int oldwidth = imp.getWidth();
			int oldheight = imp.getHeight();
			int addwidth = 50;
			ImageStack ipresized = cr.expandStack(imp.getStack(), oldwidth + addwidth, oldheight, 0, 0);
			imp.setStack(ipresized);

			//construct the color scale bar
			double unitheight = 1.0; // *** this could be adjusted ****
			if (oldheight < 256)
				unitheight = (oldheight/2)/256;	//if the window height is small, adust the scale bar height accordingly

			//pixel height per color scale step, could be less than 1.
			double stepwidth = 256*unitheight/256;

			//y-position of the top of the scale bar
			int toppos = oldheight - 10 - (int) Math.round(256*unitheight);

			// y-position corresponding to 1.0 relative area ... reference area at [0] of each track.
			int pos1 = toppos + 256 - (int) ((1 - areafracMin)/((areafracMax - areafracMin))* 256 * unitheight); 

			ImageProcessor ipslice;
			Font font = new Font("SansSerif", Font.PLAIN, 8);

			for (int k = 0; k < imp.getStackSize(); k++){
				ipslice = ipresized.getProcessor(k+1);
				for (double i = 0; i < 256*unitheight; i+=stepwidth){
					for (int j = oldwidth+5; j < oldwidth + 25; j++){
						ipslice.set( j, toppos + (int) Math.round(i),  256 - (int) Math.round(i/unitheight));
					}
				}
				ipslice.setFont(font);
				ipslice.setColor(128);

				ipslice.drawString(Double.toString(areafracMax), oldwidth + 28, toppos);
				ipslice.drawString(Double.toString(1.0), oldwidth + 28, pos1);
				ipslice.drawString(Double.toString(areafracMin), oldwidth + 28, (int) (toppos + 256*unitheight));
			}

			imp.updateAndDraw();
		}
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
			IJ.log("Null ROI returned: id" + n.getId() + 
					" frame:" + n.getFrame() + 
					" AreaFrac:" + n.getAreaFraction() + 
					" X:" + n.getX() +
					" Y:" + n.getY() +
					" trackID:" + n.getTrackID()
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
		

	
	//creates data table for area changes (normalized to 1) for each track specified. 
	public String showAreaDataInTable(Track track){
		String title = "AreaDataTable";
		ResultsTable rt;
		if (WindowManager.getFrame(title) != null){
			Frame rtf = WindowManager.getFrame(title);
			
		} else {

		}
		
		for (Node n : track.getNodes()){
			
		}
		
		return title;
	}

	@Override
	public AbstractResultsTableToTracks convertResultsTable(ResultsTable trt) {
		return new ResultsTableToTracks2Dcells(trt);
	}
	
	
	//--------------- Area Plottting tools down to here ---------------

}
