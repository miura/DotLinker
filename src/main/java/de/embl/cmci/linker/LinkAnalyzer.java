package de.embl.cmci.linker;

import emblcmci.obj.AbstractTrack;
import emblcmci.obj.AbstractTracks;

/**
 * Visitor Pattern (abstract Visitor)
 * Element in the Modified Visitor pattern.
 * @author miura
 * 20130318
 *
 */
public abstract class LinkAnalyzer {
	public abstract void analyze(AbstractTrack track);
	public abstract void analyze(AbstractTracks ts);
}
