package emblcmci.seg;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
	public void constructNodes(int roisize){
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
	 * -- 1.3. Second loop Nodes: Count number of segmented nucleus.
	 * v 2.1 if there is more than one, search for other nodes that is within same ROi frame. 
	 * v 2.1.1 if there are multiple dots, average only two-dots-coupled-ones as a Node.
	 * v 2.1.2  remove one of the two nodes. 
 	 * 3. redo the tracking and linking.
	 * 4. Do Analyze particle. 
	 * 5. Store morphological parameters 
	 * 6. update Node coordinate. 
	 * 7. Check if Dot coordinate is within segmented area. 
	 */
	@SuppressWarnings("unchecked")
	public void analyzeDotsandBinImages(){
		if (nodes == null)
			IJ.log("should construct nodes first. Exits.");
		
		//step 1: size filtering
		IJ.log("... now nodes count: " + nodes.size());
		IJ.log("... working on size and excluding nucleus at image edge.");
		for (Node n : nodes){
			ImagePlus out = firstAnalysis(n, MIN_NUCLUS_AREA);
			if ( out == null){			
				n.toRemove = true;
			} else {
				out.getProcessor().invertLut();
				int[] roiA = ROI_2_INTA.getDim2(n.getOrgroi());
				int ox = (int) n.getX() - roiA[0];
				int oy = (int) n.getY() - roiA[1];
				
				//n.setBinip(out.getProcessor());
				int pixval = out.getProcessor().getPixel(ox, oy);
				if (pixval > 0){
					n.toRemove = false;
					n.setBinip(out.getProcessor());
				} else
					n.toRemove = true;
			}
		}
//		Iterator<Node> it = nodes.iterator();
//		while(it.hasNext()){
//			if ( it.next().toRemove);
//				it.remove();
//		}
		for (Iterator<Node> it = nodes.iterator(); it.hasNext();){
			if (it.next().toRemove)
				it.remove();
		}
			
//		//TODO
//		ArrayList<Node> newnodes = (ArrayList<Node>) nodes.clone();
//		for (Node n : nodes){
//			if (n.toRemove){
//				IJ.log("... size/edge filter: removed nuc " + n.getId());
//				newnodes.remove(n);
//			}
//		}
//		nodes = (ArrayList<Node>) newnodes.clone();
		
		// 2nd screen & meging, check for overlapped dots / nuc.
		IJ.log("... now nodes count: " + nodes.size());
		IJ.log("... working on second screening, merging multiple dos per nucleus");
		//int regioncount = 0;
		ArrayList<Node> nodes2 = (ArrayList<Node>) nodes.clone();
		HashMap<Node, ArrayList<Integer>> mergemap = new HashMap<Node, ArrayList<Integer>>();
		for (Node n : nodes){
			ArrayList<Integer> mergelist = new ArrayList<Integer>();
//			conip = connextedRegions(n.getBinip());
//			regioncount = (int) conip.getStatistics().max;
			for (Node n2 : nodes2){
				if (checkCoHabitance(n, n2))
					mergelist.add(n2.getId());
			}
			if (mergelist.size() > 0){
				//IJ.log("Node " + n.getId() + " averages with " + mergelist.toString());
				if ((0 < mergelist.size() ) && ( mergelist.size() < 5))
					mergemap.put(n, mergelist);
			}
		}
		ArrayList<Node> removenodes = new ArrayList<Node>();
		for (Node n : mergemap.keySet()){
			if (!removenodes.contains(n)){
				double nx = n.getX();
				double ny = n.getY();
				for (Integer id : mergemap.get(n)){
					Node n2 = getNodefromID(id);
					nx += n2.getX();
					ny += n2.getY();
					removenodes.add(n2);
				}
				int size = mergemap.get(n).size() + 1;
				// update node coordinate
				n.setX( Math.round( nx / size));
				n.setY( Math.round(ny) / size);			
			}
		}
		for (Node n: removenodes){
			if (nodes.remove(n));
				//IJ.log("... node" + n.getId() + " removed for node merge");
		}
		IJ.log("... now nodes count: " + nodes.size());
		
		//third adjustment, based on nucleus shape, adjust the position to 
		//centroid.
		for (Node n : nodes)
			setDotToCentroid(n);
		
	}
	
	
	
	Node getNodefromID(Integer id){
		for (Node n : nodes)
			if (n.getId() == id)
				return n;
		return null;
	}
	
	/**
	 * Takes two Nodes and check if they are on a same nucleus. 
	 * @return
	 */
	boolean checkCoHabitance(Node n1, Node n2){
		if (n1.getFrame() != n2.getFrame())
			return false;
		if ( n1.getId() == n2.getId() )
			return false;
		Roi r1 = n1.getOrgroi();
		if (!r1.contains( (int) n2.getX(), (int) n2.getY()))
			return false;
		Roi r2 = n2.getOrgroi();
		if (!r2.contains( (int) n1.getX(), (int) n1.getY()))
			return false;
		int n1x, n1y, n2x, n2y;
		int n1pix, n2pix;
		int[] ps1 = ROI_2_INTA.getDim2(r1);
		n1x = ((int) n1.getX()) - ps1[0]; // subtract offset in X
		n1y = ((int) n1.getY()) - ps1[1]; // subtract offset in XY
		n2x = ((int) n2.getX()) - ps1[0]; // subtract offset in X
		n2y = ((int) n2.getY()) - ps1[1]; // subtract offset in XY
		ImageProcessor conip = connextedRegions(n1.getBinip());
		n1pix = conip.getPixel(n1x, n1y);				
		n2pix = conip.getPixel(n2x, n2y);
		if (n1pix != n2pix)
			return false;
		
		int[] ps2 = ROI_2_INTA.getDim2(r2);
		n1x = ((int) n1.getX()) - ps2[0]; // subtract offset in X
		n1y = ((int) n1.getY()) - ps2[1]; // subtract offset in XY
		n2x = ((int) n2.getX()) - ps2[0]; // subtract offset in X
		n2y = ((int) n2.getY()) - ps2[1]; // subtract offset in XY
		conip = connextedRegions(n2.getBinip());
		n1pix = conip.getPixel(n1x, n1y);				
		n2pix = conip.getPixel(n2x, n2y);		
		if (n1pix != n2pix)
			return false;
		else 
			return true;
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
		int options = firstAnalysisOptions();
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer p = new ParticleAnalyzer(options, AREA, rt, MINSIZE, MAXSIZE);
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
				//EXCLUDE_EDGE_PARTICLES +
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
	 * adjust the Node coordinate to nucleus centroid. 
	 * @param n
	 */
	void setDotToCentroid(Node n){
		int MAXSIZE = 10000;
		int MINSIZE = 100;
		int options = secondAnalysisOptions();
		ResultsTable rt = new ResultsTable();
		ParticleAnalyzer p = 
				new ParticleAnalyzer(options, CENTROID, rt, MINSIZE, MAXSIZE);
		p.setHideOutputImage(true);
		p.analyze(new ImagePlus("t", n.getBinip()));
		int[] roia = ROI_2_INTA.getDim2(n.getOrgroi());
		ImagePlus map = p.getOutputImage();
		int pixval = map.getProcessor().getPixel((int) n.getX(), (int) n.getY());
		if (pixval > 0){
			double cx = (int) rt.getValue("X", pixval - 1);
			double cy = (int) rt.getValue("Y", pixval - 1);
			n.setX(roia[0] + cx);
			n.setY(roia[1] + cy);
		}
	}
	int secondAnalysisOptions(){
		int options = 
				SHOW_ROI_MASKS + 
				EXCLUDE_EDGE_PARTICLES +
				INCLUDE_HOLES +
				CLEAR_WORKSHEET;
		return options;
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
