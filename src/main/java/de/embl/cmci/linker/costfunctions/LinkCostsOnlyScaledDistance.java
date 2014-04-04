package de.embl.cmci.linker.costfunctions;

import de.embl.cmci.linker.DotLinker;

	/** an implementation of cost calculation only by distance. The simplest form of "Nearest neighbor".
	 *  uses scaled coordinates for link cost calculation 
	 * @author Kota Miura
	 *
	 */
public class LinkCostsOnlyScaledDistance  implements LinkCosts {

		public double calccost(DotLinker.Particle p1, DotLinker.Particle p2) {
			double cost =	(p2.getSx()-p1.getSx())*(p2.getSx()-p1.getSx()) +
							(p2.getSy()-p1.getSy())*(p2.getSy()-p1.getSy()) +
							(p2.getSz()-p1.getSz())*(p2.getSz()-p1.getSz());
			// TODO Auto-generated method stub
			return cost;
		}

		@Override
		public void setParameters(double a1, double a2) {
			// TODO Auto-generated method stub
			
		}
		
	}

