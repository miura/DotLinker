from de.embl.cmci.seg import NucToDots
from de.embl.cmci.seg import NucSegRitsukoProject as NRP
from ij import IJ, ImageStack, ImagePlus
from emblcmci.linker import DotLinkerHeadless as DLH, TrackReLinker
from emblcmci.linker.costfunctions import LinkCostsOnlyDistance
from emblcmci.linker import ViewDynamics as VD
from de.embl.cmci.obj.converters import VecTrajectoryToTracks
from de.embl.cmci.seg import NucleusExtractor
from emblcmci.linker.plotter import TrackLabeling, TrackStackConstructor
from emblcmci.linker import TrackFilter, TrackFiller, RoiCorrector
from de.embl.cmci.obj.analysis import TextureAnalysis
import jarray
from util.opencsv import CSVReader
from java.io import FileReader
from java.util import HashMap
import csv
import os
'''
a test code for preprocessing nucleus image to derive maxima
and then get track
... mdofied for extracting positive class images and negative class images. 
'''

imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/l5c1_350_CLAHE.tif'
#imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/L1CH2_maxp_300-374sampleframe.tif'
#imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/l5c1_CLAHE.tif'
#imgpath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/l5c1_fastCLAHE.tif'
#imgpath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l1/CH2_maxp.tif'
#imgpath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l2/l2c1.tif'
#imgpath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l3/l3c1.tif'
#imgpath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l4/l4c1.tif'
imgpath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l5/l5c1.tif'

imp = IJ.openImage(imgpath)
ntd = NucToDots(imp);
ntd.run() # runs CLAHE first, takes a bit of time. 
#ntd.runmain() # for imp that is already stackCLAHEed.
#for i in ntd.getXcoordA():
#        print i


print "Extracting Nucleus ..."
subwwhh = 130  # this must be guessed in the pre-run, by doing particle analysis and get the approximate sizes. 
WATERSHED_THRESHOLD = 0.10;
en = NucleusExtractor(imp, ntd.getXcoordA(), ntd.getYcoordA(), ntd.getFrameA())
en.constructNodesByDots(subwwhh, WATERSHED_THRESHOLD)
print 'node length before filtering: ' + str(en.getNodes().size()) 
en.analyzeDotsandBinImages()
print 'node length after filtering: ' + str(en.getNodes().size()) 

nodes = en.getNodes()


'''
stk = ImageStack(subwwhh, subwwhh)
for n in nodes:
    binip = n.getBinip()
    stk.addSlice(binip)
ImagePlus("tt", stk).show()
'''

IJ.log('Linking ...')
dlh = DLH(imp, 3, 10) # linkrange, distance
#dlh.setData(ntd.getXcoordA(), ntd.getYcoordA(),  ntd.getFrameA())
dlh.setData(nodes) # a new way, 20130321
nearestneighbor = LinkCostsOnlyDistance()
dlh.doLinking(nearestneighbor, False)

# convert to Tracks object
vttt = VecTrajectoryToTracks()
#vttt.run(dlh.getAll_traj())
vttt.run(dlh.getAll_traj(), nodes)
tracks = vttt.getTracks()
print "tracks", str(tracks.size())

#for t in tracks.values():
    #print t.getTrackID(), t.getNodes().get(0).getX(), t.getNodes().size(), t.getFrameStart()
#    print t.getNodes().get(0).getOrgroi()

tracks.accept(TrackReLinker(10))

#tracks = TrackFilter().run(tracks, 70)

RoiCorrector().run(tracks, imp, WATERSHED_THRESHOLD)
TrackFiller().run(tracks, imp, subwwhh, WATERSHED_THRESHOLD)

#Corrrect node - associated trackiD
for track in tracks.getTracks():
    track.resetNodeTrackIDs()

# plotting part
vd = VD(imp)
#img2path = '/Volumes/D/Julia20130201-/NucleusSegmentationStudy/20130312/out_bernsen45.tif'
#outimp = IJ.openImage(img2path)
#vd.plotTracks(outimp)

#vd.plotTracks(tracks, imp)
vd.trackAllPlotter(tracks, imp)

#vd.trackGapLinkPlotter(tracks, imp)

'''
# 20130509 write tracks to a csv file
def tracksaver(imgpath, tracks):
    csvpath = os.path.splitext(imgpath)[0] + "_track.csv"
    f = open(csvpath, 'wb')
    writer = csv.writer(f)
    writer.writerow(['trackID', 'frame', 'nodeID', 'x', 'y','roix', 'roiy', 'roiwidth', 'roiheight'])
    for track in tracks.getTracks():
        nodes = track.getNodes()
        for n in nodes:
            templist = []
            templist.append(str(track.getTrackID()))
            templist.append(str(n.getFrame()))
            templist.append(str(n.getId()))
            templist.append(str(n.getX()))
            templist.append(str(n.getY()))
            croi = n.getOrgroi().getBounds()
            templist.append(str(croi.x))
            templist.append(str(croi.y))
            templist.append(str(croi.width))        
            templist.append(str(croi.height))
            writer.writerow(templist)
    f.close()
    print csvpath, '...done'

tracksaver(imgpath, tracks)


txa = TextureAnalysis()
roisize = 10
txa.getTextures(tracks, roisize)
txa.showResultsTable()

print 'Done all processings'
'''

## for classification
# ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l1/CH1_maxp.tif'
# ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l2/l2c2.tif'
# ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l3/l3c2.tif'
#ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l4/l4c2.tif'
# ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l5/l5c2.tif'

#keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l1.csv'
#keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l2.csv'
#keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l3.csv'
#keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l4.csv'
keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l5.csv'

def readKeyCSV(filepath):
   reader = CSVReader(FileReader(filepath), ",")
   ls = reader.readAll()
   keymap = HashMap()
   for item in ls:
         keymap.put(int(item[0]), int(item[1]))
         print item[0], item[1]
   return keymap

keymap = readKeyCSV(keyfilepath)
for i in keymap.keySet():
    track = tracks.get(i)
    nodes = track.getNodes()
    for index, node in enumerate(nodes):
        binip = node.getBinip()
        #orgimg = node.getOrgip()
        if index == 0:
            binstk = ImageStack(binip.getWidth(), binip.getHeight())
        binstk.addSlice(str(node.getFrame()), binip)
    binimp = ImagePlus(str(i), binstk)
    #binimp.show()
    binpath = imgpath + 't'+str(i)+'bin.tif'
    IJ.saveAs(binimp, 'TIFF', binpath)            


'''
impout1 = TrackStackConstructor().createBinStack(tracks, 4)
impout2 = TrackStackConstructor().createBinStack(tracks, 21)
if impout1 is not None:
    impout1.show()
if impout2 is not None:
    impout2.show()
'''