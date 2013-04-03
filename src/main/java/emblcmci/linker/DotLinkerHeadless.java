package emblcmci.linker;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import emblcmci.obj.Node;
import emblcmci.obj.converters.NodesToCoordArrays;

/**
 * A simple DotLinker for use from scripts. 
 * Coordinate data could be set via setData method. 
 * @author Kota Miura <miura@embl.de>
 * <a href="http://cmci.embl.de">CMCI, EMBL</a>
 * @since 2013-03-12
 */
public class DotLinkerHeadless extends AbstractDotLinker {

	private static final int TrajectoryThreshold = 0;
	int[] xA;
	int[] yA;
	/**
	 * frame number, starts from 1
	 */
	int[] timeA;
	private int[] idA;
	
	public DotLinkerHeadless() {
	}	

	public DotLinkerHeadless(ImagePlus imp) {
		super(imp);
		// TODO Auto-generated constructor stub
	}
	
	public DotLinkerHeadless(ImagePlus imp, int linkrange, double displacement){
		super(imp, linkrange, displacement);
	}

	@Override
	public boolean checkResultsTableParameters() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultsTable showTrajectoryTable(Vector<Trajectory> all_traj) {
		ResultsTable rt = new ResultsTable();
		
		Iterator<Trajectory> iter = all_traj.iterator();  	   
		
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
				}
			}
		}
		return rt;			
	}

	/**
	 * dataloader directly via arguments of setData method. 
	 * 2D time series data. 
	 */
	@Override
	public StackFrames[] dataloader() {
		IJ.log(" --- data  ---");
		if (timeA == null) return null;
		int startframe = timeA[0];
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
			Particle particle = new Particle(xA[i], yA[i], (int) (timeA[i] - 1), idA[i]);
			
			frameA[particle.frame].particles.add(particle);
		}
		return frameA;
	}
	
	public boolean setData(int[] xA, int[] yA, int[] fA){
		this.xA = xA;
		this.yA = yA;
		this.timeA = fA;
		return true;
	}
	
	public boolean setData(ArrayList<Node> nodes){
		NodesToCoordArrays ntoa = new NodesToCoordArrays(nodes);
		this.idA = ntoa.getidA();
		this.xA = ntoa.getxA();
		this.yA = ntoa.getyA();
		this.timeA = ntoa.getfA();		
		return true;
	}

}
