package emblcmci.linker;

import emblcmci.obj.Track;
import emblcmci.obj.Tracks;

/**
 * Visitor Pattern (abstract Visitor)
 * Element in the Modified Visitor pattern.
 * @author miura
 *
 */
public abstract class Analyzer {
	public abstract void analyze(Track t);
	public abstract void analyze(Tracks ts);
}
