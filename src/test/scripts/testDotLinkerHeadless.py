from emblcmci.linker import DotLinkerHeadless as DLH
from ij import IJ, ImagePlus
from ij.plugin.filter import MaximumFinder
import jarray
from emblcmci.linker.costfunctions import LinkCostsOnlyDistance
from emblcmci.linker import ViewDynamics as VD

def maxFinder(ip):
  polygon = MaximumFinder().getMaxima(ip, 1.0, False)
  npnts = len(polygon.xpoints)
  return polygon.xpoints, polygon.ypoints
  
class Point:
    def __init__(self, pntid, x, y, frame):
        self.pntid = pntid
        self.x = x
        self.y = y
        self.frame = frame
        self.nexid = -1
        self.path = None
        self.inPath = 0
        
#imp = IJ.getImage()
imgpath = '/Volumes/D/Julia20130201-/NucleusSegmentationStudy/\
20130312/maxPnts_bernsen45.tif'
imp = IJ.openImage(imgpath)


framenum = imp.getStackSize()
frames = []
fullxA, fullyA, fulltA = [], [], []
for j in range(framenum):
    xpA, ypA = maxFinder(imp.getStack().getProcessor(j+1))
    fullxA.extend(xpA)
    fullyA.extend(ypA)
    for i in range(len(xpA)):
        # frame number starts from 1
        fulltA.append(j+1)

    # from here is just to test. 
    pointlist = []
    for i in range(len(xpA)):
        pointlist.append(Point(i, xpA[i], ypA[i], j))
        #pointlist.append(Nuc2D(xpA[i], ypA[i], j, i))
    if j < 4:
        print str(j), len(pointlist)
    #print pointlist[3].x
    frames.append(pointlist)

IJ.log('test ')
dlh = DLH(imp, 1, 10)
# becareful with the swapped X and Y axis
dlh.setData(jarray.array(fullxA, 'i'), jarray.array(fullyA, 'i'),  jarray.array(fulltA, 'i'))
nearestneighbor = LinkCostsOnlyDistance()
dlh.doLinking(nearestneighbor, False)
vd = VD(imp)
img2path = '/Volumes/D/Julia20130201-/NucleusSegmentationStudy/20130312/out_bernsen45.tif'
outimp = IJ.openImage(img2path)
vd.plotTracks(outimp)

