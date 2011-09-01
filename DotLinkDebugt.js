//dotlinker Debugger
// to be called from Eclipse
// 20110831
IJ.open("C:\\dropbox\\My Dropbox\\tenFrameResults.csv");
//IJ.open("C:\\dropbox\\My Dropbox\\150framesResults.csv");
imp = IJ.openImage("C:\\dropbox\\My Dropbox\\bin10frames1_10.tif");
//imp = IJ.openImage("Z:\\Tina\\110608wt2.tif.proc.tif.bin1_150.tif");
imp.show();
IJ.run(imp, "Dot_Linker GUI", "");
//IJ.run(imp, "Dot_Linker no gui", "");
