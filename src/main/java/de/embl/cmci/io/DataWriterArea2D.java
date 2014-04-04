package de.embl.cmci.io;

import ij.measure.ResultsTable;

import java.util.Vector;

import de.embl.cmci.linker.DotLinker;
import de.embl.cmci.linker.DotLinker.Particle;
import de.embl.cmci.linker.DotLinker.Trajectory;

/**
 * Fromatting output and place track data into ResultsTable.
 * For Drosophila epidermal cell 2D tracking results. 
 * @author Kota Miura
 * 20140404 First version
 * 
 */
public class DataWriterArea2D implements IDataWriter{
	
	public ResultsTable writeTrajectoryTable(Vector<Trajectory> all_traj, DotLinker dotlinker) {
		ResultsTable rt = new ResultsTable();
			   
		for (Trajectory curr_traj : all_traj){
			Particle[] ptcls = curr_traj.getExisting_particles();
			calcAreaFraction(curr_traj);
			if (ptcls.length > dotlinker.getTrajectoryThreshold()){
				for (int i = 0; i < ptcls.length; i++){
					rt.incrementCounter();
					//IJ.log(Integer.toString(curr_traj.getSerial_number()));
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
		Particle p;
		double minimum = 1000;
		double maximum = 0;
		for (int i = 0; i < ptcles.length; i++) {
			p = ptcles[i];
			carea = (double) p.getArea();
			p.areafraction = carea / area0;			
			if (p.areafraction < minimum)
				minimum = p.areafraction;
			
			if (p.areafraction > maximum)
				maximum = p.areafraction;
		}
		track.areafracMIN = minimum;
		track.areafracMAX = maximum;		
	}
}
