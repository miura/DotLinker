package de.embl.cmci.seg;

import java.util.ArrayList;

import de.embl.cmci.obj.Node;

import fiji.threshold.Auto_Threshold;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.filter.Binary;
import ij.plugin.filter.EDM;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import ij.Prefs;

/**
 * First do Gaussblurring with a large sigma value to estimate Otsu threshold value. 
 * Second, do Gauss blurring with the original image with a small sigma value, and using the 
 * threshold value estimated in the first blurring trial to segment the second image. 
 * 
 * The macro below reimplemented as a class.
20130319
Kota Miura
 * @author miura
 */
/*
 * function KotasSegmentationEval2para3D(imageID, gausssigma1, gausssigma2) {
	currentID = imageID;	//090814
	selectImage(currentID);
	op = "title=[tempforThresholding] duplicate range=1-"+nSlices;
	run("Duplicate...", op); // for evaluation
	tempID = getImageID(); run("Out");run("Out");	
	SetToHighestIntensitySlice();
	op = "sigma="+gausssigma1+" stack";
	run("Gaussian Blur...", op);
	SetToHighestIntensitySlice(); 	
	run("MultiThresholder", "Otsu");
	getThreshold(lower, upper);
	selectImage(tempID);close();

	selectImage(currentID);
	op = "sigma="+gausssigma2+" stack";
	run("Gaussian Blur...", op);
	setThreshold(upper, 255);
	run("Convert to Mask", "  black");
	run("Fill Holes", "stack");
	//for(i=0; i<gausssigma2; i++) run("Erode", "stack");
	for(i=0; i<2; i++) run("Erode", "stack");
	for(i=0; i<2; i++) run("Dilate", "stack");
	print("      Otsu Lower Upper = "+lower+"-"+upper);
	return upper;
}
 * 
 * 
 */
public class NucSegRitsukoProject{
	final int sigma1 = 37;
	final int sigma2 = 1;
	double accuracy;
	private ImagePlus imp;
	private int[] xposA;
	private int[] yposA;
	int roisize = 100;
	
	ArrayList<ImageProcessor> ipList;
	ArrayList<Roi> roiList;
	ArrayList<ImageProcessor> binList;
	private int[] frameA;
	
	public static final EdgeObjEliminator KILL_EDGE_OBJ = new EdgeObjEliminator();
	
	public NucSegRitsukoProject(){}
	
	/**
	 * @param xposA
	 * @param yposA
	 */
	public NucSegRitsukoProject(ImagePlus imp, int[] xposA, int[] yposA, int[] frameA, int roisize) {
		super();
		this.imp = imp;
		this.xposA = xposA;
		this.yposA = yposA;
		this.frameA = frameA;
		this.roisize = roisize;
	}
	
	public void runSingleFrame(double wsthreshold){
		if ((xposA != null) && (yposA != null)) {
			extractPositions(this.imp, this.xposA, this.yposA, this.roisize, this.roisize);
			if (this.ipList.size() > 0){
				binList = new ArrayList<ImageProcessor>();
				for (ImageProcessor subip : this.ipList)
					binList.add(binarize(subip, wsthreshold));
				IJ.log("Extracted Nucleus ImageProcessors: " + Integer.toString(binList.size()));
			} else {
				IJ.log("no sub image extraction was possible");
			}
		}
	}
	
	/**
	 * using field values
	 * @param roisize
	 * @return
	 */
	public void getPerNucleusBinImgProcessors(int roisize, double wsthreshold){
		getPerNucleusBinImgProcessors(
				this.imp, roisize, 
				this.xposA, this.yposA, this.frameA,
				wsthreshold);

	}
	
	/** For checking test results. 
	 * 
	 * @return
	 */
	
	public ImageStack getBinStack(){
		ImageStack stk = new ImageStack(this.roisize, this.roisize);
		for (ImageProcessor ip : this.binList)
			stk.addSlice(ip);
		return stk;
	}
	
	/** see the description of
	 * loadImagesToNode(Node n, ImageProcessor ip, int wsthreshold)
	 * 
	 * @param n
	 * @param imp: could be either a stack or a single image. 
	 * @param wsthreshold
	 */
	public void loadImagesToNode(Node n, ImagePlus imp, double wsthreshold){
		ImageProcessor ip;
		if (imp.getStackSize() > 1)
			ip = imp.getStack().getProcessor(n.getFrame());
		else
			ip = imp.getProcessor();
		loadImagesToNode(n, ip, wsthreshold);
	}
	/**
	 * extracts image within the original image according to the Roi info 
	 * that the Node has, do binary segmentation and then store those
	 * images within Node. 
	 * @param n a single Node. 
	 * @param ip original image ImageProcessor, original frame size. 
	 */
	public void loadImagesToNode(Node n, ImageProcessor ip, double wsthreshold){
		ImageProcessor subip = ip.duplicate();
		subip.setRoi(n.getOrgroi());
		subip = subip.crop();
		n.setOrgip(subip);
		ImageProcessor binip = binarize(subip, wsthreshold);
		n.setBinip(binip);
		
	}
	/** creates Arrays of images with individual nucleus per frame, binarized.
	 * position key is based on the parameters, with fixed ROI size.  
	 * 
	 * @param imp: source stack
	 * @param roisize: widht and heigh of the roi. 
	 * @param xA: dot position x coordinate
	 * @param yA: dot position y coordinate
	 * @param fA: frame numbste, starting from 1. 
	 * 
	 */
	public void getPerNucleusBinImgProcessors(
			ImagePlus imp, 
			int roisize, 
			int[] xA, int[] yA, int[] fA,
			double wsthreshold){
		IJ.log("... subimages being accumulated and segmenting nucleus.");
		this.roisize = roisize;
		ImageProcessor ip, subip, binip;
		Roi roi;
		ArrayList<ImageProcessor> 	binList = new ArrayList<ImageProcessor>();
		ArrayList<Roi> 				roiList = new ArrayList<Roi>();
		ArrayList<ImageProcessor> 	ipList = new ArrayList<ImageProcessor>();
		for (int i = 0; i < fA.length; i++) {
			ip = imp.getStack().getProcessor(fA[i]);
			roi = makeRoi(ip, xA[i], yA[i], roisize, roisize);
			roiList.add(roi);
			//subip = extract(ip, roi);
			ip.setRoi(roi);
			subip = ip.crop();
			ipList.add(subip);
			binip = binarize(subip, wsthreshold);
			binList.add(binip);
		}
		this.binList = binList;
		this.roiList = roiList;
		this.ipList = ipList;
	}

	/**
	 * Ritsuko's project method for the nucleus segmentation. 
	 * 8 bit or 16 bit 
	 * 20130319
	 * @param subip a small subset image with supposedly single nucleus. 
	 * @return binarized ImageProcessor
	 */
	public ImageProcessor binarize(ImageProcessor subip, double wsthreshold){
		ImageProcessor ip2, ip3;
		ip2 = subip.duplicate();
		ip3 = subip.duplicate();
		GaussianBlur gb = new GaussianBlur();
		double accuracy = (ip2 instanceof ByteProcessor || ip2 instanceof ColorProcessor) ?
	            0.002 : 0.0002;
		gb.blurGaussian(ip2, sigma1, sigma1, accuracy);
		int[] hist = ip2.getHistogram();
		int lowth = Auto_Threshold.Otsu(hist);
		gb.blurGaussian(ip3, sigma2, sigma2, accuracy);
		ip3.threshold(lowth);
		//(new ImageConverter(imp3)).convertToGray8();
		ip3 = ip3.convertToByte(false);
		
		//originally, fill holes + 2 times erosion, 2 times dilation
		postProcessing(ip3);
		
		//eliminate edge objects
		//ip3 = KILL_EDGE_OBJ.run(new ImagePlus("tt",ip3));
		//watershed
		ImageProcessor ip4 = watershedWithEval(ip3, wsthreshold);
		
		//IJ.log("Lower Threshold: " + Integer.toString(lowth));
		ip2 = null;
		return ip4;
	}
	
	public ImageProcessor cleanEdge(ImagePlus imp){
		ImageProcessor ipout = KILL_EDGE_OBJ.run(imp);
		return ipout;
		
	}
	
	void postProcessing(ImageProcessor ip){
		Binary binner = new Binary();
		binner.setup("fill", null);
		binner.run(ip);
		binner.setup("erode", null);
		binner.run(ip);
		binner.run(ip);
		binner.setup("dilate", null);
		binner.run(ip);
		binner.run(ip);	
	}
	void watershed(ImageProcessor ip){
        if (!ip.isBinary()) {
            IJ.log("8-bit binary image (0 and 255) required.");
            return;
        }
		EDM edm = new EDM();
        edm.setup("watershed", null);
        edm.run(ip);
	}
	
	/** evaluates ip if this binary image is appropriate for watershed. 
	 * if yes, then do watershed. If not, original image is returned. 
	 * 20130327
	 * @param ip
	 * @param threshold: ratio watershed trace / perimeter
	 * @return
	 */
	ImageProcessor watershedWithEval(ImageProcessor ip, double threshold){
		//double threshold = 0.25; // ratio watershed trace / perimeter
		WaterShedEvaluation wse = new WaterShedEvaluation(threshold);
		//ImageProcessor ipout = wse.test2WatershedFast(ip);
		ImageProcessor ipout = wse.testWatershedFast3(ip, 100);
		return ipout;
	}
	
	
	public ImageProcessor extract(ImagePlus imp, Roi roi){
		return extract(imp.getProcessor(), roi);
		
	}
	public ImageProcessor extract(ImageProcessor ip, Roi roi){
		ImageProcessor ipcopy = ip.duplicate();
		ipcopy.setRoi(roi);
		return ipcopy.crop();
	}
	
	public void extractPositions(
			ImagePlus imp, 
			int[] xposA, 
			int[] yposA, 
			int ww, 
			int hh){
		Roi r;
		int x, y;
		this.ipList = new ArrayList<ImageProcessor>();
		this.roiList = new ArrayList<Roi>();
		for (int i = 0 ; i < xposA.length; i++){
			x = xposA[i] - ww/2;
			y = yposA[i] - hh/2;
			
			r = makeRoi(imp.getProcessor(), xposA[i], yposA[i], ww, hh);
			ipList.add(extract(imp.getProcessor(), r));
			roiList.add(r);
		}
	}
	
	public Roi makeRoi(
			ImageProcessor ip, 
			int xpos, 
			int ypos, 
			int ww, 
			int hh){
		Roi r;
		int x, y;
		x = xpos - ww/2;
		y = ypos - hh/2;
		r = makeRoi(
				ip.getWidth(),ip.getHeight(),
				x, y, ww, hh);
		return r;
	}
	
	public Roi makeRoi(
			int ipw,
			int iph,
			int x, 
			int y, 
			int ww, 
			int hh){
		Roi r;
		if (x < 0) x = 0;
		if ( (x + ww) > ipw - 1)
			x = ipw - ww;
		if (y < 0) y = 0;
		if ( (y + hh) > iph - 1)
			y = iph - hh;
		r = new Roi( x , y , ww, hh);
		return r;
	}

	public ArrayList<ImageProcessor> getIpList() {
		return ipList;
	}

	public ArrayList<Roi> getRoiList() {
		return roiList;
	}
	public ArrayList<ImageProcessor> getBinList() {
		return binList;
	}
	
	private static class EdgeObjEliminator{
		
		int MAXSIZE = 10000;
		int MINSIZE = 100;
		int options = eliminationOptions();
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer p = 
				new ParticleAnalyzer(options, ParticleAnalyzer.CENTROID, 
						rt, MINSIZE, MAXSIZE);
		public ImageProcessor run(ImagePlus imp){
			Prefs.blackBackground = true;
			p.setHideOutputImage(true);
			p.analyze(imp);
			//IJ.log("Objects:" + rt.getCounter());
			ImageProcessor ipout = p.getOutputImage().getProcessor();
			ipout.invertLut();
			return ipout;
		}
		
		int eliminationOptions(){
			int options = 
					ParticleAnalyzer.SHOW_MASKS + 
					ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES +
					ParticleAnalyzer.INCLUDE_HOLES +
					ParticleAnalyzer.CLEAR_WORKSHEET;
			return options;
		}			
	}
}
