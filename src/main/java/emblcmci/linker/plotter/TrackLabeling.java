package emblcmci.linker.plotter;

import ij.ImagePlus;
import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Track;
import emblcmci.obj.Tracks;

public class TrackLabeling {
	private AbstractTracks<Tracks> tracks;
	private ImagePlus imp;

	/**
	 * @param imp		target ImagePlus object to plot labels. 
	 * @param tracks
	 */
	public TrackLabeling(ImagePlus imp, Tracks tracks) {
		super();
		this.tracks = tracks;
		this.imp = imp;
	}
	
	public void dolable(){
		for (Track track : tracks.getTracks()){
			
		}
	}
	
	
}
