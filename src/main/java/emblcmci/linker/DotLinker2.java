package emblcmci.linker;
import java.io.File;
import java.util.Iterator;
import java.util.Vector;

import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.measure.ResultsTable;

public class DotLinker2 extends AbstractDotLinker{
	
	int znum = 27; //this should be at some point be given
	
//	static String datapath = "C:\\dropbox\\My Dropbox\\Mette\\centroid position ROI_1_4_embryoComma.csv";
	String datapath = "C:\\dropbox\\My Dropbox\\Mette\\dummy.csv";	
	public DotLinker2(ImagePlus imp) {
		super(imp);
		// TODO Auto-generated constructor stub
	}
	
	public DotLinker2(ImagePlus imp, int linkrange, double displacement) {
		// TODO Auto-generated constructor stub
		super(imp, linkrange, displacement);
	}



	/**
	 * Loads data from file system.
	 * currently, Volocity csv file must be converted to tab delimited.
	 * use "volocityResultsLoader.js" 
	 */
	/* (non-Javadoc)
	 * @see emblcmci.linker.DotLinker#dataloader()
	 */
	public StackFrames[] dataloader(){
		String[] dataA;
		//File fi = new File(datapath);
		
		//check if the file exists
		//if (!fi.exists()){
//		OpenDialog od = new OpenDialog("track file", OpenDialog.getLastDirectory());
		OpenDialog od = new OpenDialog("track file", "");
		datapath = od.getDirectory() + od.getFileName();
		//}
		IJ.log("Selected file:\n " + datapath);
		// data from text file
		String str = IJ.openAsString(datapath);
		String[] linesA = ij.util.Tools.split(str, "\n");
		int[] timeA = new int[linesA.length-1];
		
		//pixel coordinates
		float[] xA = new float[linesA.length-1];
		float[] yA = new float[linesA.length-1];
		float[] zA = new float[linesA.length-1];
		
		//scaled coordinates
		float[] sxA = new float[linesA.length-1];
		float[] syA = new float[linesA.length-1];
		float[] szA = new float[linesA.length-1];
		
		float[] volA = new float[linesA.length-1];
		float[] svolA = new float[linesA.length-1];
		float[] meanintA = new float[linesA.length-1];
		float[] totalintA = new float[linesA.length-1];

		for (int i = 0; i < timeA.length; i++){
			//dataA = ij.util.Tools.split(linesA[i+1], "\t");
			dataA = ij.util.Tools.split(linesA[i+1], ",");
/*
 * 			// old version, manually converted column positions
 * 			timeA[i] =  Integer.valueOf(dataA[4]).intValue();
			xA[i] =  Float.valueOf(dataA[8].trim()).floatValue();
			yA[i] =  Float.valueOf(dataA[9].trim()).floatValue();
			zA[i] =  Float.valueOf(dataA[10].trim()).floatValue();
			sxA[i] =  Float.valueOf(dataA[11].trim()).floatValue();
			syA[i] =  Float.valueOf(dataA[12].trim()).floatValue();
			szA[i] =  Float.valueOf(dataA[13].trim()).floatValue();
*/
			// 20111212 version, importing file converted by volocityResultsLoader.js
//			timeA[i] =  Integer.valueOf(dataA[2]).intValue(); //modified to one below, 20120120
			timeA[i] = (int) Float.valueOf(dataA[2]).floatValue();
			volA[i] =  Float.valueOf(dataA[3].trim()).floatValue();
			svolA[i] =  Float.valueOf(dataA[4].trim()).floatValue();
			meanintA[i] =  Float.valueOf(dataA[7].trim()).floatValue();
			totalintA[i] =  Float.valueOf(dataA[8].trim()).floatValue();
			xA[i] =  Float.valueOf(dataA[10].trim()).floatValue();
			yA[i] =  Float.valueOf(dataA[11].trim()).floatValue();
			zA[i] =  Float.valueOf(dataA[12].trim()).floatValue();
			sxA[i] =  Float.valueOf(dataA[13].trim()).floatValue();
			syA[i] =  Float.valueOf(dataA[14].trim()).floatValue();
			szA[i] =  Float.valueOf(dataA[15].trim()).floatValue();			
		}
		IJ.log(" --- volocity data format ---");
		int startframe = timeA[1];
		int endframe = timeA[timeA.length-1];		
		int framenumber = endframe - startframe + 1;
		IJ.log("start frame:" + Integer.toString(startframe));
		IJ.log("end frame:" + Integer.toString(endframe));		
		IJ.log("frame number:" + Integer.toString(framenumber));
		frameA = new StackFrames[framenumber];
		for (int i = 0; i < framenumber; i++){
			frameA[i] = new StackFrames(i);
			//frameA[i].particles.next = new Particle[linkrange];
		}
		// fill in the Myframe object
		for (int i = 0 ; i< timeA.length; i++){
//			Particle particle = new Particle(xA[i], yA[i], zA[i], (int) (timeA[i] - 1), i);
			Particle particle = new Particle(xA[i], yA[i], zA[i], sxA[i], syA[i], szA[i], (int) (timeA[i] - 1), i);
			particle.setVolume(volA[i]);
			particle.setSvolume(svolA[i]);
			particle.setMeanint(meanintA[i]);
			particle.setTotalint(totalintA[i]);
			
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
					rt.addValue("SXpos", ptcls[i].getSx());
					rt.addValue("SYpos", ptcls[i].getSy());
					rt.addValue("SZpos", ptcls[i].getSz());					
					rt.addValue("ParticleID", ptcls[i].getParticleID());
					rt.addValue("Volume", ptcls[i].getVolume());					
					rt.addValue("VolumeScaled", ptcls[i].getSvolume());					
					rt.addValue("MeanInt", ptcls[i].getMeanint());					
					rt.addValue("TotalInt", ptcls[i].getTotalint());					
					
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
	
	/** data loader for Volocity file. 
	 *  depricated
	 */
	public StackFrames[] dataloaderOLD(){
		
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


}
