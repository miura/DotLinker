package emblcmci.linker.plotter;

import java.awt.Color;
import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;
import emblcmci.obj.Tracks;

public class TrackLabeling {
	private AbstractTracks tracks;
	private ImagePlus imp;
	
	/**
	 * @param imp		target ImagePlus object to plot labels. 
	 * @param tracks
	 */
	public TrackLabeling(ImagePlus imp, Tracks tracks) {
		super();
		this.tracks = tracks;
		this.imp = imp;
	}
	public TrackLabeling() {
	}
	
	public void dolable(){
		for (AbstractTrack t : tracks.getTracks()){
			doAlable(this.imp, t);
		}
	}
	static public void doAlable(ImagePlus imp, AbstractTrack t){
		ArrayList<Node> ns;
		ImageProcessor ip;
		int cx, cy, cw, ch;		
		ns = t.getNodes();
		
		for (Node n : ns){
			if (n.getOrgroi() != null){
				// frame number starts from 1
				ip = imp.getStack().getProcessor(n.getFrame());
				ip.setColor(Color.WHITE);
				//ip.drawRoi(n.getOrgroi());
//				cx = n.getOrgroi().getBounds().x;
//				cy = n.getOrgroi().getBounds().y;
//				cw = n.getOrgroi().getBounds().width;
//				ch = n.getOrgroi().getBounds().height;
				cx = (int) n.getX();
				cy = (int) n.getY();

//				ip.drawString(Integer.toString(n.getTrackID()), cx + cw/2, cy + ch/2);				
				ip.drawString(Integer.toString(n.getTrackID()), cx, cy);
			} else {
				IJ.log(n.getId() + ".. no ROI ");
			}
		}		
	}
	
	
	
}
