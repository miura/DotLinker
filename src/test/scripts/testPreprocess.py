from emblcmci.seg import NucToDots
from ij import IJ

imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/l5c1_350_.tif'
imp = IJ.openImage(imgpath)
maximp = NucToDots(imp).run()
maximp.show()