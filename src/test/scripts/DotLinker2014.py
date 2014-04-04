from de.embl.cmci.linker import DotLinker
from de.embl.cmci.io import ResultsTableLoader, DataWriterArea2D
from de.embl.cmci.linker.costfunctions import LinkCostswithAreaDynamics
from ij.measure import ResultsTable

print "test"
lcAD = "de.embl.cmci.linker.costfunctions.LinkCostswithAreaDynamics"
loadmethod = 'de.embl.cmci.io.ResultsTableLoader'
path = '/Users/miura/Dropbox/people/Giogia_Stefano/toJoe/coords.csv'

rt = ResultsTable.open2(path)
dotlinker = DotLinker(loadmethod, rt)
dotlinker.setTrajectoryThreshold(5)
dotlinker.setShowTrackTable(False)
#dotlinker = DotLinker(loadmethod)
linkcostfunction = dotlinker.setLinkCostFunction(lcAD)
linkcostfunction.setParameters(5.0, 2.0)
rtout = dotlinker.doLinking(False)
rtout.show("Tracks")