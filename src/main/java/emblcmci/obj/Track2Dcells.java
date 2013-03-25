package emblcmci.obj;

import java.util.ArrayList;

/**
 * 2D epidermal cell track. Used for tracking Drosophiola development. 
 * (2011, For Tina)
 * the field values areaFracMax/min are the fractions of area compared to reference area. 
 * 
 * 
 * @author Kota Miura
 * refactored: 20130325
 */
public class Track2Dcells extends AbstractTrack {
	Double areafracMAX;
	Double areafracMIN;
	
	public Double getAreafracMAX() {
		return areafracMAX;
	}

	public void setAreafracMAX(Double areafracMAX) {
		this.areafracMAX = areafracMAX;
	}

	public Double getAreafracMIN() {
		return areafracMIN;
	}

	public void setAreafracMIN(Double areafracMIN) {
		this.areafracMIN = areafracMIN;
	}

	public Track2Dcells(ArrayList<Node> nodes) {
		super(nodes);
	}

}
