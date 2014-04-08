package emblcmci.seg;

import de.embl.cmci.obj.Node;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.measure.ResultsTable;
import ij.plugin.ImageCalculator;
import ij.plugin.filter.Binary;
import ij.plugin.filter.EDM;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;

public class WaterShedEvaluation {
	private Double PERI_WATERSHEDLINE_THRESHOLD;
	//final Duplicator dup = new Duplicator();
	/**
	 * 
	 */
	public WaterShedEvaluation(Double threshold) {
		this.PERI_WATERSHEDLINE_THRESHOLD = threshold;
		Prefs.blackBackground = true;
	}
	/**
	 *  The fastest method for testing watershed splitting.
	 * Takes a binary ImagePlus, and if test goes through 
	 * returns watersheded image. If not, original is returned.
	 *  
	 * @param imp: binary already watershed applied image
	 */
	public boolean testWatershedFast(ImageProcessor iporg, ImageProcessor ip){
		
		Binary b = new Binary();
		ImageCalculator ic = new ImageCalculator();
		
		ImageProcessor ip2 = ip.duplicate();
		b.setup("dilate", null);
		b.run(ip2);
		ImagePlus imp = new ImagePlus("1", ip);
		ImagePlus imp2 = new ImagePlus("2", ip2);		
		ImagePlus impws =  ic.run("Difference create", imp, imp2);
		
		//ImageProcessor ip3 = ip2.duplicate();
		//b.setup("erode", null);
		//b.run(ip3);
		//ImagePlus imp3 = new ImagePlus("3", ip3);	
		ImagePlus imp0 = new ImagePlus("0", iporg);
		ImagePlus impperi = ic.run("Difference create", imp2, imp0);
		int[] wshist = impws.getProcessor().getHistogram();
		int[] perihist = impperi.getProcessor().getHistogram();
		double ratio = ( ((double) wshist[255]) / ((double)perihist[255])) - 1.0;

//		imp.show();
//		imp2.show();
//		imp0.show();
//		impws.show();
//		impperi.show();
//		IJ.log("ws266:"+ wshist[0] + " peri255:" + perihist[0]);
//		IJ.log("ws266:"+ wshist[255] + " peri255:" + perihist[255]);
//		IJ.log("watershed ratio" + ratio);
		ip2 = null;
		//ip3 = null;
		if (ratio > PERI_WATERSHEDLINE_THRESHOLD)
			return false; // no watershed 
		else
			return true; //watershed recommended
						 // if ratio is 0, then no watershed happend. 
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
		if (testWatershedFast(ip, ip2))
			return ip2;
		else
			return ip;
	}

	/**a bit more complex version:
	 * 
	 * (1) Do watershed. 
	 * (2) Do particle analysis, remove small one and edge ones. 
	 * (3) Do the particle analysis again. 
	 * (4) if there is multiple particle, then is should be decided whether the watershed is right or wrong. 
	 * (5) watershed evaluation. (the ratio watershed/periphery), only decision.
	 * for this, dilate and erode to go back to the none-watersheded state. 
	 * a. if watershed is recommneded, go back to (1) watersheded image 
	 * and choose the one according to the correct centroid of the node. 
	 * b. if the watershed is not recommended, none-watersheded state should be taken. 
	 * 
	 * @param ip: none-watershedded image.  
	 * @param minimumarea: size of particles below this size will be discarded. 
	 * @return
	 */
	public ImageProcessor testWatershedFast3(ImageProcessor ip, int minimumarea){
		ImageProcessor ip2 = ip.duplicate();
		EDM edm = new EDM();
		edm.setup("watershed", null);
		edm.run(ip2);
		ImageProcessor ipout = particleFilter(ip2, minimumarea);
		return ipout;
	}	
	
	/**
	 *  Eliminates dots very small segmented signal. 
	 * @param ip: ImageProcessor 
	 * @param minimumarea: threshold area. 
	 * @return binary image excluded with small ones and edge ones.
	 */
	ImageProcessor particleFilter(ImageProcessor ip, int minimumarea){
		int MAXSIZE = 10000;
		int MINSIZE = minimumarea;
		int options = analysisOptions();
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer p = new ParticleAnalyzer(options, ParticleAnalyzer.AREA, rt, MINSIZE, MAXSIZE);
		p.setHideOutputImage(true);
		p.analyze(new ImagePlus("t", ip));
		if (rt.getCounter() < 1)
			return ip;
		else if (rt.getCounter() == 1) {//there is only one image, so straight forwared. 
			p.getOutputImage().getProcessor().invertLut();
			return p.getOutputImage().getProcessor(); 
		} else {
			ImageProcessor ipout = p.getOutputImage().getProcessor();
			ipout.invertLut();
			ImageProcessor ipout2 = ipout.duplicate();
			dilateerode(ipout);
			if (testWatershedFast(ipout, ipout2)) // if watershed recommended
				return ipout2;
			else	
				return ipout;
				
		}
	}

	int analysisOptions(){
		int options = 
				ParticleAnalyzer.SHOW_MASKS + 
				ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES +
				ParticleAnalyzer.INCLUDE_HOLES +
				ParticleAnalyzer.CLEAR_WORKSHEET;
		return options;
	}
	
	void dilateerode(ImageProcessor ip){
		Binary binner = new Binary();
		binner.setup("dilate", null);
		binner.run(ip);
		binner.setup("erode", null);
		binner.run(ip);
	}
}
