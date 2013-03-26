package emblcmci.obj;

import ij.gui.Roi;

import java.util.HashMap;
import java.util.Iterator;

import emblcmci.linker.LinkAnalyzer;

public abstract class AbstractTracks extends HashMap<Integer, AbstractTrack> implements IBioObj {

	public  <V extends AbstractTrack> AbstractTracks addTrack(int ID, AbstractTrack t){
		put(ID, t);
		t.setTrackID(ID);
		return this;
	}
	
	public boolean removeTrack(int ID){
		remove(ID);
		return true;
	}

	public int getID(AbstractTrack t){
		return t.getTrackID();
	}
	
	/** 
	 * visitor acceptance (now for analyzer as a visitor)
	 * ... this will be the entry point for analysis of Tracks. 
	 */
	@Override
	public void accept(LinkAnalyzer analyzer) {
		analyzer.analyze(this);
	}
	
	public int getTrackClosesttoPointROI(Roi pntroi){
		int closestTrackID = 1;
		if (pntroi.getType() != Roi.POINT)
			return closestTrackID;
		double rx = pntroi.getBounds().getCenterX();
		double ry = pntroi.getBounds().getCenterY();
		double mindist = 10000;
		double dist;
		for (AbstractTrack v : values()){
			dist = Math.sqrt(Math.pow((v.getNodes().get(0).getX() - rx), 2) + 	Math.pow((v.getNodes().get(0).getY() - ry), 2) );
			if (dist < mindist) {
				mindist = dist;
				closestTrackID = v.getNodes().get(0).getTrackID();
			}
		}	
		return closestTrackID;
	}

	public Iterator<AbstractTrack> iterator() {
		// TODO Auto-generated method stub
		return this.values().iterator();
	}	

}
