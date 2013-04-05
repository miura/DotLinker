package emblcmci.linker;

import java.util.ArrayList;

import emblcmci.obj.AbstractTrack;
import emblcmci.obj.Track;
import emblcmci.obj.Tracks;

/**
 * Fills interpolated nodes in the gaps in tracks. 
 * Captures ImageProcessor of the correspondinng position as well. 
 * 
 * @author miura
 *
 */
public class TrackFiller {

	public void run(Tracks ts){
		for (AbstractTrack t : ts.values()){
			t.checkFrameList();
			ArrayList<Integer> framelist = t.getFramelist();
			int i = framelist.get(0);
			ArrayList<Integer> gapindex = new ArrayList<Integer>();
			for (Integer frame : framelist){
				if (frame != i){
					gapindex.add(i);
					i++;
				} 
				i++;
			}
		}
	}
}
