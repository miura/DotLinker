package de.embl.cmci.io;

import ij.IJ;
import ij.measure.ResultsTable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import de.embl.cmci.linker.DotLinker;
import de.embl.cmci.linker.DotLinker.Particle;
import de.embl.cmci.linker.DotLinker.StackFrames;
import de.embl.cmci.linker.DotLinker.Trajectory;

public class ResultsTableLoader implements IDataLoader {

	private StackFrames[] frameA;
	private DotLinker dotlinker;

	@Override
	public StackFrames[] dataLoader(DotLinker dl) {
		if (checkResultsTableParameters()){
			this.dotlinker = dl;
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
			IJ.error("Results table has missing column");
			return null;
		}
	}

	@Override
	public ResultsTable showTrajectoryTable(Vector<Trajectory> all_traj) {
		ResultsTable rt = new ResultsTable();
		
		Iterator<Trajectory> iter = all_traj.iterator();  	   
		int rowcount = 0;
		
		while (iter.hasNext()) {
			Trajectory curr_traj = iter.next();
			Particle[] ptcls = curr_traj.getExisting_particles();
			calcAreaFraction(curr_traj);
			if (ptcls.length > dotlinker.getTrajectoryThreshold()){
				for (int i = 0; i < ptcls.length; i++){
					rt.incrementCounter();
					rt.addValue("TrackID", curr_traj.getSerial_number());
					rt.addValue("frame", ptcls[i].getFrame());
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
	public void calcAreaFraction(Trajectory track){
		Particle[] ptcles = track.getExisting_particles();
		double area0 = (double) ptcles[0].getArea();
		double carea;
		int counter = 0;
		Particle p;
		double minimum = 1000;
		double maximum = 0;
		for (int i = 0; i < ptcles.length; i++) {
			p = ptcles[i];
			carea = (double) p.getArea();
			p.areafraction = carea / area0;			
			counter++;
			if (p.areafraction < minimum)
				minimum = p.areafraction;
			
			if (p.areafraction > maximum)
				maximum = p.areafraction;
		}
		track.areafracMIN = minimum;
		track.areafracMAX = maximum;		
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
}
