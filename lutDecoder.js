//retrieving the LUTarrays
//kota
LL = LutLoader();
IndexColorModel = LL.open('D:\\gitrepo\\DotLinker\\physics.lut');
LUT  = LookUpTable(LookUpTable(IndexColorModel));
r = LUT.getReds();
g = LUT.getGreens();
b = LUT.getBlues();
IJ.log(r.slice().valueOf());
IJ.log(g.slice().valueOf());
IJ.log(b.slice().valueOf());
