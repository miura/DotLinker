package emblcmci.seg;

import java.util.ArrayList;

import fiji.threshold.Auto_Threshold;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;
import ij.plugin.filter.Binary;
import ij.plugin.filter.EDM;
import ij.plugin.filter.GaussianBlur;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;

/**
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
	
	public void runSingleFrame(){
		if ((xposA != null) && (yposA != null)) {
			extractPositions(this.imp, this.xposA, this.yposA, this.roisize, this.roisize);
			if (this.ipList.size() > 0){
				binList = new ArrayList<ImageProcessor>();
				for (ImageProcessor subip : this.ipList)
					binList.add(binarize(subip));
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
	public void getPerNucleusBinImgProcessors(int roisize){
		getPerNucleusBinImgProcessors(this.imp, roisize, this.xposA, this.yposA, this.frameA);

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
	
	
	/** creates a stack with individual nucleus per frame, binarized. 
	 * 
	 * @param imp
	 * @param roisize
	 * @param xA
	 * @param yA
	 * @param fA
	 * @return
	 */
	public void getPerNucleusBinImgProcessors(ImagePlus imp, int roisize, int[] xA, int[] yA, int[] fA){
		this.roisize = roisize;
		ImageProcessor ip, subip, binip;
		Roi roi;
		this.binList = new ArrayList<ImageProcessor>();
		this.roiList = new ArrayList<Roi>();
		this.ipList = new ArrayList<ImageProcessor>();
		for (int i = 0; i < fA.length; i++) {
			ip = imp.getStack().getProcessor(fA[i]);
			roi = makeRoi(ip, xA[i], yA[i], roisize, roisize);
			roiList.add(roi);
			subip = extract(ip, roi);
			ipList.add(subip);
			binip = binarize(subip);
			binList.add(binip);
		}
	}

	/**
	 * Ritsuko's project method for the nucleus segmentation. 
	 * 8 bit or 16 bit 
	 * 20130319
	 * @param subip a small subset image with supposedly single nucleus. 
	 * @return binarized ImageProcessor
	 */
	public ImageProcessor binarize(ImageProcessor subip){
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
		//watershed
		watershed(ip3);
		
		//IJ.log("Lower Threshold: " + Integer.toString(lowth));
		ip2 = null;
		return ip3;
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
		EDM edm = new EDM();
        if (!ip.isBinary()) {
            IJ.log("8-bit binary image (0 and 255) required.");
            return;
        }
        edm.setup("watershed", null);
        edm.run(ip);
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
		if (x < 0) x = 0;
		if ( (x + ww) > ip.getWidth() - 1)
			x = ip.getWidth() - ww;
		if (y < 0) y = 0;
		if ( (y + hh) > ip.getHeight() - 1)
			y = ip.getHeight() - hh;
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
	

}
