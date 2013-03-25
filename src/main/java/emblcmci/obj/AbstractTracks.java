package emblcmci.obj;

import ij.gui.Roi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import emblcmci.linker.LinkAnalyzer;

public abstract class AbstractTracks implements IBioObj{
	HashMap<Integer, AbstractTrack> tracks = new HashMap<Integer, AbstractTrack>();
	
	public void setTracks(HashMap<Integer, AbstractTrack> tracks){
		this.tracks = tracks;
	}
	
	public AbstractTracks addTrack(int ID, Track t){
		tracks.put(ID, t);
		t.setTrackID(ID);
		return this;
	}
	
	public boolean removeTrack(int ID){
		tracks.remove(ID);
		return true;
	}
	
//	public boolean mergeTracks(int ID1, int ID2){
//		Integer maxid = Collections.max(tracks.keySet());
//		Integer mergedid = maxid + 1;
//		Track mergedTrack = tracks.get(ID1).mergeTracks(tracks.get(ID2));
//		this.tracks.put(mergedid, mergedTrack);
//		this.tracks.remove(ID1);
//		this.tracks.remove(ID2);
//		return true;
//	}

	public int getTrackClosesttoPointROI(Roi pntroi){
		int closestTrackID = 1;
		if (pntroi.getType() != Roi.POINT)
			return closestTrackID;
		double rx = pntroi.getBounds().getCenterX();
		double ry = pntroi.getBounds().getCenterY();
		double mindist = 10000;
		double dist;
		for (AbstractTrack v : this.tracks.values()){
			dist = Math.sqrt(Math.pow((v.getNodes().get(0).getX() - rx), 2) + 	Math.pow((v.getNodes().get(0).getY() - ry), 2) );
			if (dist < mindist) {
				mindist = dist;
				closestTrackID = v.getNodes().get(0).getTrackID();
			}
		}	
		return closestTrackID;
	}	
	
	/** 
	 * visitor acceptance (now for analyzer as a visitor)
	 * ... this will be the entry point for analysis of Tracks. 
	 */
	@Override
	public void accept(LinkAnalyzer analyzer) {
		analyzer.analyze(this);
	}
	
	public Iterator<AbstractTrack> iterator(){
		return tracks.values().iterator();
	}

	public Collection<Integer> keys(){
		return tracks.keySet();
	}
	

	abstract public Collection<AbstractTrack> values();

	abstract public AbstractTrack get(int trackID);
	
	public int getID(Track t){
		return t.getTrackID();
	}

	public void put(int trackID, AbstractTrack track) {
		tracks.put(trackID, track);
	}
}