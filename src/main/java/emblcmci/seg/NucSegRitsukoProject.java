package emblcmci.seg;

import fiji.threshold.Auto_Threshold;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.plugin.filter.GaussianBlur;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageConverter;
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
		ip3.convertToByte(false);
		
		//originally, fill holes + 2 times erosion, 2 times dilation
		
		IJ.log("Lower Threshold: " + Integer.toString(lowth));
		ip2 = null;
		return ip3;
	}
	
	public ImageProcessor crop(ImagePlus imp, Roi roi){
		ImagePlus impcopy = imp.duplicate();
		impcopy.setRoi(roi);
		impcopy.getProcessor().crop();
		return impcopy.getProcessor();
		
	}

}
