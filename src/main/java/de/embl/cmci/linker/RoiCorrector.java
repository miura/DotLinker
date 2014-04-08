package de.embl.cmci.linker;

import java.util.ArrayList;

import de.embl.cmci.obj.AbstractTrack;
import de.embl.cmci.obj.AbstractTracks;
import de.embl.cmci.obj.Node;

import ij.ImagePlus;
import emblcmci.seg.NucSegRitsukoProject;

/**
 * Adjust the position of the Roi of each nodes, updates the ip and the binary image. 
 * @author Kota Miura
 * 20130405
 * 
 */
public class RoiCorrector {
	/**
	 * Goes through nodes within tracks and shift the position of ROIs associated with nodes
	 * according to the xy position of the node. 
	 * subimage and the binarized image associated with the node is then updated. 
	 * 
	 * @param ts: Tracks. 
	 * @param imp: original ImagePlus (image stack)
	 * @param wsthreshold: threshold value for watershed evaluation. 
	 */
	public void run(AbstractTracks ts, ImagePlus imp, double wsthreshold){
		for (AbstractTrack t : ts.values()){
			ArrayList<Node> nodes = t.getNodes();
			for (Node n : nodes){
				int roiw = n.getOrgroi().getBounds().width;
				int roih = n.getOrgroi().getBounds().height;
				int newroix = (int) n.getX() - roiw/2;
				int newroiy = (int) n.getY() - roih/2;				
				if (newroix < 0)
					newroix = 0;
				if ((newroix + roiw) > (imp.getWidth() -1))
					newroix = imp.getWidth() - roiw;
				if (newroiy < 0)
					newroiy = 0;
				if ((newroiy + roih) > (imp.getHeight() -1))
					newroiy = imp.getHeight() - roih;
				if ((newroix != n.getOrgroi().getBounds().x) || (newroiy != n.getOrgroi().getBounds().y)){
					n.getOrgroi().setLocation(newroix, newroiy);
					NucSegRitsukoProject nrp = new NucSegRitsukoProject();
					nrp.loadImagesToNode(n, imp, wsthreshold);
				}
			}
		}
			
	}
}
