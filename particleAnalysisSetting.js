/*
This processing is done after watershed, to get particle positions and areas. 
Kota
*/
imp = IJ.getImage();
IJ.run(imp, "Options...", "iterations=1 count=1 black edm=Overwrite do=Nothing");
IJ.run("Set Measurements...", "area mean standard centroid perimeter shape integrated stack display redirect=None decimal=5");
//IJ.run(imp, "Analyze Particles...", "size=30-2100 circularity=0.30-1.00 show=Outlines display exclude clear include stack");
op =	"size=30-2100 " +
		"circularity=0.30-1.00 " + 
		"show=Outlines " +
		"display exclude clear include stack";
IJ.run(imp, "Analyze Particles...", op);
