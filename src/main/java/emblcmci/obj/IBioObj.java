package emblcmci.obj;

import emblcmci.linker.LinkAnalyzer;


/**
 * An interface for visitor pattern (Element)
 * @author miura
 *
 */
public interface IBioObj {
	public abstract void accept(LinkAnalyzer analyzer); 
}
