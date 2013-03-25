package emblcmci.obj;

import java.util.Collection;

public class Tracks2Dcells extends AbstractTracks {

	@Override
	public Collection<?> values(){
		return tracks.values();
	}

	@Override	
	public Track2Dcells get(int trackID) {
		// TODO Auto-generated method stub
		return (Track2Dcells) tracks.get(trackID);
	}
	
}
