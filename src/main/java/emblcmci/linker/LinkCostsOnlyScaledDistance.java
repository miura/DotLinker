package emblcmci.linker;

import emblcmci.linker.AbstractDotLinker;

	/** an implementation of cost calculation only by distance. The simplest form of "Nearest neighbor".
	 *  uses scaled coordinates for link cost calculation 
	 * @author Kota Miura
	 *
	 */
public class LinkCostsOnlyScaledDistance  implements LinkCosts {

		public double calccost(AbstractDotLinker.Particle p1, AbstractDotLinker.Particle p2) {
			double cost =	(p2.getSx()-p1.getSx())*(p2.getSx()-p1.getSx()) +
							(p2.getSy()-p1.getSy())*(p2.getSy()-p1.getSy()) +
							(p2.getSz()-p1.getSz())*(p2.getSz()-p1.getSz());
			// TODO Auto-generated method stub
			return cost;
		}
		
	}

