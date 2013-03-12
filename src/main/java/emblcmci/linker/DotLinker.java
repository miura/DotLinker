package emblcmci.linker;


import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;

import emblcmci.linker.AbstractDotLinker;

/**
 * DotLinker
 * 
 * Loads data from Results window (such as the particle analysis results)
 * and links listed particles. Linked results will be drawn
 * on the duplicate of the original stack.
 * 
 * This class was initially wrote for Tina.
 * 
 * @author Kota Miura
 * Centre for Molecular and Cellular Imaging, EMBL Heidelberg
 * 20110830 First Version, Currently assumes only 2D sequences. 
 * 
 * Further development: interface for cost function and implementations. 
 */
public class DotLinker extends AbstractDotLinker{
//	double[] xA = rt.getColumn(rt.getColumnIndex("X"));
//	double[] yA = rt.getColumn(rt.getColumnIndex("Y"));
//	double[] areaA = rt.getColumn(rt.getColumnIndex("Area"));
//	double[] frameA = rt.getColumn(rt.getColumnIndex("Slice"));
	
	StackFrames[] frameA;
	private int TrajectoryThreshold;	
	
	public DotLinker(ImagePlus imp){
		super(imp);
		//this.imp = imp;
	}

	public DotLinker(ImagePlus imp, int linkrange, double displacement){
		super(imp, linkrange, displacement);
		//this.imp = imp;
		//this.linkrange = linkrange;
		//this.displacement = displacement;
	}	
	
	// analyze particle case. 
	public boolean checkResultsTableParameters(){
		boolean rtOK = false;
		ResultsTable rt = ResultsTable.getResultsTable();
		if (rt != null){
			if (rt.columnExists(ResultsTable.AREA))
				if (rt.columnExists(ResultsTable.X_CENTROID))
					if (rt.columnExists(ResultsTable.Y_CENTROID))
						if (rt.columnExists(ResultsTable.SLICE))
							rtOK = true;
						else 
							IJ.log("some results parameter missing");
		} else {
			IJ.log("need Analyze particle Results!");
		}
		return rtOK;
	}
	

	/** Loads data in Results table and place data in Myframe object
	 * @return 
	 * 
	 */
	public StackFrames[] dataloader(){
		
		//data loading from results table
		ResultsTable rt = ResultsTable.getResultsTable();
		if (rt == null){
			IJ.error("no  results table !");
			return null;
		}
		if (rt.getColumn(0).length <10){
			IJ.error("there seems to be almost no data...");
			return null;
		}
		
		float[] xA = rt.getColumn(rt.getColumnIndex("X"));
		float[] yA = rt.getColumn(rt.getColumnIndex("Y"));
		float[] areaA = rt.getColumn(rt.getColumnIndex("Area"));
		float[] sliceA = rt.getColumn(rt.getColumnIndex("Slice"));
		float[] sliceAsort = sliceA.clone();
		Arrays.sort(sliceAsort);
		int startframe = (int) sliceAsort[0];
		int endframe = (int) sliceAsort[sliceAsort.length-1];
		int framenumber = endframe - startframe + 1;
		frameA = new StackFrames[framenumber];
		for (int i = 0; i < framenumber; i++){
			frameA[i] = new StackFrames(i);
			//frameA[i].particles.next = new Particle[linkrange];
		}
		// fill in the Myframe object
		for (int i = 0 ; i< sliceA.length; i++){
			Particle particle = new Particle(xA[i], yA[i], (int) (sliceA[i] - 1), areaA[i], i);
			frameA[particle.frame].particles.add(particle);
		}
		return frameA;
	}

	
	public ResultsTable showTrajectoryTable(Vector<Trajectory> all_traj){
		ResultsTable rt = new ResultsTable();
		
		Iterator<Trajectory> iter = all_traj.iterator();  	   
		int rowcount = 0;
		
		while (iter.hasNext()) {
			Trajectory curr_traj = iter.next();
			Particle[] ptcls = curr_traj.existing_particles;
			calcAreaFraction(curr_traj);
			if (ptcls.length > TrajectoryThreshold){
				for (int i = 0; i < ptcls.length; i++){
					rt.incrementCounter();
					rt.addValue("TrackID", curr_traj.serial_number);
					rt.addValue("frame", ptcls[i].frame);
					rt.addValue("Xpos", ptcls[i].getX());
					rt.addValue("Ypos", ptcls[i].getY());
					rt.addValue("Area", ptcls[i].getArea());
					rt.addValue("AreaFraction", ptcls[i].areafraction);
				}
			}

		}
		return rt;
//		IJ.log("Mac track length = " + maxlength);
//		for (int i =1; i<counter.length; i++)
//			IJ.log("track length " + i + ": " + counter[i]);
	}



	
}
