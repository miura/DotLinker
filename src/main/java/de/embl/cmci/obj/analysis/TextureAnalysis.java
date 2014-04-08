package de.embl.cmci.obj.analysis;

import ij.IJ;
import ij.gui.Roi;
import ij.measure.ResultsTable;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import de.embl.cmci.obj.AbstractTrack;
import de.embl.cmci.obj.AbstractTracks;
import de.embl.cmci.obj.Node;

import emblcmci.glcm.GLCMtexture;

/**
 * Measures texture of Node, in the ROI defined as small rectangle surrounding 
 * Node's centroid. 
 *   
 * @author miura
 *
 */
public class TextureAnalysis {
	//int roisize = 30;
	ResultsTable rt = new ResultsTable();
	
	public void getTextures(AbstractTracks tracks, int roisize){
		int rx, ry;
		IJ.log("Texture analysis started...");
		GLCMtexture glcm = new GLCMtexture(1, 45, true, false);
		for (AbstractTrack t : tracks.values()){
			ArrayList<Node> nodes = t.getNodes();
			for (Node n : nodes){
				ImageProcessor ip8 = n.getOrgip().convertToByte(true);
				rx = (int) (n.getX() - n.getOrgroi().getBounds().x - roisize/2) ;
				ry = (int) (n.getY() - n.getOrgroi().getBounds().y - roisize/2);
				ip8.setRoi(rx, ry, roisize, roisize);
				glcm.calcGLCM(ip8);
				HashMap<?, ?> res = glcm.getResultsArray();
				n.setGlcmResults(res);
				//n.getOrgip().fill(new Roi(rx, ry, roisize, roisize));
				toResultsTable(n, (HashMap<String, Double>) res);
				//IJ.log("cor:" + res.get("Correlation"));
				//ip8.resetRoi();
			}
		}
	}
	void toResultsTable(Node n, HashMap<String, Double> res){
		rt.incrementCounter();
		rt.addValue("TrackID", n.getTrackID());
		rt.addValue("Frame", n.getFrame());
		for ( Entry<String, Double>  e : res.entrySet())
			rt.addValue(e.getKey(), e.getValue());
	}
	public void showResultsTable(){
		this.rt.show("GLCM");
	}
}
