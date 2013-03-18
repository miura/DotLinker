package emblcmci.seg;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import mpicbg.ij.clahe.Flat;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.MaximumFinder;
import ij.process.ImageProcessor;
/**
 * Deriving nicely placed dots according to nucleus positions. 
 * original script: VoronoiSeries.py
 * 
 * @author Kota Miura
 * 20130318
 *
 */
public class NucToDots {

	private ImagePlus imp;

	public NucToDots(ImagePlus imp){
		if (imp == null){
			IJ.log("No image file found in the path");
			return;
		}
		this.imp = imp.duplicate();
	}
	public void stackCLAHE(ImagePlus imp){
		int i;
		
		for (i = 0; i < imp.getStackSize(); i++){
			imp.setSlice( i + 1 );
			Flat clahe = new Flat();
			clahe.run(imp, 49, 256, (float) 3.0, null, false);
		}
	}
	public ImagePlus maxPoints(ImagePlus orgimp){
		ImagePlus imp = preprocess(orgimp);
		//IJ.run(imp, "Find Maxima...", "noise=8 output=[Single Points]");
		ImageProcessor ip = ( new MaximumFinder()).findMaxima(imp.getProcessor(), 
				8.0, 0.0, MaximumFinder.SINGLE_POINTS, false, false);
		ImagePlus maxpntsimp = new ImagePlus("maxPnts", ip);
		return maxpntsimp;
	}

	/**
	 * Convert to 8 bit -
	 * Grays morphology -
	 * Auto Local threshold -
	 * Fill holes -
	 * Distance map conversion. 
	 * @return
	 */
	public ImagePlus preprocess(ImagePlus orgimp){
		ImagePlus imp = orgimp.duplicate();
		IJ.run(imp, "8-bit", "");
		//ImagePlus impdup = imp.duplicate();
		IJ.run(imp, "Gray Morphology", "radius=1 type=circle operator=erode");
		IJ.run(imp, "Auto Local Threshold", "method=Bernsen radius=45 parameter_1=0 parameter_2=0 white");
		IJ.run(imp, "Fill Holes", "");
		IJ.run(imp, "Distance Map", "");
		return imp;
	}
	/** Cosmetic method to calculate original image in 8 bit overlayed with voronoi separators. 
	 *  currently commented out, but worth to leaveit heare for viewing the 
	 * @param imp8bit (this does not exists in the currentl work flow in run method)
	 * @param impmaxpoints
	 * @return
	 */
	public ImagePlus generateVoronoiOverlay(ImagePlus imp8bit, ImagePlus impmaxpoints){
		// ? Polygon polygon =( new MaximumFinder()).getMaxima(imp.getProcessor(), 1.0, false);
		ImagePlus voroimp = new ImagePlus("voro", impmaxpoints.getProcessor().duplicate());
		IJ.run(voroimp, "Voronoi", "");
		//ImagePlus orgvoroimp = voroimp.duplicate();
		IJ.setThreshold(voroimp, 1, 255);
		IJ.run(voroimp, "Convert to Mask", "");
		IJ.run(voroimp, "Invert LUT", "");
		(new ImageCalculator()).run("Max", imp8bit, voroimp);
		return imp8bit;
	}
	
	public ImagePlus run(){
		IJ.log("CLAHE 1st run ...");
		stackCLAHE(this.imp);
		IJ.log("CLAHE 2nd run ...");
		stackCLAHE(this.imp);
		IJ.log("Estimating max points ...");
		ImagePlus simp, maximp;
		ImageStack stk = new ImageStack(this.imp.getWidth(), this.imp.getHeight());
		for (int i = 0; i < this.imp.getStackSize(); i++){
			simp = new ImagePlus("tt", this.imp.getStack().getProcessor(i+1));
			maximp = maxPoints(simp);
			stk.addSlice(maximp.getProcessor());
		}			
		
//		ImagePlus imp = this.imp.duplicate();
//		IJ.run(imp, "8-bit", "");
//		ImagePlus voronoiImp = generateVoronoiOverlay(imp, maximp);
		
		return new ImagePlus("MaxP", stk);
	}
	
	
	
	
}
