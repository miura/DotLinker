from ij import IJ, ImagePlus, Prefs
from emblcmci.seg import NucSegRitsukoProject
from ij.gui import Roi

#imp = IJ.getImage()
imp = IJ.openImage('/Users/miura/Dropbox/examples/blobsBInary.tif')
nrp = NucSegRitsukoProject();
#binimp = nrp.eliminateEdgeObjects(subip)
imp.show()
Prefs.blackBackground = True
binip = nrp.cleanEdge(imp)
binip.invertLut()
#ImagePlus("binimage", subip).show()
ImagePlus("binimage", binip).show()