'''
An emergency protocol to extract key frames from the second channel.

'''
from util.opencsv import CSVReader
from java.io import FileReader
from java.util import HashMap
from ij.gui import Roi
from ij  import IJ, ImagePlus
import os

from ij import IJ, ImageStack, ImagePlus

## for classification
#ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l1/CH1_maxp.tif'
#ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l2/l2c2.tif'
#ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l3/l3c2.tif'
#ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l4/l4c2.tif'
ch2img = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l5/l5c2.tif'

#keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l1.csv'
#keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l2.csv'
#keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l3.csv'
#keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l4.csv'
keyfilepath = '/Users/miura/Dropbox/people/julia/NucSegmentStudy/divpoints/l5.csv'

#datapath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l1/CH2_maxp_track.csv'
#datapath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l2/l2c1_track.csv'
#datapath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l3/l3c1_track.csv'
#datapath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l4/l4c1_track.csv'
datapath = '/Volumes/D/Julia20130201-/almfscreen/samples_112712/l5/l5c1_track.csv'

def readKeyCSV(filepath):
   reader = CSVReader(FileReader(filepath), ",")
   ls = reader.readAll()
   keymap = HashMap()
   for item in ls:
         keymap.put(int(item[0]), int(item[1]))
         print item[0], item[1]
   return keymap

def readDataCSV(fp):
    reader = CSVReader(FileReader(fp), ",")
    ls = reader.readAll()
    tracks = HashMap()
    for idx,item in enumerate(ls):
        if idx > 0:
            if tracks.containsKey(int(item[0])):
                pass
            else:
                track = [[], [], [], []]
                tracks.put(int(item[0]), track)   # trackID in str. 
            track = tracks.get(int(item[0]))
            track[0].append(int(item[1])) #frame 0
            track[1].append(item[3]) #x 1
            track[2].append(item[4]) #y 2
            roi = Roi(int(item[5]), int(item[6]), int(item[7]), int(item[8]))
            track[3].append(roi) # a roi 3
    return tracks

def subimgSaver(stackimp, keyframe, trackA):
    for index, frame in enumerate(track[0]):    #loop over frames
        if frame == keyframe:
            print i, keyframe, track[3][index].toString()
            preframe = keyframe -10
            ip = c2imp.getStack().getProcessor(preframe)
            ip.setRoi(track[3][index])
            cip = ip.crop()
            ImagePlus(str(i), cip).show()            
            #binimg = node.getBinip()
            #orgimg = node.getOrgip()
            #IJ.saveAsTiff("", imagePlus)                       

keymap = readKeyCSV(keyfilepath)
tracks = readDataCSV(datapath)
c2imp = IJ.openImage(ch2img)
# for i in keymap.keySet():
#     track = tracks.get(i)
#     keyframe = keymap.get(i)
#     #print i, track[0]
#     for index, frame in enumerate(track[0]):    #loop over frames
#         if frame == keyframe:
#             print i, keyframe, track[3][index].toString()
#             preframe = keyframe -10
#             ip = c2imp.getStack().getProcessor(preframe)
#             ip.setRoi(track[3][index])
#             cip = ip.crop()
#             ImagePlus(str(i), cip).show()            
#             #binimg = node.getBinip()
#             #orgimg = node.getOrgip()
#             #IJ.saveAsTiff("", imagePlus)

# save all as a substack

filename = os.path.basename(ch2img)
filepath = os.path.dirname(ch2img)
fileprefix = os.path.splitext(filename)[0]

for i in keymap.keySet():
    track = tracks.get(i)
    keyframe = keymap.get(i)
    #print i, track[0]
    for index, frame in enumerate(track[0]):    #loop over frames
            #print i, keyframe, track[3][index].toString()
        
        ip = c2imp.getStack().getProcessor(frame)
        ip.setRoi(track[3][index])
        cip = ip.crop()
        if index == 0:
            stk = ImageStack(cip.getWidth(), cip.getHeight())
        stk.addSlice(str(frame), cip)
    outimp = ImagePlus(str(i), stk)
    outname =  fileprefix + 'ch2T' + str(i) + '.tif'
    outpath = os.path.join(filepath, outname)         
            #binimg = node.getBinip()
            #orgimg = node.getOrgip()
    IJ.saveAs(outimp, 'TIFF', outpath)            
