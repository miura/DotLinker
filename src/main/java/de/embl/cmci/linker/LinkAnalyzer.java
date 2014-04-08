package de.embl.cmci.linker;

import de.embl.cmci.obj.AbstractTrack;
import de.embl.cmci.obj.AbstractTracks;

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
