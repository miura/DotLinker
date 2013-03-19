from emblcmci.seg import NucSegRitsukoProject as NSP
from ij import IJ

imp = IJ.openImage("http://imagej.nih.gov/ij/images/blobs.gif");
#imp = IJ.openImage("http://imagej.nih.gov/ij/images/m51.zip");

nsp = NSP()
res = nsp.binarize(imp);
res.show()

