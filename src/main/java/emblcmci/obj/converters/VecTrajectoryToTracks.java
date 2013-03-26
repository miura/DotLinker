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

	@Override
	public AbstractTracks createTracks() {
		return new Tracks();
	}

	@Override
	public AbstractTrack createTrack(ArrayList<Node> nodes) {
		return new Track(nodes);
	}
	
}
