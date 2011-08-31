
import emblcmci.DotLinker;
import ij.IJ;
import ij.ImagePlus;
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
	public void run(String arg) {
		ImagePlus imp = IJ.getImage();
		DotLinker dl = new DotLinker(imp);
		dl.doLinking();
	}
}