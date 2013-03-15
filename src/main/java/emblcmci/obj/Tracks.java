package emblcmci.obj;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import emblcmci.linker.Analyzer;

/**
 * A data structure containing all tracks
 * @author miura
 *
 */

public class Tracks implements IBioObj{
	HashMap<Integer, Track> tracks = new HashMap<Integer, Track>();
	
	public void setTracks(HashMap<Integer, Track> tracks){
		this.tracks = tracks;
	}
	
	public Tracks addTrack(int ID, Track t){
		tracks.put(ID, t);
		t.setTrackID(ID);
		return this;
	}
	/** 
	 * visitor acceptance (now for analyzer as a visitor)
	 * ... this will be the entry point for analysis of Tracks. 
	 */
	@Override
	public void accept(Analyzer analyzer) {
		analyzer.analyze(this);
	}
	
	public Iterator<Track> iterator(){
		return tracks.values().iterator();
	}

	public Collection<Integer> keys(){
		return tracks.keySet();
	}
	
	public Collection<Track> values(){
		return tracks.values();
	}

	public Track get(int trackID) {
		// TODO Auto-generated method stub
		return tracks.get(trackID);
	}
	public int getID(Track t){
		return t.getTrackID();
	}

	public void put(int trackID, Track track) {
		tracks.put(trackID, track);
	}
}
