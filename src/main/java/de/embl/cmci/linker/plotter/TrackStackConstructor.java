package de.embl.cmci.linker.plotter;

import java.util.ArrayList;

import de.embl.cmci.obj.AbstractTrack;
import de.embl.cmci.obj.AbstractTracks;
import de.embl.cmci.obj.Node;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

/**
 * generates a sub image stack from track. 
 * @author miura
 *
 */
public class TrackStackConstructor {

	public ImagePlus createBinStack(AbstractTracks ts, int trackid){
		AbstractTrack t = ts.get(trackid);
		if (t != null){
			ArrayList<Node> nodes = t.getNodes();
			ImageStack stack = new ImageStack(
					nodes.get(0).getBinip().getWidth(),
					nodes.get(0).getBinip().getHeight());
			for (Node n : nodes){
//				IJ.log(n.getId() + ":" + 
//				n.getBinip().getWidth() + "," + n.getBinip().getHeight());
				stack.addSlice(Integer.toString(n.getFrame()), n.getBinip());
			}
			return (new ImagePlus("trackBIN"+ Integer.toString(trackid), stack ));
//			return null;
		} else {
			IJ.log("could not find track " + Integer.toString(trackid)); 
			return null;
		}
	}
	public ImagePlus createSubImageStack(AbstractTracks ts, int trackid){
		AbstractTrack t = ts.get(trackid);
		if (t != null){
			ArrayList<Node> nodes = t.getNodes();
			ImageStack stack = new ImageStack(
					nodes.get(0).getOrgip().getWidth(),
					nodes.get(0).getOrgip().getHeight());
			for (Node n : nodes)
				stack.addSlice(Integer.toString(n.getFrame()), n.getOrgip());

			return (new ImagePlus("trackIP"+ Integer.toString(trackid), stack ));
		} else {
			IJ.log("could not find track " + Integer.toString(trackid)); 
			return null;
		}
	}
}
