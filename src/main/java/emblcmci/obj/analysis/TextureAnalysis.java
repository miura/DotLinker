package emblcmci.obj.analysis;

import ij.process.ImageProcessor;

import java.util.ArrayList;

import emblcmci.glcm.GLCMtexture;
import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;
import emblcmci.obj.Tracks;

public class TextureAnalysis {

	public void getTextures(AbstractTracks tracks){
		GLCMtexture glcm = new GLCMtexture(1, 45, true, false);
		for (AbstractTrack t : tracks.values()){
			ArrayList<Node> nodes = t.getNodes();
			for (Node n : nodes){
				ImageProcessor ip8 = n.getOrgip().convertToByte(false);
				glcm.calcGLCM(ip8);
				n.setGlcmResults(glcm.getResultsArray());
			}
		}
	}
}
