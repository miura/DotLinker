package emblcmci.seg;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;

import java.util.ArrayList;

import util.FindConnectedRegions;
import util.FindConnectedRegions.Results;

import emblcmci.obj.Node;

/**
 * Associate MaxFinder Dots with Segmented Nucleus images.
 * Segmentation of nucleus is first done (using 2009 algorithm).
 * And then evaluates the dots as well, and also calcualte morphological parameters.
 * 
 * In terms of tracking:
 * prepare nodes first and to the tracking afterwards using a group of nodes.  
 * @author Kota Miura
 * 201303201-
 *  *
 */
public class NucleusExtractor extends ParticleAnalyzer {
	int[] xA, yA, fA;
	private ImagePlus imp;
	private ArrayList<Node> nodes;
	final int MIN_NUCLUS_AREA = 1000;
	
	public static final RoiToIntA ROI_2_INTA = new RoiToIntA();
	
	/**
	 * 
	 */
	public NucleusExtractor(ImagePlus imp, int[] xA, int[] yA, int[] fA) {
		super();
		setDotCoords(xA, yA, fA);
		this.imp = imp;
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}

	/**
	 * segmentation from original sub image and initialization of Nodes. 
	 * 
	 * @param roisize determines the size of the subimage pixel size. 
	 * @return
	 */
	public ArrayList<Node> constructNodes(int roisize){
		NucSegRitsukoProject nrp = new NucSegRitsukoProject();
		nrp.getPerNucleusBinImgProcessors(imp, roisize, xA, yA, fA);
		ArrayList<ImageProcessor> ipList = nrp.getIpList();
		ArrayList<Roi> roiList = nrp.getRoiList();
		ArrayList<ImageProcessor> binList = nrp.getBinList();
		
		nodes = new ArrayList<Node>();
		for (int i = 0; i < fA.length; i++){
			Node n = new Node(xA[i], yA[i], fA[i], i);
			n.setOrgroi(roiList.get(i));
			n.setOrgip(ipList.get(i));
			n.setBinip(binList.get(i));
			nodes.add(n);	
		}
		return null;	
	}


	void setDotCoords(int[] xA, int[] yA, int[] fA){
		this.xA = xA;
		this.yA = yA;
		this.fA = fA;
	}
	
	// from here down will be dot and nucleus evaluation using binary image.
	
	/** 
	 * 1. Do particle analysis
	 * v 1.1. Eliminates edge touching nucleus.
	 * v 1.2. First loop Nodes: Check the size of the nucleus. 
	 * v 1.2.1. if its too small or none, then that Node should be eliminated.   
	 * (maybe refine the position already?)
	 * 1.3. Second loop Nodes: Count number of segmented nucleus.
	 * 1.3.1 if there is more than one, search for other nodes that is within same ROi frame. 
	 * 1.3.1.1 if there are multiple dots, average those dot positions. Select one Node.
	 * 1.4 redo the tracking and linking.
	 * 1.3. Redo Analyze particle. 
	 * 1.4. Store morphological parameters 
	 * 1.4.1 update Node coordinate. 
	 * 1. Check if Dot coordinate is within segmented area. 
	 */
	@SuppressWarnings("unchecked")
	public void analyzeDotsandBinImages(){
		if (nodes == null)
			IJ.log("should construct nodes first. Exits.");
		//step 1: size filtering
		//ImageStack remove1stk = new ImageStack(nodes.get(0).getBinip().getWidth(), 
		//		nodes.get(0).getBinip().getHeight());
		for (Node n : nodes){
			ImagePlus out = firstAnalysis(n, MIN_NUCLUS_AREA);
			if ( out == null){			
				//nodes.remove(n);
//				is for development. 
//				n.getBinip().setColor(126);
				int roix = ROI_2_INTA.getDim4(n.getOrgroi())[0];
				int roiy = ROI_2_INTA.getDim4(n.getOrgroi())[1];
//				n.getBinip().drawOval((int) n.getX()-roix-2, (int) n.getY() - roiy -2, 5, 5);
//				IJ.log("Node to be Removed:" + n.getId() + " c:" + roix + ", " + roiy);
//				remove1stk.addSlice(n.getBinip());
				n.toRemove = false;
			} else {
				out.getProcessor().invertLut();
				n.setBinip(out.getProcessor());
			}
		}
//		for (Integer i : removeIDlist){
//			newnode.remove(i);
		ArrayList<Node> newnodes = (ArrayList<Node>) nodes.clone();
		for (Node n : nodes){
			if (n.toRemove){
				IJ.log("... size/edge filter: removed nuc " + n.getId());
				newnodes.remove(n);
			}
		}
		nodes = (ArrayList<Node>) newnodes.clone();
		
		// 2nd analysis, check for overlapped dots / nuc.
		ImageProcessor conip; //connected region map
		int regioncount = 0;
		ArrayList<Node> nodes2 = (ArrayList<Node>) nodes.clone();
		ArrayList<Node> newnodes2 = new ArrayList<Node>();
		Roi r;
		int nx, ny;
		int n1pix, n2pix;
		for (Node n : nodes){
			ArrayList<Integer> mergelist = new ArrayList<Integer>();
			conip = connextedRegions(n.getBinip());
			regioncount = (int) conip.getStatistics().max;
			r = n.getOrgroi();
			int[] ps = ROI_2_INTA.getDim2(r);
			for (Node n2 : nodes2){
				if (n.getFrame() == n2.getFrame()){
					if ( n.getId() != n2.getId() ){
						if (r.contains( (int) n2.getX(), (int) n2.getY())){
							nx = ((int) n2.getX()) - ps[0]; // subtract offset in X
							ny = ((int) n2.getY()) - ps[1]; // subtract offset in XY
							n2pix = conip.getPixel(nx, ny);
							nx = ((int) n.getX()) - ps[0]; // subtract offset in X
							ny = ((int) n.getY()) - ps[1]; // subtract offset in XY
							n1pix = conip.getPixel(nx, ny);			
							if (n1pix == n2pix){ // two dots are in the same nucleus. Average positions
								mergelist.add(n2.getId());
							}
						}
					}
				}
			}
			if (mergelist.size() > 0){
				IJ.log("Node " + n.getId() + " averages with " + mergelist.toString());
			}
		}
		
		
	}
	
	/**
	 *  Eliminates dots very small segmented signal. 
	 * @param n
	 * @param minimumarea: threshold area. 
	 * @return binary image of the nucleus.
	 */
	ImagePlus firstAnalysis(Node n, int minimumarea){
		int MAXSIZE = 10000;
		int MINSIZE = minimumarea;
		ParticleAnalyzer p = null;
		int options = firstAnalysisOptions();
		ResultsTable rt = new ResultsTable();
		p = new ParticleAnalyzer(options, AREA, rt, MINSIZE, MAXSIZE);
		p.setHideOutputImage(true);
		p.analyze(new ImagePlus("t", n.getBinip()));
		if (rt.getCounter() < 1){
			return null;
		} else 
			return p.getOutputImage();
	}

	int firstAnalysisOptions(){
		int options = 
				SHOW_MASKS + 
				EXCLUDE_EDGE_PARTICLES +
				INCLUDE_HOLES +
				CLEAR_WORKSHEET;
		return options;
	}
		
	// not used this currently. 
	// maybe use this if removal is too much
	ImagePlus firstSubAnalysis(ImageProcessor ip, int minimumarea){
		int MAXSIZE = 10000;
		int MINSIZE = minimumarea;
		//ParticleAnalyzer p = new ParticleAnalyzer();
		int options = 
				SHOW_MASKS + 
				//p.EXCLUDE_EDGE_PARTICLES +
				INCLUDE_HOLES +
				CLEAR_WORKSHEET;
		int measures = 
				AREA;
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer p = new ParticleAnalyzer(options, measures, rt, MINSIZE, MAXSIZE);
		p.setHideOutputImage(true);
		p.analyze(new ImagePlus("t", ip));
		if (rt.getCounter() < 1){
			return null;
		} else 
			return p.getOutputImage();
	}
	
	/**
	 * 
	 * @param segimp: degmented binary image
	 * @return connected regions mapped by ID. 
	 */
	ImageProcessor connextedRegions(ImageProcessor segip){

//		ij.ImagePlus imagePlus, 
//		boolean diagonal, 
//		boolean imagePerRegion, 
//		boolean imageAllRegions, 
//		boolean showResults, 
//		boolean mustHaveSameValue, 
//		boolean startFromPointROI, 
//		boolean autoSubtract, 
//		double valuesOverDouble, 
//		double minimumPointsInRegionDouble, 
//		int stopAfterNumberOfRegions, 
//		boolean noUI

		FindConnectedRegions fcr = new FindConnectedRegions();
		Results fcrresults = fcr.run(new ImagePlus("seg", segip), true, false, true, false, false, false, false, 100, 600, 10, true);
		ImagePlus allregionimp =  fcrresults.allRegions;
		return allregionimp.getProcessor();
	}
	
	private static class RoiToIntA {
		public int[] getDim4(Roi roi){
			int[] a = {
					roi.getBounds().x, 
					roi.getBounds().y,
					roi.getBounds().width,
					roi.getBounds().height
			};
			return a;
		}
		public int[] getDim2(Roi roi){
			int[] a = {
					roi.getBounds().x, 
					roi.getBounds().y
			};
			return a;
		}
	}


}
