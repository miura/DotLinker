package emblcmci.seg;

import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.Binary;
import ij.plugin.filter.EDM;
import ij.process.ImageProcessor;

public class WaterShedEvaluation {
	private Double PERI_WATERSHEDLINE_THRESHOLD;
	//final Duplicator dup = new Duplicator();
	/**
	 * 
	 */
	public WaterShedEvaluation(Double threshold) {
		this.PERI_WATERSHEDLINE_THRESHOLD = threshold;
	}
	/**
	 *  The fastest method for testing watershed splitting.
	 * Takes a binary ImagePlus, and if test goes through 
	 * returns watersheded image. If not, original is returned.
	 *  
	 * @param imp: binary already watershed applied image
	 */
	public boolean testWatershedFast(ImageProcessor ip){
		
		Binary b = new Binary();
		ImageCalculator ic = new ImageCalculator();
		
		ImageProcessor ip2 = ip.duplicate();
		b.setup("dilate", null);
		b.run(ip2);
		ImagePlus imp = new ImagePlus("1", ip);
		ImagePlus imp2 = new ImagePlus("2", ip2);		
		ImagePlus impws =  ic.run("Difference create", imp, imp2);
		
		ImageProcessor ip3 = ip2.duplicate();
		b.setup("erode", null);
		b.run(ip3);
		ImagePlus imp3 = new ImagePlus("3", ip3);	
		ImagePlus impperi = ic.run("Difference create", imp2, imp3);
		int[] wshist = impws.getProcessor().getHistogram();
		int[] perihist = impperi.getProcessor().getHistogram();
		double ratio = ( ((double) wshist[255]) / ((double)perihist[255])) - 1.0;
		ip2 = null;
		ip3 = null;
		if (ratio > PERI_WATERSHEDLINE_THRESHOLD)
			return false; // no watershed 
		else
			return true; //watershed recommended
	}
	
	/**
	 * 
	 * @param imp not yet thresholded image. 
	 * @return
	 */
	public ImageProcessor test2WatershedFast(ImageProcessor ip){
		ImageProcessor ip2 = ip.duplicate();
		EDM edm = new EDM();
		edm.setup("watershed", null);
		edm.run(ip2);
		if (testWatershedFast(ip2))
			return ip2;
		else
			return ip;
	}
}
