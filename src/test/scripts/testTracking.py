from emblcmci.seg import NucToDots
from emblcmci.seg import NucSegRitsukoProject as NRP
from ij import IJ, ImageStack, ImagePlus
from emblcmci.linker import DotLinkerHeadless as DLH, TrackReLinker
from emblcmci.linker.costfunctions import LinkCostsOnlyDistance
from emblcmci.linker import ViewDynamics as VD
from emblcmci.obj.converters import VecTrajectoryToTracks
from emblcmci.seg import NucleusExtractor 
import jarray
'''
a test code for preprocessing nucleus image to derive maxima
and then get track
'''

imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/l5c1_350_CLAHE.tif'
#imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/L1CH2_maxp_300-374sampleframe.tif'
imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/l5c1_CLAHE.tif'
imp = IJ.openImage(imgpath)
ntd = NucToDots(imp);
#ntd.run()
ntd.runmain() # for imp that is already stackCLAHEed.
#for i in ntd.getXcoordA():
#        print i


print "Extracting Nucleus ..."
subwwhh = 110  # this must be guessed in the pre-run, by doing particle analysis and get the approximate sizes. 
en = NucleusExtractor(imp, ntd.getXcoordA(), ntd.getYcoordA(), ntd.getFrameA())
en.constructNodes(subwwhh)
print 'node length before filtering: ' + str(en.getNodes().size()) 
en.analyzeDotsandBinImages()
print 'node length after filtering: ' + str(en.getNodes().size()) 

nodes = en.getNodes()

'''
stk = ImageStack(subwwhh, subwwhh)
for n in nodes:
    binip = n.getBinip()
    stk.addSlice(binip)
ImagePlus("tt", stk).show()
'''

IJ.log('Linking ...')
dlh = DLH(imp, 3, 15) # linkrange, distance
#dlh.setData(ntd.getXcoordA(), ntd.getYcoordA(),  ntd.getFrameA())
dlh.setData(nodes) # a new way, 20130321
nearestneighbor = LinkCostsOnlyDistance()
dlh.doLinking(nearestneighbor, False)

# convert to Tracks object
vttt = VecTrajectoryToTracks()
vttt.run(dlh.getAll_traj())
tracks = vttt.getTracks()
print "tracks", str(tracks.size())
for t in tracks.values():
    print t.getTrackID(), t.getNodes().get(0).getX(), t.getNodes().size(), t.getFrameStart()

tracks.accept(TrackReLinker())

# plotting part
vd = VD(imp)
#img2path = '/Volumes/D/Julia20130201-/NucleusSegmentationStudy/20130312/out_bernsen45.tif'
#outimp = IJ.openImage(img2path)
#vd.plotTracks(outimp)


#vd.plotTracks(tracks, imp)
vd.trackAllPlotter(tracks, imp)
#vd.trackGapLinkPlotter(tracks, imp)
