package emblcmci.obj.converters;

import ij.IJ;
import ij.measure.ResultsTable;

import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Node;

/**
 * Abstract class for loads data from ImageJ Results Table and generates Tracks instance. 
 * 
 * @author Kota Miura
 *
 */
public abstract class AbstractResultsTableToTracks {
	
	protected ResultsTable trt;
	protected AbstractTracks tracks;

	public AbstractResultsTableToTracks(ResultsTable trt){
		this.trt = trt;
	}
	
	public void run(){
		//boolean Areadata_Exists = false;
		if (trt == null){
			IJ.error("no track data available");
			return;
		}
		int rowlength = trt.getColumn(0).length;
		if (checkHeaders() && checkHeaderLength(rowlength)){
			AbstractTracks tracks = createTracks();
			AbstractTrack track;
			Node node;		
			for (int i = 0; i < rowlength; i++){
				node = generateNode(i);
				if (tracks.get(node.getTrackID()) == null){
					//track =new Track(new ArrayList<Node>());
					track =createTrack();
					track.setTrackID(node.getTrackID());
					tracks.put(node.getTrackID(), track);
				} else
					track = tracks.get(node.getTrackID());
				track.getNodes().add(node);

			}
      for (AbstractTrack t : tracks.values())
        t.checkFrameList();
			this.tracks = tracks;
			calcTrackParameters();
			//return tracks;
		} else {
			IJ.log("Result table does not contain required header/s");
			return;
		}
	}
	
	public AbstractTracks getTracks(){
		return tracks;
	}
	abstract Node generateNode(Integer i);
	
	abstract AbstractTracks createTracks();
	
	abstract AbstractTrack createTrack();
	
	abstract boolean checkHeaders();
	
	abstract boolean checkHeaderLength(int rowlength);
	
	abstract void calcTrackParameters();
	
}
