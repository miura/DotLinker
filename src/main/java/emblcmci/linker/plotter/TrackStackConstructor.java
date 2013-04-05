package emblcmci.linker.plotter;

import java.util.ArrayList;

import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;
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
		ArrayList<Node> nodes = t.getNodes();
		ImageStack stack = new ImageStack(
				nodes.get(0).getOrgip().getWidth(),
				nodes.get(0).getOrgip().getHeight());
		for (Node n : nodes)
			stack.addSlice(Integer.toString(n.getFrame()), n.getBinip());
		
		return (new ImagePlus("trackBIN"+ Integer.toString(trackid), stack ));
	}
	public ImagePlus createSubImageStack(AbstractTracks ts, int trackid){
		AbstractTrack t = ts.get(trackid);
		ArrayList<Node> nodes = t.getNodes();
		ImageStack stack = new ImageStack(
				nodes.get(0).getOrgip().getWidth(),
				nodes.get(0).getOrgip().getHeight());
		for (Node n : nodes)
			stack.addSlice(Integer.toString(n.getFrame()), n.getOrgip());
		
		return (new ImagePlus("trackIP"+ Integer.toString(trackid), stack ));
	}
}
