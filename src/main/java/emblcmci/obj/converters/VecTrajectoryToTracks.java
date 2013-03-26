package emblcmci.obj.converters;

import java.util.ArrayList;
import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;
import emblcmci.obj.Track;
import emblcmci.obj.Tracks;
/**
 * Converts "ParticleTracker" 2D trajectories to emblcmci.obj.Tracks
 * @author Kota Miura (miura@embl.de)
 *
 */
public class VecTrajectoryToTracks extends AbstractVecTrajectoryToTracks{
	
	public VecTrajectoryToTracks() {
		super();
	}

//	@Override
//	public AbstractTracks createTracks() {
//		return new Tracks();
//	}

	@Override
	public AbstractTrack createTrack() {
		return new Track(new ArrayList<Node>());
	}

	@Override
	public AbstractTracks getTracks() {
		return this.tracks;
	}

	@Override
	public void setTracks() {
		this.tracks = new Tracks();
	}

	
}
