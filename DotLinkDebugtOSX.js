//dotlinker Debugger (osx path)
// to be called from Eclipse
// 20110831
IJ.open("/Users/miura/Dropbox/20framesResults.xls");
imp = IJ.openImage("/Users/miura/Dropbox/20frames.tif");
imp.show();
IJ.run(imp, "Dot_Linker GUI", "");
//IJ.run(imp, "Dot_Linker no gui", "");
