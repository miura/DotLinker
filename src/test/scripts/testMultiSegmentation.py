from de.embl.cmci.seg import NucToDots
from ij import IJ
from de.embl.cmci.seg import NucSegRitsukoProject as NRP
from ij import ImageStack
from ij import ImagePlus
from de.embl.cmci.seg import NucleusExtractor 
'''
a test code for segmenting multiple nucleus based on positions detected by MaxFinder. 
'''

imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/l5c1_350_.tif'
#imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/L1CH2_maxp_300-374sampleframe.tif'
imp = IJ.openImage(imgpath)

#ntd = NucToDots(imp)
#ntd.stackCLAHE(imp)
#imp.show()
#ppimp =ntd.preprocess(imp) 
#ppimp.show()

#maximp = NucToDots(imp).run()
#maximp.show()

ntd = NucToDots(imp);
ntd.run()

subwwhh = 120 # size of sub image
# from here is the extraction
'''
nrp = NRP()
nrp.getPerNucleusBinImgProcessors(imp, subwwhh, ntd.getXcoordA(), ntd.getYcoordA(), ntd.getFrameA())
stk = nrp.getBinStack();
'''

en = NucleusExtractor(imp, ntd.getXcoordA(), ntd.getYcoordA(), ntd.getFrameA())
en.constructNodes(subwwhh)
en.analyzeDotsandBinImages()
nodes = en.getNodes()
stk = ImageStack(subwwhh, subwwhh)
for n in nodes:
    binip = n.getBinip()
    stk.addSlice(binip)

'''
stk = ImageStack(subwwhh, subwwhh)
for i, f in enumerate(ntd.getFrameA()):
    ip = imp.getStack().getProcessor(f)
    roi = nrp.makeRoi(ip, ntd.getXcoordA()[i], ntd.getYcoordA()[i], subwwhh, subwwhh)
    subip = nrp.extract(ip, roi)
    binip = nrp.binarize(subip)
    stk.addSlice(binip)
'''
ImagePlus("res", stk).show()

    
    
    

    

#NucSegRitsukoProject(ImagePlus imp, int[] xposA, int[] yposA, int roisize)