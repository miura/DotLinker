from de.embl.cmci.seg import NucSegRitsukoProject as NSP
from ij import IJ
from ij import ImagePlus
imp = IJ.openImage("http://imagej.nih.gov/ij/images/blobs.gif");
#imp = IJ.openImage("http://imagej.nih.gov/ij/images/m51.zip");
imp = IJ.openImage('/Volumes/D/Julia20130201-/NucleusSegmentationStudy/20130320/L1CH2_maxp_300-374-6.tif')
nsp = NSP()
res = nsp.binarize(imp.getProcessor());
ImagePlus("result", res).show()

