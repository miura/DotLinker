package emblcmci.obj;

import ij.gui.Roi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import de.embl.cmci.linker.LinkAnalyzer;


public abstract class AbstractTracks implements ILinkAnalysisElement {

//	public  <V extends AbstractTrack> AbstractTracks addTrack(int ID, AbstractTrack V){
//		put(ID, V);
//		V.setTrackID(ID);
//		return this;
//	}
	HashMap<Integer, AbstractTrack> map = new HashMap<Integer, AbstractTrack>();
	
	
	public  void addTrack(Integer ID, AbstractTrack v){
		map.put(ID, v);
		v.setTrackID((java.lang.Integer) ID);
	}

	public abstract Collection<AbstractTrack> getTracks();
	
	public boolean removeTrack(int ID){
		map.remove(ID);
		return true;
	}

	public int getID(AbstractTrack t){
		return t.getTrackID();
	}
	
	public AbstractTrack get(int id){
		return map.get(id);
	}
	
	public Collection<AbstractTrack> values(){
		return map.values();
	}
	
	public Set<Integer> keySet(){
		return map.keySet();
	}
	
	public void put(int id, AbstractTrack t){
		map.put(id, t);
	}
	
	public Integer size(){
		return map.size();
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

	public Iterator<AbstractTrack> iterator() {
		// TODO Auto-generated method stub
		return map.values().iterator();
	}
	
	public int getLastNodeID(){
		int thelastID = 0;
		for (AbstractTrack t : values()){
			ArrayList<Node> nodes = t.getNodes();
			for (Node n : nodes){
				if (n.getId() > thelastID)
					thelastID = n.getId();
			}
		}
		return thelastID;
	}

}
