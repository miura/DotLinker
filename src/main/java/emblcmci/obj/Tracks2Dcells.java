package emblcmci.obj;

import java.util.Collection;
import java.util.HashMap;

import emblcmci.linker.LinkAnalyzer;

public class Tracks2Dcells extends AbstractTracks {	
	
	/** 
	 * visitor acceptance (now for analyzer as a visitor)
	 * ... this will be the entry point for analysis of Tracks. 
	 */
	@Override
	public void accept(LinkAnalyzer analyzer) {
		analyzer.analyze(this);
	}

	
}
