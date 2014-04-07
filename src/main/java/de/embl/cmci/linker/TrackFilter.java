package de.embl.cmci.linker;

import emblcmci.obj.AbstractTrack;
import emblcmci.obj.Tracks;

/**
 * go through tracks and take only long tracks.
 *  
 * 
 * @author miura
 *
 */
public class TrackFilter {
	
	public Tracks run(Tracks ts, int lengthThreshold){
		Tracks filtered = new Tracks();
		for (AbstractTrack t : ts.values()){
			if (t.getNodes().size() > lengthThreshold){
				filtered.addTrack(t.getTrackID(), t);
			}
		}
		return filtered;	
	}
	
}
