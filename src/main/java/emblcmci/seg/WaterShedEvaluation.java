package emblcmci.seg;

import ij.ImagePlus;
import ij.plugin.Duplicator;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.Binary;

public class WaterShedEvaluation {
	private Double PERI_WATERSHEDLINE_THRESHOLD;
	/**
	 * @param imp: binary image
	 */
	public WaterShedEvaluation(Double threshold) {
		this.PERI_WATERSHEDLINE_THRESHOLD = threshold;
	}
	/* The fastest method for testing watershed splitting.
	 * Takes a binary ImagePlus, and if test goes through 
	 * returns watersheded image. If not, original is returned. 
	 * 
	 */
	public boolean testWatershedFast(ImagePlus imp){
		Duplicator dup = new Duplicator();
		Binary b = new Binary();
		ImageCalculator ic = new ImageCalculator();
		
		ImagePlus imp2 = dup.run(imp);
		b.setup("dilate", null);
		b.run(imp2.getProcessor());
		ImagePlus impws =  ic.run("Difference create", imp, imp2);
		
		ImagePlus imp3 = dup.run(imp2);
		b.setup("erode", null);
		b.run(imp3.getProcessor());
		
		ImagePlus impperi = ic.run("Difference create", imp2, imp3);
		int[] wshist = impws.getProcessor().getHistogram();
		int[] perihist = impperi.getProcessor().getHistogram();
		double ratio = ( ((double) wshist[255]) / ((double)perihist[255])) - 1.0;
		if (ratio > PERI_WATERSHEDLINE_THRESHOLD)
			return true;
		else
			return false;
	}
}
