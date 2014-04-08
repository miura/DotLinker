from de.embl.cmci.linker import DotLinker
from de.embl.cmci.io import ResultsTableLoader, DataWriterArea2D
from de.embl.cmci.linker.costfunctions import LinkCostswithAreaDynamics
from de.embl.cmci.linker.plotter import ViewDynamicsArea
from ij.measure import ResultsTable
from ij import IJ

'''
coding for constructing workflow from particle analysis results to color coded area. 
'''
print "test"
# Costfunction for linking
lcAD = "de.embl.cmci.linker.costfunctions.LinkCostswithAreaDynamics"
# Loading method
loadmethod = 'de.embl.cmci.io.ResultsTableLoader'
# Path to the partcile analysis results
path = '/Users/miura/Dropbox/people/Giogia_Stefano/toJoe/coords.csv'
#Path to the binary image, to be plotted with area. 
binimgpath = '/Users/miura/Dropbox/people/Giogia_Stefano/toJoe/mask10frames.tif'

rt = ResultsTable.open2(path)
dotlinker = DotLinker(loadmethod, rt) #better there is a constructor also with linkkost function object. 
dotlinker.setTrajectoryThreshold(5)
dotlinker.setShowTrackTable(False)
#dotlinker = DotLinker(loadmethod)
linkcostfunction = dotlinker.setLinkCostFunction(lcAD)
linkcostfunction.setParameters(5.0, 2.0)
rtout = dotlinker.doLinking(False)
rtout.show("Tracks")

imp = IJ.openImage(binimgpath)
vd = ViewDynamicsArea(imp)
vd.plotAreaDynamics(rtout, vd.PLOTALL)
imp.show()