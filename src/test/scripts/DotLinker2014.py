from de.embl.cmci.linker import DotLinker
from de.embl.cmci.io import ResultsTableLoader, DataWriterArea2D
from de.embl.cmci.linker.costfunctions import LinkCostswithAreaDynamics
from de.embl.cmci.linker.plotter import ViewDynamicsArea
from ij.measure import ResultsTable
from ij import IJ, Prefs
from ij.plugin.filter import ParticleAnalyzer as PA

'''
coding for constructing workflow from particle analysis results to color coded area. 
'''
print "test"
Prefs.blackBackground = True
# Costfunction for linking
lcAD = "de.embl.cmci.linker.costfunctions.LinkCostswithAreaDynamics"
# Loading method
loadmethod = 'de.embl.cmci.io.ResultsTableLoader'
# Path to the partcile analysis results
path = '/Users/miura/Dropbox/people/Giogia_Stefano/toJoe/coords.csv'
#Path to the binary image, to be plotted with area. 
#binimgpath = '/Users/miura/Dropbox/people/Giogia_Stefano/toJoe/mask10frames.tif'
binimgpath = '/Users/miura/Dropbox/people/Giogia_Stefano/toJoe/mask20framesOrg.tif'
#binimgpath = '/Users/miura/Dropbox/people/Giogia_Stefano/toJoe/mask_z4_furrowSeg1.0.6_threshOffest5e-04_closingSize3_200f.tif'

imp = IJ.openImage(binimgpath)

#paOpt = PA.CLEAR_WORKSHEET +\
paOpt =         PA.SHOW_OUTLINES +\
                PA.EXCLUDE_EDGE_PARTICLES# +\
                #PA.INCLUDE_HOLES #+ \
#       PA.SHOW_RESULTS 
measOpt = PA.AREA + PA.CENTROID + PA.SLICE# + PA.SHAPE_DESCRIPTORS + PA.INTEGRATED_DENSITY
rt = ResultsTable()
MINSIZE = 2
MAXSIZE = 10000
pa = PA(paOpt, measOpt, rt, MINSIZE, MAXSIZE)
pa.setHideOutputImage(True)
#pa.processStack = True
for i in range(imp.getStackSize()):
   imp.setSlice( i + 1)
   pa.analyze(imp)
#pa.getOutputImage().show()
rt.show("cells")

#rt = ResultsTable.open2(path)
dotlinker = DotLinker(loadmethod, rt) #better there is a constructor also with linkkost function object. 
dotlinker.setTrajectoryThreshold(5)
dotlinker.setShowTrackTable(False)
#dotlinker = DotLinker(loadmethod)
linkcostfunction = dotlinker.setLinkCostFunction(lcAD)
linkcostfunction.setParameters(5.0, 2.0)
rtout = dotlinker.doLinking(False)
rtout.show("Tracks")


vd = ViewDynamicsArea(imp)
vd.plotAreaDynamics(rtout, vd.PLOTALL)
imp.show()
projimp = vd.projectStackHorizontally(imp)
vd.addAreaColorScale(projimp, rtout)
projimp.show()

