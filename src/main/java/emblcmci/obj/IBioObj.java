package emblcmci.obj;

import emblcmci.linker.Analyzer;


/**
 * An interface for visitor pattern (Element)
 * @author miura
 *
 */
public interface IBioObj {
	public abstract void accept(Analyzer analyzer); 
}
