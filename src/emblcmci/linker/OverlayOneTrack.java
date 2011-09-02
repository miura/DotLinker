package emblcmci.linker;

import java.awt.Frame;
import java.awt.Color;
import java.util.Vector;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.Overlay;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Line;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.text.TextWindow;

/**
 * Kota Miura
 * 
 */
public class OverlayOneTrack implements PlugIn {

	double[][] xycoords; 
	       
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
		int rows = tt.getTextPanel().getLineCount(); 
		xycoords = new double[rows][5];
		int rowlength = trt.getColumn(0).length;
		for (int i = 0; i < rowlength; i++){
			xycoords[i][0] = (int) trt.getValue("TrackID", i);
			xycoords[i][1] = trt.getValue("Xpos", i);
			xycoords[i][2] = trt.getValue("Ypos", i);
			xycoords[i][3] = (int) trt.getValue("frame", i);			
			xycoords[i][4] = trt.getValue("Area", i);			
		}
		int ChosenTrackNumber = (int) IJ.getNumber("Choose a Track", 1);
		int[] xA;
		int[] yA;
		int count = 0;
		for (int i = 0; i< rowlength-1; i++){
			if (xycoords[i][0] == ChosenTrackNumber){
				IJ.log(""+ xycoords[i][1] + ", " + xycoords[i][2]);
				count++;
				
			}
		}
		xA = new int[count];
		yA = new int[count];
		int count2 = 0;
		for (int i = 0; i < count; i++){
			if (xycoords[i][0] == ChosenTrackNumber){
				xA[count2] = (int) xycoords[i][1];
				yA[count2] = (int) xycoords[i][2];				
			}			
		}
		Roi roi1 = new PolygonRoi(xA, yA, count, Roi.POLYLINE);
		imp.setRoi(roi1);
		Overlay overlay = imp.getOverlay();
		if (overlay != null)
			overlay.add(roi1);
		else
			overlay = new Overlay(roi1);
		Color red = new Color(1, 0, 0);
		overlay.setStrokeColor(red); 
		imp.setOverlay(overlay);
		imp.updateAndDraw() ;		
		
	}
	
	public void overlayLine(ImagePlus imp, double x1, double y1, double x2, double y2){
	    
	  //roi1 = Arrow( 110, 245, 111, 246);
	      Roi roi1 = new Line(x1, y1, x2, y2);
	  //roi1.setWidth(1.5);
	  //roi1.setHeadSize(3);
	      imp.setRoi(roi1);
	      Overlay overlay = imp.getOverlay();
	      if (overlay != null)
	          overlay.add(roi1);
	      else
	          overlay = new Overlay(roi1);
	      Color red = new Color(1, 0, 0);
	      overlay.setStrokeColor(red); 
	      imp.setOverlay(overlay);
	      imp.updateAndDraw() ;
	   
	  //IJ.log("head " + roi1.getDefaultHeadSize());
	  //IJ.log("head " + roi1.getDefaultWidth());		
	}
}