package de.embl.cmci.obj;

import java.util.Collection;

public class Tracks2Dcells extends AbstractTracks{	
	
	public AbstractTracks addTrack(int ID, Track2Dcells t){
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
