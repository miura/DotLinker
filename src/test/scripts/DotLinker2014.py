from de.embl.cmci.linker import DotLinker
from de.embl.cmci.linker.costfunctions import LinkCostswithAreaDynamics

print "test"
lcAD = "de.embl.cmci.linker.costfunctions.LinkCostswithAreaDynamics"
dotlinker = DotLinker('de.embl.cmci.io.ResultsTableLoader')
linkcostfunction = dotlinker.setLinkCostFunction(lcAD);
linkcostfunction.setParameters(10.0, 2.0);