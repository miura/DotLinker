package de.embl.cmci.linker.plugin;

import de.embl.cmci.linker.plotter.ViewDynamics;
import de.embl.cmci.linker.plotter.ViewDynamicsArea;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;

/**
 * ImageJ plugin for plotting cell movement analysis results in a image stack. 
 * Plots
 *  - tracks
 *  - color coded area changes.
 *  
 * see core class: emblcmci.linker.ViewDynamics.
 * 
 * Requirements for plugin
 * 1. an image stack with the dimension size greater or equal to the analyzed stack. 
 * 2. Results table with Window title "Tracks".
 * 
 * An example script for opening such data sets is overlaydebug.js (included in this project).  
 * 
 * @author Kota Miura
 * 20110905
 * 
 */
public class Overlay_Track implements PlugIn {


	public void run(String arg) {
	    ImagePlus imp = IJ.getImage();
	    if (imp == null){
	    	IJ.error("no image to plot");
	    	return;
	    }
	    ViewDynamicsArea vdyna = new ViewDynamicsArea(imp);
	    if (arg.equals("track"))
	    	vdyna.plotTracks();
	    
	    if (arg.equals("area"))
	    	vdyna.plotAreaDynamics();
	    
	    if (arg.equals("areascale"))
	    	vdyna.addAreaColorScale();
	}
}