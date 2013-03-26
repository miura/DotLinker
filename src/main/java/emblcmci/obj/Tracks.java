package emblcmci.obj;

import ij.gui.Roi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import emblcmci.linker.LinkAnalyzer;

/**
 * A data structure containing all tracks
 * @author miura
 *
 */

public class Tracks extends AbstractTracks{

	/** 
	 * visitor acceptance (now for analyzer as a visitor)
	 * ... this will be the entry point for analysis of Tracks. 
	 */
	@Override
	public void accept(LinkAnalyzer analyzer) {
		analyzer.analyze(this);
	}

	
	@Override
	public Collection<AbstractTrack> values(){
		return values();
	}

	public AbstractTrack get(int trackID) {
		// TODO Auto-generated method stub
		return get(trackID);
	}

//	public void put(int trackID, Track track) {
//		tracks.put(trackID, track);
//	}


}
