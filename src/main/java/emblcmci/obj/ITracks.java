package emblcmci.obj;

import java.util.Collection;

public interface ITracks extends IBioObj {
	
	public Collection<Track> values();

	public Track get(int trackID);

}
