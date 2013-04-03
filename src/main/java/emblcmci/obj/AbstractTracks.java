package emblcmci.obj;

import ij.gui.Roi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import emblcmci.linker.LinkAnalyzer;

public abstract class AbstractTracks<Integer, V extends AbstractTrack> implements IBioObj {

//	public  <V extends AbstractTrack> AbstractTracks addTrack(int ID, AbstractTrack V){
//		put(ID, V);
//		V.setTrackID(ID);
//		return this;
//	}
	HashMap<Integer, V> map = new HashMap<Integer, V>();
	
	
	public  void addTrack(Integer ID, V v){
		map.put(ID, v);
		v.setTrackID((java.lang.Integer) ID);
	}

	public abstract <V> Collection<V> getTracks();
	
	public boolean removeTrack(int ID){
		map.remove(ID);
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
		for (AbstractTrack v : map.values()){
			dist = Math.sqrt(Math.pow((v.getNodes().get(0).getX() - rx), 2) + 	Math.pow((v.getNodes().get(0).getY() - ry), 2) );
			if (dist < mindist) {
				mindist = dist;
				closestTrackID = v.getNodes().get(0).getTrackID();
			}
		}	
		return closestTrackID;
	}

	public Iterator<V> iterator() {
		// TODO Auto-generated method stub
		return map.values().iterator();
	}	

}
