
import emblcmci.DotLinker;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

/**
 * This is a template for a plugin that does not require one image
 * (be it that it does not require any, or that it lets the user
 * choose more than one image in a dialog).
 */
public class Dot_Linker implements PlugIn {
	/**
	 *
	 */
	// parameters for calculating links
	int linkrange = 2;
	double displacement = 2.0;
	
	public void run(String arg) {
		ImagePlus imp = IJ.getImage();
		DotLinker dl;
		if (arg.equals("gui")){
			if (!getParameterDialog()){
				return;
			} 
			dl = new DotLinker(imp, linkrange, displacement);
		} else {
			dl = new DotLinker(imp);			
		}
		dl.doLinking();
	}
	
	public boolean getParameterDialog(){
		GenericDialog gd = new GenericDialog("Dot Linker...", IJ.getInstance());
		gd.addMessage("Linking Parameters:\n");
		gd.addNumericField("Link Range", 2, 0);
		gd.addNumericField("Displacement", 10.0, 2); 
		gd.showDialog();

		this.linkrange = (int)gd.getNextNumber();
		this.displacement = gd.getNextNumber();
		
		return true;
	}
}