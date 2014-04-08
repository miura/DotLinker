package emblcmci.obj;

import de.embl.cmci.linker.LinkAnalyzer;


/**
 * An interface for visitor pattern (Element)
 * @author miura
 *
 */
public interface ILinkAnalysisElement {
	public abstract void accept(LinkAnalyzer analyzer); 
}
