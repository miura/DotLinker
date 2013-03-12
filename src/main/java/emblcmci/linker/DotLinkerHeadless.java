package emblcmci.linker;

import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;
import java.util.Vector;

public class DotLinkerHeadless extends AbstractDotLinker {

	int[] xA;
	int[] yA;
	int[] timeA;
	
	public DotLinkerHeadless() {
	}	

	public DotLinkerHeadless(ImagePlus imp) {
		super(imp);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean checkResultsTableParameters() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResultsTable showTrajectoryTable(Vector<Trajectory> all_traj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StackFrames[] dataloader() {
		IJ.log(" --- data  ---");
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
			Particle particle = new Particle(xA[i], yA[i], (int) (timeA[i] - 1), i);
			
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

}
