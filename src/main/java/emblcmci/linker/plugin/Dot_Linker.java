package emblcmci.linker.plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

/**
 * ImageJ/Fiji Plugin for Loading "Analyze Particle" results of Image Stack (currently 2D only)
 * and link particles in each frame to successive frames.  
 *  
 * Core of the algorithm is in emblcmci.linker.DotLinker.java
 * 
 * This class interfaces with ImageJ plugin interface. 
 * Checks if there is an Image stack opened, a Results table with appropriate parameters.
 * 
 *  If there is no results or some parameter required is/are missing, then will 
 *  redo "analyze particle" with the default setting (this might need changes)
 *  
 *  Kota Miura
 *  Center for Molecular and Cellular Imaging, EMBL Heidelberg
 *  started: 20110905
 *  20111202: linking using 3D coordinates, output of Volocity segmentation (Mette).
 *  	added linking algorithm using real scale. 		 
 *  
 * 
 */
public class Dot_Linker implements PlugIn {
	/**
	 *
	 */
	// parameters for calculating links
	int linkrange = 2;
	double displacement = 2.0;
	private int TrajectoryThreshold;
	
	public void run(String arg) {
		ImagePlus imp;
		if (WindowManager.getImageCount()>0)
			imp = IJ.getImage();
		else
			imp = null;
		
		AbstractDotLinker dl;
		LinkCosts linkcostmethod;
		boolean showtrack = true; 
		
		//check image stack
		// commented out for volovity data
/*		
		if (imp == null){
			IJ.error("No image Stack!");
			return;
		}*/
		//check data in resultstable
		// if no results, or parameter missing, try to do "analyze particle". 

		
		//check arguments, whether to do the analysis with GUI dialog or silently using the 
		//default parameter setting. 
		if (arg.equals("gui")){
			if (!getParameterDialog())
				return;			 
			dl = new DotLinker(imp, linkrange, displacement);
			
		} else if (arg.equals("gui_volocity")){
			if (!getParameterDialog())
				return;
			dl = new DotLinker2(imp, linkrange, displacement);
			showtrack = false;
			
		} else if (arg.equals("volocity")) {
			dl = new DotLinker2(imp);
			showtrack = false;
		} else
			dl = new DotLinker(imp);

		// incase of volocity data (Mette), choose only distance cost. 
		if (arg.equals("volocity") || arg.equals("gui_volocity"))
			//linkcostmethod = new LinkCostsOnlyDistance();
			//linkcostmethod = new LinkCostsOnlyScaledDistance();
			linkcostmethod = new LinkCostswithIntensityDynamics(displacement, 2.0);
		else
			linkcostmethod = new LinkCostswithAreaDynamics(displacement, 2.0);
		
		if (!dl.checkResultsTableParameters()){
			redoAnalyzeParticle(imp);
		}
		dl.setTrajectoryThreshold(TrajectoryThreshold);
		dl.doLinking(linkcostmethod, showtrack);
	}
	
	public void redoAnalyzeParticle(ImagePlus imp){
		IJ.run(imp, "Options...", 
				"iterations=1 count=1 black edm=Overwrite do=Nothing");
		IJ.run("Set Measurements...", 
				"area mean standard centroid perimeter " +
				"shape integrated stack display redirect=None decimal=5");
		String op =	
			"size=30-2100 " +
			"circularity=0.30-1.00 " + 
			"show=Outlines " +
			"display exclude clear include stack";
		IJ.run(imp, "Analyze Particles...", op);
	}
	
	public boolean getParameterDialog(){
		GenericDialog gd = new GenericDialog("Dot Linker...", IJ.getInstance());
		gd.addMessage("Linking Parameters:\n");
		gd.addNumericField("Link Range", 2, 0);
		gd.addNumericField("Displacement", 5.0, 2); 
		gd.addMessage("-----");
		gd.addNumericField("Trajectory Lenght Threshold", 10, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return false;
		this.linkrange = (int)gd.getNextNumber();
		this.displacement = gd.getNextNumber();
		this.TrajectoryThreshold = (int) gd.getNextNumber();		
		return true;
	}

	/**
	 * @return the linkrange
	 */
	public int getLinkrange() {
		return linkrange;
	}

	/**
	 * @param linkrange the linkrange to set
	 */
	public void setLinkrange(int linkrange) {
		this.linkrange = linkrange;
	}

	/**
	 * @return the displacement
	 */
	public double getDisplacement() {
		return displacement;
	}

	/**
	 * @param displacement the displacement to set
	 */
	public void setDisplacement(double displacement) {
		this.displacement = displacement;
	}
}