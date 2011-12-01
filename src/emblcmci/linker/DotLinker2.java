package emblcmci.linker;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.ResultsTable;

public class DotLinker2 extends DotLinker{
	
	int znum = 27; //this should be at some point be given
	
	public DotLinker2(ImagePlus imp) {
		super(imp);
		// TODO Auto-generated constructor stub
	}
	
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
