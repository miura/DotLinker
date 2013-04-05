package emblcmci.linker;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;

import java.util.ArrayList;

import emblcmci.obj.AbstractTrack;
import emblcmci.obj.Node;
import emblcmci.obj.Tracks;
import emblcmci.seg.NucSegRitsukoProject;

/**
 * Fills interpolated nodes in the gaps in tracks. 
 * Captures ImageProcessor of the correspondinng position as well. 
 * 
 * @author miura
 *
 */
public class TrackFiller {

	int size = 120;
	
	public void setRoiSize(int size){
		this.size = size;
	}
	
	/**
	 * examins each track and if there is any gap, fills with new nodes positioned at interpolated 
	 * coordinate between pre and post nodes of the track. 
	 * @param ts: tracks. 
	 * @param imp: original image stack. 
	 * @param roisize: roisize for extraciton of sub images. 
	 * @param wsthreshold: threhsold value for the watershed evaluation. 
	 */
	public void run(Tracks ts, ImagePlus imp, int roisize, double wsthreshold){
		IJ.log("re-filling the tracks...");
		int idcount = ts.getLastNodeID() + 1;
		setRoiSize(roisize);
		NucSegRitsukoProject nseg = new NucSegRitsukoProject();
		
		for (AbstractTrack t : ts.values()){
			t.checkFrameList();
			ArrayList<Integer> framelist = t.getFramelist();
			//int i = framelist.get(0);
			ArrayList<Integer> gapframe = new ArrayList<Integer>();	
			ArrayList<ArrayList<Node>> insertNodes = new ArrayList<ArrayList<Node>>();
			Node pre, post;
			for (Integer frame : framelist){
				Integer index = framelist.indexOf(frame);
				if (index < framelist.size() - 1){
					pre = t.getNodes().get(index);
					post = t.getNodes().get(index + 1);
					Integer framegap = post.getFrame() - pre.getFrame();
					if (framegap > 1){
						gapframe.add(frame);
						ArrayList<Node> insert = new ArrayList<Node>();
						for (int i = 0; i < framegap-1; i++){
							// linear interpolation of the positions
							double vx = ( (post.getX() - pre.getX()) / framegap ) * (i+1);
							double vy = ((post.getY() - pre.getY()) / framegap ) * (i+1);
							double nx = pre.getX() + vx;
							double ny = pre.getY() + vy;
							Node newnode = new Node(
									nx,
									ny,
									pre.getFrame() + i + 1, 
									t.getTrackID(),
									idcount++);
							//@TODO avoid going out from frame. 
							Roi nroi = new Roi(nx - size/2, ny - size/2, size, size);
							newnode.setOrgroi(nroi);
							newnode.setInterpoleted(true);
							nseg.loadImagesToNode(newnode, imp, wsthreshold);
							insert.add(newnode);		
						}
						insertNodes.add(insert);
					}
				}
				
			}
			if (gapframe.size() > 0){
				IJ.log("track"+ t.getTrackID());
				IJ.log(t.getFramelist().toString());
				IJ.log(gapframe.toString());
			}
			for ( Integer frame : gapframe){
				Integer index = framelist.indexOf(frame);
				Integer insertindex = index + 1;
				t.getNodes().addAll(insertindex, insertNodes.get(gapframe.indexOf(frame)));
			}
			t.checkFrameList();
			
			
//				Node pre = 
//			}
//			
		}
		
	}
	public void captureNodeImage(Node node){
		
	}
}
