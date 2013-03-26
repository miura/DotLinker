package emblcmci.obj;

public class Tracks2Dcells extends AbstractTracks{	
	
	public AbstractTracks addTrack(int ID, Track2Dcells t){
		put(ID, t);
		t.setTrackID(ID);
		return this;
	}
}
