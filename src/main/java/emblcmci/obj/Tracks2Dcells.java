package emblcmci.obj;

import java.util.Collection;

public class Tracks2Dcells extends Tracks {

	@Override
	public Collection<Track> values(){
		return tracks.values();
	}

	@Override	
	public Track get(int trackID) {
		// TODO Auto-generated method stub
		return tracks.get(trackID);
	}
	
}
