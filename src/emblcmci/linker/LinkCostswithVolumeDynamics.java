package emblcmci.linker;
/**
 * For 3D tracking, cost calculation using volume changes (should be mostly constant)
 *  try with volumeweight 2 or so
 * 20111213
 * @author Kota Miura
 */
import emblcmci.linker.DotLinker.Particle;

public class LinkCostswithVolumeDynamics implements LinkCosts{

	double displacement;
	double volumeweight;
	/**Constructor. 
	 * 
	 * @param displacement: user-defined displacement value
	 */
	public LinkCostswithVolumeDynamics(double displacement, double volumeweight){
		this.displacement = displacement;
		this.volumeweight = volumeweight;
	};
	@Override
	public double calccost(Particle p1, Particle p2) {
		double cost =	(p2.getSx()-p1.getSx())*(p2.getSx()-p1.getSz()) +
						(p2.getSy()-p1.getSy())*(p2.getSy()-p1.getSy()) +
						(p2.getSz()-p1.getSz())*(p2.getSz()-p1.getSz()) ; 
//						(displacement * displacement)* volumeweight * Math.abs((p1.getVolume() - p2.getVolume())/p1.getVolume());
		cost +=	 displacement * volumeweight * Math.abs(1 - p2.getVolume()/p1.getVolume());
		// TODO Auto-generated method stub
		return cost;
	}

	
}
