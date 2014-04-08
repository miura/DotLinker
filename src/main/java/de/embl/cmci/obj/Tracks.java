package de.embl.cmci.obj;

import java.util.Collection;

/**
 * A data structure containing all tracks
 * @author miura
 *
 */

public class Tracks extends AbstractTracks{

	public AbstractTracks addTrack(int ID, Track t){
		put(ID, t);
		t.setTrackID(ID);
		return this;
	}

	@Override
	public Collection<AbstractTrack> getTracks() {
		// TODO Auto-generated method stub
		return map.values();
	}

}
