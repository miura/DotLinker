from de.embl.cmci.seg import NucToDots
from ij import IJ
from emblcmci.linker import DotLinkerHeadless as DLH, TrackReLinker
import jarray
from emblcmci.linker.costfunctions import LinkCostsOnlyDistance
from emblcmci.linker import ViewDynamics as VD
from de.embl.cmci.obj import VecTrajectoryToTracks
'''
a test code for preprocessing nucleus image to derive maxima
and then get track
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
for i in ntd.getXcoordA():
        print i
        
IJ.log('test ')
dlh = DLH(imp, 2, 15) # linkrange, distance
# becareful with the swapped X and Y axis
#dlh.setData(jarray.array(ntd.getXcoords(), 'i'), jarray.array(ntd.getYcoords(), 'i'),  jarray.array(ntd.getFrame(), 'i'))
dlh.setData(ntd.getXcoordA(), ntd.getYcoordA(),  ntd.getFrameA())
nearestneighbor = LinkCostsOnlyDistance()
dlh.doLinking(nearestneighbor, False)
# convert to Tracks object
tracks = VecTrajectoryToTracks().runsimple(dlh.getAll_traj())
tracks.accept(TrackReLinker())

# plotting part
vd = VD(imp)
#img2path = '/Volumes/D/Julia20130201-/NucleusSegmentationStudy/20130312/out_bernsen45.tif'
#outimp = IJ.openImage(img2path)
#vd.plotTracks(outimp)

#vd.plotTracks(tracks, imp)
vd.trackAllPlotter(tracks, imp)
#vd.trackGapLinkPlotter(tracks, imp)
