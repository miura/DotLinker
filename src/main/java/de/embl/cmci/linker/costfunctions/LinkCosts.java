/**
 * 
 */
package de.embl.cmci.linker.costfunctions;

import de.embl.cmci.linker.DotLinker.Particle;

/**
 * Interface for Calculating costs between a particle and another particle in successive frame. 
 * @author Kota Miura
 *
 */
public interface LinkCosts {

	/**This method should be implemented for calculating cost for linking.  
	 * 
	 * @param p1 a paticle in (n)th frame
	 * @param p2 a particle in (n+1)th frame
	 * @return
	 */
	public abstract double calccost(Particle p1, Particle p2);

}
