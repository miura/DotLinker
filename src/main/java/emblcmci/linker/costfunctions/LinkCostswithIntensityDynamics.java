package emblcmci.linker.costfunctions;
/**
 * For 3D tracking, cost calculation using volume changes (should be mostly constant)
 *  try with volumeweight 2 or so
 * 20111213
 * 20111214 changed, so that threshold is 20% +- 
 * @author Kota Miura
 */
import emblcmci.linker.AbstractDotLinker;
import emblcmci.linker.LinkCosts;
import emblcmci.linker.AbstractDotLinker.Particle;

public class LinkCostswithIntensityDynamics implements LinkCosts{

	double displacement;
	double intensityweight;
	/**Constructor. 
	 * 
	 * @param displacement: user-defined displacement value
	 */
	public LinkCostswithIntensityDynamics(double displacement, double intensitywight){
		this.displacement = displacement;
		this.intensityweight = intensitywight;
	};
	@Override
	public double calccost(AbstractDotLinker.Particle p1, AbstractDotLinker.Particle p2) {
		double cost =	(p2.getSx()-p1.getSx())*(p2.getSx()-p1.getSx()) +
						(p2.getSy()-p1.getSy())*(p2.getSy()-p1.getSy()) +
						(p2.getSz()-p1.getSz())*(p2.getSz()-p1.getSz()) ; 
						//(displacement * displacement)* intensitywight * Math.abs(1 - p2.getMeanint()/p1.getMeanint());

/*		
		double ratio = Math.abs(1 - p2.getMeanint()/p1.getMeanint());
		if (ratio > 0.3)
			//cost +=	 displacement * intensitywight * ;
			cost *=	 2;
*/		
		if ((p2.getMeanint() - p1.getMeanint()) != 0){
			cost = cost * (1 + (Math.abs(p2.getMeanint() - p1.getMeanint())/225)*intensityweight);
		}
		return cost;		
	}

	
}
