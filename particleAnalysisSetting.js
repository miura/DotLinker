imp = IJ.getImage();
IJ.run("Set Measurements...", "area mean standard centroid perimeter shape integrated stack display redirect=None decimal=5");
IJ.run(imp, "Analyze Particles...", "size=30-2100 circularity=0.30-1.00 show=Outlines display exclude clear include stack");
