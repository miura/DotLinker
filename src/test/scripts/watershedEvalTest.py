from emblcmci.seg import WaterShedEvaluation as WSE
from ij import IJ, ImagePlus

imgpath = "/Users/miura/Dropbox/people/julia/NucSegmentStudy/binimage.tif"
imp = IJ.openImage(imgpath)
wse = WSE(0.2)
ip = wse.test2WatershedFast(imp.getProcessor())
ImagePlus("tested", ip).show()