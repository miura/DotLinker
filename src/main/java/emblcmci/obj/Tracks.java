package emblcmci.obj;

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

}
