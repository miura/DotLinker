package emblcmci.linker;

import emblcmci.obj.AbstractTracks;
import emblcmci.obj.Track;
import emblcmci.obj.Tracks;

/**
 * Visitor Pattern (abstract Visitor)
 * Element in the Modified Visitor pattern.
 * @author miura
 * 20130318
 *
 */
public abstract class LinkAnalyzer {
	public abstract void analyze(AbstractTracks abstractTracks);
	public abstract void analyze(Tracks ts);
}
