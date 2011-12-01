package emblcmci.linker;
import java.util.Iterator;
import java.util.Vector;

import emblcmci.linker.DotLinker.Particle;
import emblcmci.linker.DotLinker.Trajectory;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;

public class DotLinker2 extends DotLinker{
	
	int znum = 27; //this should be at some point be given
	
	public DotLinker2(ImagePlus imp) {
		super(imp);
		// TODO Auto-generated constructor stub
	}
	
	public DotLinker2(ImagePlus imp, int linkrange, double displacement) {
		// TODO Auto-generated constructor stub
		super(imp, linkrange, displacement);
	}

	/** data loader for Volocity file. 
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
		IJ.log(" --- volocity data format ---");
		float[] xA = rt.getColumn(rt.getColumnIndex("Centroid X"));
		float[] yA = rt.getColumn(rt.getColumnIndex("Centroid Y"));
		float[] zA = rt.getColumn(rt.getColumnIndex("Centroid Z"));
//		float[] areaA = rt.getColumn(rt.getColumnIndex("Area"));
		float[] timeA = rt.getColumn(rt.getColumnIndex("Timepoint"));
//		float[] sliceAsort = sliceA.clone();
//		Arrays.sort(sliceAsort);
//		int startframe = (int) sliceAsort[0];
//		int endframe = (int) sliceAsort[sliceAsort.length-1];
		int startframe = (int) timeA[0];
		int endframe = (int) timeA[timeA.length-1];		
		int framenumber = endframe - startframe + 1;
		frameA = new StackFrames[framenumber];
		for (int i = 0; i < framenumber; i++){
			frameA[i] = new StackFrames(i);
			//frameA[i].particles.next = new Particle[linkrange];
		}
		// fill in the Myframe object
		for (int i = 0 ; i< timeA.length; i++){
			Particle particle = new Particle(xA[i], yA[i], zA[i], (int) (timeA[i] - 1), i);
			frameA[particle.frame].particles.add(particle);
		}
		return frameA;
	}
	
	/** overrides. 
	* currently dummy, should be implemented at some point.
	*/ 
	public boolean checkResultsTableParameters(){
		boolean rtOK = false;
/*		ResultsTable rt = ResultsTable.getResultsTable();
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
		}*/
		rtOK = true;
		return rtOK;
	}
	
	/**
	 * modified version of showTrajectoryTable()
	 * overrides
	 */
	public ResultsTable showTrajectoryTable(Vector<Trajectory> all_traj){
		ResultsTable rt = new ResultsTable();
		
		Iterator<Trajectory> iter = all_traj.iterator();  	   
//		int rowcount = 0;
		
		while (iter.hasNext()) {
			Trajectory curr_traj = iter.next();
			Particle[] ptcls = curr_traj.existing_particles;
			calcAreaFraction(curr_traj);
			if (ptcls.length > getTrajectoryThreshold()){
				for (int i = 0; i < ptcls.length; i++){
					rt.incrementCounter();
					rt.addValue("TrackID", curr_traj.serial_number);
					rt.addValue("frame", ptcls[i].frame);
					rt.addValue("Xpos", ptcls[i].getX());
					rt.addValue("Ypos", ptcls[i].getY());
					rt.addValue("Zpos", ptcls[i].getZ());
					//rt.addValue("Area", ptcls[i].area);
					//rt.addValue("AreaFraction", ptcls[i].areafraction);
				}
			}

		}
		return rt;
//		IJ.log("Mac track length = " + maxlength);
//		for (int i =1; i<counter.length; i++)
//			IJ.log("track length " + i + ": " + counter[i]);
	}


}
