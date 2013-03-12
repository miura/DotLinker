package emblcmci.linker;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.Roi;
import ij.gui.StackWindow;
import ij.measure.ResultsTable;
import ij.plugin.Duplicator;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

//import emblcmci.linker.AbstractDotLinker.Particle;
//import emblcmci.linker.AbstractDotLinker.StackFrames;
//import emblcmci.linker.AbstractDotLinker.Trajectory;
//import emblcmci.linker.AbstractDotLinker.TrajectoryCanvas;
//import emblcmci.linker.AbstractDotLinker.TrajectoryStackWindow;

public abstract class AbstractDotLinker {
//	double[] xA = rt.getColumn(rt.getColumnIndex("X"));
//	double[] yA = rt.getColumn(rt.getColumnIndex("Y"));
//	double[] areaA = rt.getColumn(rt.getColumnIndex("Area"));
//	double[] frameA = rt.getColumn(rt.getColumnIndex("Slice"));
	
	StackFrames[] frameA;
	private int linkrange = 2;
	private double displacement = 5;
	private Vector<Trajectory> all_traj;
	private int number_of_trajectories;
	private ImagePlus imp;
	private int frames_number;
	private int TrajectoryThreshold;	
	
	public AbstractDotLinker(ImagePlus imp){
		this.imp = imp;
	}

	public AbstractDotLinker(ImagePlus imp, int linkrange, double displacement){
		this.imp = imp;
		this.linkrange = linkrange;
		this.displacement = displacement;
	}	
	/**
	 * @return the trajectoryThreshold
	 */
	public int getTrajectoryThreshold() {
		return TrajectoryThreshold;
	}

	/**
	 * @param trajectoryThreshold the trajectoryThreshold to set
	 */
	public void setTrajectoryThreshold(int trajectoryThreshold) {
		TrajectoryThreshold = trajectoryThreshold;
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
	
	/** Method that should be called from a plugin, or from scripts to
	 * do all the processing. 
	 */
	public void doLinking(LinkCosts linkcostmethod, boolean showtrack){
		frameA = dataloader();
		if (frameA !=null){
			
			IJ.showStatus("Linking Particles");		
			linkParticles(frameA, frameA.length, linkrange, displacement, linkcostmethod);
			this.frames_number = frameA.length;
		}
		else {
			IJ.error("data loading from Results table failed");
			return;
		}
		IJ.showStatus("Generating Trajectories");		
		generateTrajectories(frameA, frameA.length);
		
		// viewing the trajectories

		if (showtrack)
			generateView(); //plot with xy coordinate inverted, the particle tracker bug 
		
		printTrajectories();
		//putLinkedParticeID();
		ResultsTable trackrt = showTrajectoryTable(all_traj);
		trackrt.show("Tracks");
	}
	
	/** simplified version of MyFrame class in Particle tracker. 
	 * Holds particle array and frame number and area. 
	 */
	public class StackFrames {
		int frameID = 0; //here, frame starts from 1 while in ImageJ slice number starts from 1
		Vector<Particle> particles;
		
		public StackFrames (int frameID){
			this.frameID = frameID;
			this.particles = new Vector<Particle>(); 
		}

		public Vector<Particle> getParticles() {
			return particles;
		}
	}
	
	public class Particle {
		private float x = 0;
		private float y = 0;
		private float z = 0;
		private float sx = 0; //scaled coordinate x
		private float sy = 0; //scaled coordinate y
		private float sz = 0; //scaled coordinate z
		int frame = 0;			//starts from 0
		private float area = 0; //2D useage
		private float volume = 0;
		private float svolume = 0; //scaled volume
		private float meanint = 0;
		private float totalint = 0;
		
		private int particleID = 0;
		int[] next = null;		//array to hold linked ids
		boolean special;		// a flag that is used while detecting and linking particles
		
		boolean hasArea = false;	//flag if particle has area data. 
		public double areafraction;


		public Particle(float x, float y, 
				int frame, float area, int particleID){
			this.setX(x);
			this.setY(y);
			this.frame = frame;	
			this.setArea(area);
			this.particleID = particleID;
			this.next = new int[linkrange];
			
			this.hasArea = true;
		}

		//3D PARTICLE
		public Particle(float x, float y, float z,
				int frame, int particleID){
			this.setX(x);
			this.setY(y);
			this.setZ(z);			
			this.frame = frame;	
			this.particleID = particleID;
			this.next = new int[linkrange];
			
			this.hasArea = true;
		}
		
		//3D PARTICLE, with scaled coordinates
		public Particle(
				float x, float y, float z,
				float sx, float sy, float sz,
				int frame, int particleID){
			this.setX(x);
			this.setY(y);
			this.setZ(z);
			this.setSx(sx);
			this.setSy(sy);
			this.setSz(sz);
			this.frame = frame;	
			this.particleID = particleID;
			this.next = new int[linkrange];
			
			this.hasArea = true;
		}
		public int getFrame() {
			return this.frame;
		}


		/**
		 * @param x the x to set
		 */
		public void setX(float x) {
			this.x = x;
		}


		/**
		 * @return the x
		 */
		public float getX() {
			return x;
		}


		/**
		 * @param y the y to set
		 */
		public void setY(float y) {
			this.y = y;
		}


		/**
		 * @return the y
		 */
		public float getY() {
			return y;
		}


		/**
		 * @param z the z to set
		 */
		public void setZ(float z) {
			this.z = z;
		}


		/**
		 * @return the z
		 */
		public float getZ() {
			return z;
		}


		/**
		 * @return the sx
		 */
		public float getSx() {
			return sx;
		}

		/**
		 * @param sx the sx to set
		 */
		public void setSx(float sx) {
			this.sx = sx;
		}

		/**
		 * @return the sy
		 */
		public float getSy() {
			return sy;
		}

		/**
		 * @param sy the sy to set
		 */
		public void setSy(float sy) {
			this.sy = sy;
		}

		/**
		 * @return the sz
		 */
		public float getSz() {
			return sz;
		}

		/**
		 * @param sz the sz to set
		 */
		public void setSz(float sz) {
			this.sz = sz;
		}

		/**
		 * @param area the area to set
		 */
		public void setArea(float area) {
			this.area = area;
		}


		/**
		 * @return the area
		 */
		public float getArea() {
			return area;
		}

		public int getParticleID() {
			return particleID;
		}

		public void setParticleID(int particleID) {
			this.particleID = particleID;
		}

		public float getVolume() {
			return volume;
		}

		public void setVolume(float volume) {
			this.volume = volume;
		}

		public float getSvolume() {
			return svolume;
		}

		public void setSvolume(float svolume) {
			this.svolume = svolume;
		}

		public float getMeanint() {
			return meanint;
		}

		public void setMeanint(float meanint) {
			this.meanint = meanint;
		}

		public float getTotalint() {
			return totalint;
		}

		public void setTotalint(float totalint) {
			this.totalint = totalint;
		}
		
	}
	
	/**
	 * Very simplified version of ParticleTracker Trajectory class.
	 * @author miura
	 *
	 */
	public class Trajectory {

		Particle[] existing_particles;		// holds all particles of this trajetory in order
		int length; 						// number of frames this trajectory spans on

		ArrayList<int[]> gaps = new ArrayList<int[]>(); 	// holds arrays (int[]) of size 2 that holds  
		// 2 indexs of particles in the existing_particles.
		// These particles are the start and end points of a gap 
		// in this trajectory
		int num_of_gaps = 0;

		int serial_number;					// serial number of this trajectory (for report and display)
		boolean to_display = true;			// flag for display filter
		Color color;						// the display color of this Trajectory
		Roi mouse_selection_area;			// The Roi area where a mouse click will select this trajectory
		Roi focus_area;						// The Roi for focus display of this trajectory
		public double areafracMIN;
		public double areafracMAX;



		/**
		 * Constructor.
		 * <br>Constructs a Trajectory from the given <code>Particle</code> array.
		 * <br>Sets its length according to information of the first and last particles
		 * <br>Sets its <code>Color</code> to default (red) 
		 * @param particles the array containing all the particles defining this Trajectory
		 */
		public Trajectory(Particle[] particles) {

			this.existing_particles = particles;
			// the length is the last trajectory frame - the first frame (first frame can be 0) 
			this.length = this.existing_particles[this.existing_particles.length-1].getFrame() - 
			this.existing_particles[0].getFrame();
			color = Color.red; //default
		}
		/**
		 * Populates the <code>gaps</code> Vector with int arrays of size 2. 
		 * <br>Each array represents a gap, while the values in the array are the <b>indexs</b>
		 * of the particles that have a gap between them. 
		 * <br>The index is of the particles in the <code>existing_particles</code> array - 
		 * two sequential particles that are more then 1 frame apart give a GAP
		 */
		private void populateGaps() {

			for (int i = 0; i<existing_particles.length-1; i++){
				// if two sequential particles are more then 1 frame apart - GAP 
				if (existing_particles[i+1].getFrame() - existing_particles[i].getFrame() > 1) {
					int[] gap = {i, i+1};
					gaps.add(gap);
					num_of_gaps++;
				}
			}
		}
		private void drawStatic(Graphics g, ImageCanvas ic) {
			int i;
			g.setColor(this.color);
			for (i = 0; i<this.existing_particles.length-1; i++) {
				if (this.existing_particles[i+1].getFrame() - this.existing_particles[i].getFrame() > 1) {	    			   
					g.setColor(Color.red); //gap
				}
				g.drawLine(ic.screenXD(this.existing_particles[i].getY()), 
						ic.screenYD(this.existing_particles[i].getX()), 
						ic.screenXD(this.existing_particles[i+1].getY()), 
						ic.screenYD(this.existing_particles[i+1].getX()));

				g.setColor(this.color);							
			}
			//mark death of particle
			if((this.existing_particles[this.existing_particles.length-1].getFrame()) < frames_number - 1){
				g.fillOval(ic.screenXD(this.existing_particles[this.existing_particles.length-1].getY()), 
						ic.screenYD(this.existing_particles[this.existing_particles.length-1].getX()), 5, 5);
			}
		}
	}

	/**
	 * Defines an overlay Canvas for a given <code>ImagePlus</code> on which the non 
	 * filtered found trajectories are displayed for further displaying and analysis options
	 */
	private class TrajectoryCanvas extends ImageCanvas {

		private static final long serialVersionUID = 1L;

		/**
		 * Constructor.
		 * <br>Creates an instance of TrajectoryCanvas from a given <code>ImagePlus</code>
		 * and <code>ImageCanvas</code>
		 * <br>Displays the detected particles from the given <code>MyFrame</code>
		 * @param aimp
		 */
		private TrajectoryCanvas(ImagePlus aimp) {
			super(aimp);
		}

		/* (non-Javadoc)
		 * @see java.awt.Component#paint(java.awt.Graphics)
		 */
		public void paint(Graphics g) {            
			super.paint(g);
			drawTrajectories(g); 
		}

		/**
		 * Draws each of the trajectories in <code>all_traj</code>
		 * on this Canvas according to each trajectories <code>to_display</code> value
		 * @param g
		 * @see Trajectory#drawStatic(Graphics, ImageCanvas)
		 */
		private void drawTrajectories(Graphics g) {

			if (g == null) return;
			Iterator<Trajectory> iter = all_traj.iterator();  	   
			// Iterate over all the trajectories 
			while (iter.hasNext()) {
				Trajectory curr_traj = iter.next();	
				// if the trajectory to_display value is true
				if (curr_traj.to_display) {	   		   				   
					curr_traj.drawStatic(g, this);
				}
			}
		}
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

	/**
	 * The linking algorithm, modified version of Mosaic ParticleTracker plugin.
	 * Cost function (now an Interface) was changed, 
	 * so that distance and area changes are considered.
	 * Instead, no involvement of intensity in the cost function.
	 * 
	 * Key algorithm for linking is the  global optimization of the cost. 
	 * This is done by testing all g(i, j)=0 positions by calculating the 
	 * net increase/decrease in the cost (see 'z = ' equation, with 4 terms).
	 *  
	 * If net cost is negative, than this position temporarily becomes 1. 
	 * all the other g(i, j) position will be tested, that would end up in 
	 * the minimum global cost. 
	 * 
	 * Linking algorithm is well explained in 
	 * "A MATLAB toolbox for virus particle tracking"
	 * Sbalzarini (2006) p15.
	 * 
	 * Below is the description from the original:
	 * <br>Identifies points corresponding to the 
	 * same physical particle in subsequent frames and links the positions into trajectories
	 * <br>The length of the particles next array will be reset here according to the current linkrange
	 * <br>Adapted from Ingo Oppermann implementation
	 */	
	
	abstract void linkParticles(StackFrames[] frames, int frames_number, int linkrange, double displacement, LinkCosts link) 

	/**
	 * Generates <code>Trajectory</code> objects according to the infoamtion 
	 * avalible in each MyFrame and Particle. 
	 * <br>Populates the <code>all_traj</code> Vector.
	 */
	private void generateTrajectories(StackFrames[] frameA, int frames_number) {

		int i, j, k;
		int found, n, m;
		// Bank of colors from which the trjectories color will be selected
		//Color[] col={Color.blue,Color.green,Color.orange,Color.cyan,Color.magenta,Color.yellow,Color.white,Color.gray,Color.pink};
		Color[] col={Color.red};
		
		Trajectory curr_traj;
		// temporary vector to hold particles for current trajctory
		Vector<Particle> curr_traj_particles = new Vector<Particle>(frames_number);		
		// initialize trajectories vector
		all_traj = new Vector<Trajectory>();
		this.number_of_trajectories = 0;		

		for(i = 0; i < frames_number; i++) {
			for(j = 0; j < frameA[i].getParticles().size(); j++) {
				if(!frameA[i].getParticles().elementAt(j).special) {
					frameA[i].getParticles().elementAt(j).special = true;
					found = -1;
					// go over all particles that this particle (particles[j]) is linked to
					for(n = 0; n < this.linkrange; n++) {
						// if it is NOT a dummy particle - stop looking
						if(frameA[i].getParticles().elementAt(j).next[n] != -1) {
							found = n;
							break;
						}
					}
					// if this particle is not linked to any other
					// go to next particle and dont add a trajectory
					if(found == -1)
						continue;

					// Added by Guy Levy, 18.08.06 - A change form original implementation
					// if this particle is linkd to a "real" paritcle that was already linked
					// break the trajectory and start again from the next particle. dont add a trajectory
					if(frameA[i + n + 1].getParticles().elementAt(frameA[i].getParticles().elementAt(j).next[n]).special) 
						continue;

					// this particle is linked to another "real" particle that is not already linked
					// so we have a trajectory
					this.number_of_trajectories++;					
					curr_traj_particles.add(frameA[i].getParticles().elementAt(j));
					k = i;
					m = j;
					do {
						found = -1;
						for(n = 0; n < this.linkrange; n++) {
							if(frameA[k].getParticles().elementAt(m).next[n] != -1) {
								// If this particle is linked to a "real" particle that
								// that is NOT already linked, continue with building the trajectory
								if(frameA[k + n + 1].getParticles().elementAt(frameA[k].getParticles().elementAt(m).next[n]).special == false) {
									found = n;
									break;
									// Added by Guy Levy, 18.08.06 - A change form original implementation
									// If this particle is linked to a "real" particle that
									// that is already linked, stop building the trajectory
								} else {									
									break;
								}
							}
						}
						if(found == -1)
							break;
						m = frameA[k].getParticles().elementAt(m).next[found];
						k += (found + 1);
						curr_traj_particles.add(frameA[k].getParticles().elementAt(m));
						frameA[k].getParticles().elementAt(m).special = true;
					} while(m != -1);					

					// Create the current trajectory
					Particle[] curr_traj_particles_array = new Particle[curr_traj_particles.size()];
					curr_traj = new Trajectory((Particle[])curr_traj_particles.toArray(curr_traj_particles_array));

					// set current trajectory parameters
					curr_traj.serial_number = this.number_of_trajectories;
					curr_traj.color = col[this.number_of_trajectories% col.length];
					curr_traj.populateGaps();
					// add current trajectory to all_traj vactor
					all_traj.add(curr_traj);
					// clear temporary vector
					curr_traj_particles.removeAllElements();
				}				
			}
		}		
	}

	public void generateView() {		
		ImagePlus duplicated_imp;
		double magnification;
		TrajectoryCanvas tc;		
		String new_title = "All Trajectories Visual";		

		// if there is no image to generate the view on:
		// generate a new image by duplicating the original image
		Duplicator dup = new Duplicator();
		duplicated_imp= dup.run(this.imp);
		// Set magnification to the one of original_imp	
		magnification = this.imp.getWindow().getCanvas().getMagnification();

		// Create a new canvas based on the image - the canvas is the view
		// The trajectories are drawn on this canvas when it is constructed and not on the image
		// Canvas is an overlay window on top of the ImagePlus
		tc = new TrajectoryCanvas(duplicated_imp);

		// Create a new window to hold the image and canvas
		TrajectoryStackWindow tsw = new TrajectoryStackWindow(duplicated_imp, tc);

		// zoom the window until its magnification will reach the set magnification magnification
		while (tsw.getCanvas().getMagnification() < magnification) {
			tc.zoomIn(0,0);
		}		
	}
	
	public void printTrajectories(){
		Iterator<Trajectory> iter = all_traj.iterator();  	   
		// Iterate over all the trajectories
		// first get statistics
		int maxlength = 0; 
		while (iter.hasNext()) {
			Trajectory curr_traj = iter.next();	
			//IJ.log("Track"+curr_traj.serial_number);
			if (maxlength < curr_traj.existing_particles.length) maxlength = curr_traj.existing_particles.length;
		}
		int[] counter = new int[maxlength+1];
		 iter = all_traj.iterator(); 
		while (iter.hasNext()) {
			Trajectory curr_traj = iter.next();	
			//IJ.log("Track"+curr_traj.serial_number);
			counter[curr_traj.existing_particles.length] += 1;
		}
		 iter = all_traj.iterator(); 
/*		 
		while (iter.hasNext()) {
			Trajectory curr_traj = iter.next();	
			IJ.log("Track"+curr_traj.serial_number);
			Particle[] ptcls = curr_traj.existing_particles;
			for (int i = 0; i < ptcls.length; i++){
				IJ.log("    " + i + ": " + ptcls[i].x + ", " + ptcls[i].y);
			}
		}*/
		IJ.log("Max track length = " + maxlength);
		for (int i =1; i<counter.length; i++)
			IJ.log("track length " + i + ": " + counter[i]);
	}
	public void putLinkedParticeID(){
		ResultsTable rt = ResultsTable.getResultsTable();
		Particle ptcl, linkedptcl;
		int nextid;
		int ptclID;
		double value;
		for (int i = 0; i< rt.getColumn(rt.getColumnIndex("X")).length; i++){
			rt.incrementCounter();
			rt.addValue("LinkedID", 0);
		}
		rt.updateResults();
		for (int i = 0; i < frameA.length; i++){
			for(int j = 0; j < frameA[i].getParticles().size(); j++){
				ptcl = frameA[i].getParticles().elementAt(j);
				nextid = -1;
//				rt.setValue("LinkedID", ptcl.particleID , (ptcl.next[0] != -1) ? ptcl.next[0] : ptcl.next[1]);
				if ( ptcl.next[0] != -1){
					linkedptcl = frameA[i + 1].getParticles().elementAt(ptcl.next[0]);
					nextid = linkedptcl.particleID;
				
				} else {
					if ( ptcl.next[0] != -1) {
						linkedptcl = frameA[i + 2].getParticles().elementAt(ptcl.next[0]);
						nextid = linkedptcl.particleID;
					} 
				}
				ptclID = ptcl.particleID;
				value = (double) nextid;
				rt.setValue("LinkedID", ptclID , value);
				//rt.addValue("LinkedID", value);
				//IJ.log(Integer.toString(ptcl.particleID) + ":" + nextid);
			}
		}
		rt.updateResults();
		
	}
	
	private class TrajectoryStackWindow extends StackWindow {

		private static final long serialVersionUID = 1L;
		/**
		 * Constructor.
		 * <br>Creates an instance of TrajectoryStackWindow from a given <code>ImagePlus</code>
		 * and <code>ImageCanvas</code> 
		 * ---- following omitted
		 * and a creates GUI panel.
		 * <br>Adds this class as a <code>MouseListener</code> to the given <code>ImageCanvas</code>
		 * @param aimp
		 * @param icanvas
		 */
		private TrajectoryStackWindow(ImagePlus aimp, ImageCanvas icanvas) {
			super(aimp, icanvas);
		}
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
					rt.addValue("Xpos", ptcls[i].x);
					rt.addValue("Ypos", ptcls[i].y);
					rt.addValue("Area", ptcls[i].area);
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
		Particle[] ptcles = track.existing_particles;
		double area0 = (double) ptcles[0].area;
		double carea;
		int counter = 0;
		Particle p;
		double minimum = 1000;
		double maximum = 0;
		for (int i = 0; i < ptcles.length; i++) {
			p = ptcles[i];
			carea = (double) p.area;
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

	
	/**
	 * clone of the method in {@link FeaturePointDetector} of ParticletTracker
	 * 
	 * Returns a * c + b
	 * @param a: y-coordinate
	 * @param b: x-coordinate
	 * @param c: width
	 * @return
	 * 
	 */
	private int coord (int a, int b, int c) {
		return (((a) * (c)) + (b));
	}
	
}
