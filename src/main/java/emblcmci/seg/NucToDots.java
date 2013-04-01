package emblcmci.seg;

import java.awt.Polygon;
import java.util.ArrayList;


import fiji.threshold.Auto_Local_Threshold;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import mmorpho.MorphoProcessor;
import mmorpho.StructureElement;
import mpicbg.ij.clahe.Flat;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.MaximumFinder;
import ij.process.ImageProcessor;
/**
 * Deriving dots according to nucleus positions using MaxFinder function. 
 * original script: VoronoiSeries.py
 * 
 * @author Kota Miura
 * 20130318
 *
 */
public class NucToDots {

	private ImagePlus imp;
	private int[] xcoordA;
	private int[] ycoordA;
	private int[] frameA;


	public NucToDots(ImagePlus imp){
		if (imp == null){
			IJ.log("No image file found in the path");
			return;
		}
		this.imp = imp.duplicate();
	}
	public void stackCLAHE(ImagePlus imp){
		int i;
		IJ.log("CLAHE running ...");
		for (i = 0; i < imp.getStackSize(); i++){
			imp.setSlice( i + 1 );
			//Flat clahe = new Flat();
			// two times. 
			Flat.getFastInstance().run(imp, 49, 256, (float) 3.0, null, false);
			Flat.getFastInstance().run(imp, 49, 256, (float) 3.0, null, false);
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
	
	public Polygon maxFinder(ImageProcessor ip){
		  Polygon polygon = new MaximumFinder().getMaxima(ip, 1.0, false);
		  //npnts = len(polygon.xpoints)
		  return polygon;
	}
	/**
	 * preprocessCore +
	 * Distance map conversion. 
	 * @return imp: processed ImagePlus
	 *
	 */
	public ImagePlus preprocess(ImagePlus orgimp){
		ImagePlus imp2 = preprocessCore(orgimp);
		IJ.run(imp2, "Distance Map", "");
		return imp2;
	}

	/** 
	 * Convert to 8 bit -
	 * Grays morphology -
	 * Auto Local threshold -
	 * Fill holes -
	 * 
	 * @return imp: processed ImagePlus
	 *
	 */
	public ImagePlus preprocessCore(ImagePlus orgimp){
		ImagePlus imp = orgimp.duplicate();
		ImagePlus imp2;
		IJ.run(imp, "8-bit", "");
		StructureElement se = new StructureElement(StructureElement.CIRCLE, 0, 1.0f, StructureElement.OFFSET0);
		MorphoProcessor morph = new MorphoProcessor(se);
		morph.erode(imp.getProcessor());
		//"method=Bernsen radius=45 parameter_1=0 parameter_2=0 white"
		Auto_Local_Threshold alt = new Auto_Local_Threshold();
		imp2 = (ImagePlus) alt.exec(imp, "Bernsen", 45, 0, 0, true)[0];
		//imp2.duplicate().show();
		IJ.run(imp2, "Fill Holes", "");
		imp = null;
		return imp2;
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
	

	
	/**
	 * Preprocessing of stack. 
	 * @param stackimp
	 * @return
	 */
	public ImagePlus preprocessCoreStack(ImagePlus stackimp){
		IJ.log("Preprocess frames ...");
		ImageStack stk = new ImageStack(stackimp.getWidth(), stackimp.getHeight());
		ImagePlus tempimp;
		for (int i = 0; i < stackimp.getStackSize(); i++){
			tempimp = preprocessCore(new ImagePlus("tt", stackimp.getStack().getProcessor(i+1)));
			stk.addSlice(tempimp.getProcessor());
		}
		ImagePlus ppimp = new ImagePlus("prepoped", stk);		
		return ppimp;
	}
	
	public void run(){
		stackCLAHE(this.imp);
		runmain();		
	}
	
	public void runmain(){
		ImagePlus ppimp = preprocessCoreStack(this.imp);
		IJ.run(ppimp, "Distance Map", "stack");
		IJ.log("Estimating max points ...");
		Polygon maxpolygon;
		ArrayList<Polygon> ploygonlist = new ArrayList<Polygon>();
		for (int i = 0; i < ppimp.getStackSize(); i++){
			maxpolygon = maxFinder(ppimp.getStack().getProcessor(i+1));
			ploygonlist.add(maxpolygon);
		}
		int totalpnts = 0;
		for (Polygon p : ploygonlist){
			totalpnts += p.npoints;
		}
		this.xcoordA = new int[totalpnts];
		this.ycoordA = new int[totalpnts];
		this.frameA = new int[totalpnts];
		int framecount = 0;
		int filledlength = 0;
		for (Polygon p : ploygonlist){
			System.arraycopy(p.xpoints, 0, xcoordA, filledlength, p.xpoints.length);
			System.arraycopy(p.ypoints, 0, ycoordA, filledlength, p.ypoints.length);
			for (int i = filledlength; i < filledlength + p.npoints; i++){
				this.frameA[i] = framecount + 1;
			}
			framecount++;
			filledlength += p.npoints;
		}		
	}
	
	/**
	 * deprecated. returns max points stack. 
	 * @return
	 */
	public ImagePlus runOLD(){
		stackCLAHE(this.imp);
		IJ.log("Estimating max points ...");
		ImagePlus simp, maximp;
		this.imp.show();
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
	public int[] getXcoordA() {
		return xcoordA;
	}

	public int[] getYcoordA() {
		return ycoordA;
	}
	public int[] getFrameA() {
		return frameA;
	}
	
	
	
}
