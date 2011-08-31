//dotlinker Debugger
// to be called from Eclipse
// 20110831
IJ.open("C:\\dropbox\\My Dropbox\\tenFrameResults.csv");
imp = IJ.openImage("C:\\dropbox\\My Dropbox\\bin10frames1_10.tif");
imp.show();
IJ.run(imp, "Dot_Linker GUI", "");
//IJ.run(imp, "Dot_Linker no gui", "");
