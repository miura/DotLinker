package de.embl.cmci.io;

import ij.IJ;
import ij.measure.ResultsTable;

import java.util.Arrays;
import java.util.Vector;

import de.embl.cmci.linker.DotLinker;
import de.embl.cmci.linker.DotLinker.Particle;
import de.embl.cmci.linker.DotLinker.StackFrames;
import de.embl.cmci.linker.DotLinker.Trajectory;

/**
 * Loads data from one of the following:
 * 	1. Results window (such as the particle analysis results)
 *  2. saved Results Table as a csv file
 *  3. ResultsTable object
 *  
 * This class was initially wrote for Tina@DeLenzis, then furether exrtended.
 * 
 * @author Kota Miura
 * Centre for Molecular and Cellular Imaging, EMBL Heidelberg
 * 20110830 
 * 	First Version, Currently assumes only 2D sequences. 
 * 
 * 20140400 
 * 	Changed as an inherited class of abstract to an implementation of IDataLoader
 *  Two additional constructors with an argument, path or ResultsTable instance. 
 */

public class ResultsTableLoader implements IDataLoader {

	private StackFrames[] frameA;
	private DotLinker dotlinker;
	private boolean loadFromFile = false;
	private ResultsTable rt;

	public ResultsTableLoader(){
		loadFromFile = false;
		ResultsTable rt = ResultsTable.getResultsTable();
		this.rt = rt;
	}

	/** This constructor is for instanciation of DotLinker object from script. 
	 *  It could be also in future be called from GUI, to load data file interactively. 
	 *  20140404
	 * @param path
	 */
	public ResultsTableLoader(String path){
		loadFromFile = true;
		ResultsTable rt = ResultsTable.open2(path);
		this.rt = rt;
	}

	public ResultsTableLoader(ResultsTable rt){
		loadFromFile = false;
		this.rt = rt;
	}	
	
	@Override
	public StackFrames[] dataLoader(DotLinker dl) {
		if (checkResultsTableParameters()){
			this.dotlinker = dl;
			//data loading from results table
			//ResultsTable rt = ResultsTable.getResultsTable();
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
			frameA = new DotLinker.StackFrames[framenumber];
			for (int i = 0; i < framenumber; i++){
				frameA[i] = dl.new StackFrames(i);
				//frameA[i].particles.next = new Particle[linkrange];
			}
			// fill in the Myframe object
			for (int i = 0 ; i< sliceA.length; i++){
				Particle particle = dl.new Particle(xA[i], yA[i], (int) (sliceA[i] - 1), areaA[i], i);
				frameA[particle.getFrame()].getParticles().add(particle);
			}
			return frameA;
		} else {
			return null;
		}
	}

	@Override
	public ResultsTable showTrajectoryTable(Vector<Trajectory> all_traj) {
		DataWriterArea2D writer = new DataWriterArea2D();
		return writer.writeTrajectoryTable(all_traj, dotlinker);
//		ResultsTable rt = new ResultsTable();
//			   
//		for (Trajectory curr_traj : all_traj){
//			Particle[] ptcls = curr_traj.getExisting_particles();
//			calcAreaFraction(curr_traj);
//			if (ptcls.length > dotlinker.getTrajectoryThreshold()){
//				for (int i = 0; i < ptcls.length; i++){
//					rt.incrementCounter();
//					//IJ.log(Integer.toString(curr_traj.getSerial_number()));
//					rt.addValue("TrackID", curr_traj.getSerial_number());
//					rt.addValue("frame", ptcls[i].getFrame());
//					rt.addValue("Xpos", ptcls[i].getX());
//					rt.addValue("Ypos", ptcls[i].getY());
//					rt.addValue("Area", ptcls[i].getArea());
//					rt.addValue("AreaFraction", ptcls[i].areafraction);
//				}
//			}
//
//		}
//		return rt;
////		IJ.log("Mac track length = " + maxlength);
////		for (int i =1; i<counter.length; i++)
////			IJ.log("track length " + i + ": " + counter[i]);
	}
//	public void calcAreaFraction(Trajectory track){
//		Particle[] ptcles = track.getExisting_particles();
//		double area0 = (double) ptcles[0].getArea();
//		double carea;
//		Particle p;
//		double minimum = 1000;
//		double maximum = 0;
//		for (int i = 0; i < ptcles.length; i++) {
//			p = ptcles[i];
//			carea = (double) p.getArea();
//			p.areafraction = carea / area0;			
//			if (p.areafraction < minimum)
//				minimum = p.areafraction;
//			
//			if (p.areafraction > maximum)
//				maximum = p.areafraction;
//		}
//		track.areafracMIN = minimum;
//		track.areafracMAX = maximum;		
//	}
	// analyze particle case. 
	public boolean checkResultsTableParameters(){
		boolean rtOK = false;
		//ResultsTable rt = ResultsTable.getResultsTable();
		if (rt != null){
			if (rt.getColumnIndex("Area") != -1)
				if (rt.getColumnIndex("X") != -1)
					if (rt.getColumnIndex("Y") != -1)
						if (rt.getColumnIndex("Slice") != -1)
							rtOK = true;
						else {
							IJ.log("some results parameter missing:");
							IJ.log(rt.getColumnHeadings());
						}
		} else {
			IJ.log("need Analyze particle Results!");
		}
		return rtOK;
	}	
}
