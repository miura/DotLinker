package emblcmci.obj;

import java.util.Collection;
import java.util.HashMap;

import emblcmci.linker.LinkAnalyzer;

public class Tracks2Dcells extends AbstractTracks {
	HashMap<Integer, Track2Dcells> tracks = new HashMap<Integer, Track2Dcells>();	

	@Override
	public Collection<Track2Dcells> values(){
		return tracks.values();
	}

	@Override	
	public Track2Dcells get(int trackID) {
		// TODO Auto-generated method stub
		return (Track2Dcells) tracks.get(trackID);
	}
	
	/** 
	 * visitor acceptance (now for analyzer as a visitor)
	 * ... this will be the entry point for analysis of Tracks. 
	 */
	@Override
	public void accept(LinkAnalyzer analyzer) {
		analyzer.analyze(this);
	}
	
}
