package emblcmci.linker.costfunctions;

import emblcmci.linker.AbstractDotLinker;
import emblcmci.linker.LinkCosts;
import emblcmci.linker.AbstractDotLinker.Particle;

/** an implementation of cost calculation only by distance between linked particle and area change. 
 *  cost = (2D or 3D distance)^2 + MaxSisplacement^2 * abs(ratio of area change);
 *  where 
 *  maxdisplacement is the user-defined maximum displacement
 *  ratio of area change = ((particle2 area) - (particle1 area)) / (particle1 area);
 *  if area suddenly becomes twice, then ratio is 1, which then the cost exceeds MaxDistance easily.
 *  this will also be the case if area suddenly becomes a half. 
 *  
 *  ... so the intention here is that linking should be done with cells with similar area. 
 *  during segmentation, watershed tends to divide cell into two artificially and want to avoid such
 *  to be included in the trajectory.
 *  
 *   see more details here:
 *   http://cmci.embl.de/blogtng/2011-09-01/cell_splitting_and_cost_function
 *   
 * @author Kota Miura
 *
 */
public class LinkCostswithAreaDynamics implements LinkCosts{

	double displacement;
	double areaweight;
	/**Constructor. 
	 * 
	 * @param displacement: user-defined displacement value
	 */
	public LinkCostswithAreaDynamics(double displacement, double areaweight){
		this.displacement = displacement;
		this.areaweight = areaweight;
	};
	@Override
	public double calccost(AbstractDotLinker.Particle p1, AbstractDotLinker.Particle p2) {
		double cost =	(p2.getX()-p1.getX())*(p2.getX()-p1.getX()) +
						(p2.getY()-p1.getY())*(p2.getY()-p1.getY()) +
						(p2.getZ()-p1.getZ())*(p2.getZ()-p1.getZ()) + 
						(displacement * displacement)* areaweight * Math.abs(1 - p2.getArea()/p1.getArea());
		// TODO Auto-generated method stub
		return cost;
	}

	
}

