import heatmap
import parsecsv
import math

"""
Draws the Heat Map when a csv file is passed to it. 
"""
class SimHeatMap(object) :

    def __init__(self) :
        pass

    def refresh(self, epiFile) :
 
        self._file = epiFile
        self._stateEpiDict = {}
        epiLines = open(self._file).readlines()
        self._stateEpiDict = parsecsv.SimParser(epiLines[0], epiLines[1:]).getDict()
        self._minIter = 0
        self._maxIter = 0
        self._maxVal  = 0
        self._window  = 1
        self.initHeat()
        self._noOfStates = len(self._stateEpiDict)
        self._heatPoints = self.formHeatPoints()
        hm = heatmap.HeatMap(self._heatPoints, self._maxVal, self._maxIter, self._noOfStates, self._window, [], [])
        hm.draw()
   
    """
    Saves the heat points from the stored dictionary
    """
    def formHeatPoints(self) :

       heatPoints = []    
       y = 0
       for stateName in self._stateEpiDict :
           y     = y + 1
           for time in self._stateEpiDict[stateName] :
               #print "Time =" , time, " ", self._stateEpiDict[stateName][time][0]
               #print "X =", time, "Y= ", y, "Val =", self._stateEpiDict[stateName][time][0]
               heatPoints.append([int(time), y, self._stateEpiDict[stateName][time][0]])
       return heatPoints
     
    """
    Max is needed for the colormapper. 
    """
    def getMax(self) :
 
       for stateName in self._stateEpiDict :
           for time in self._stateEpiDict[stateName] :
               if self._maxVal < self._stateEpiDict[stateName][time][0] :
                   self._maxVal = self._stateEpiDict[stateName][time][0]
  
    def addToDict(self, parsedVal, fileDict) : 

       if parsedVal.State not in fileDict :
            fileDict[parsedVal.State] = {}

       if parsedVal.File not in fileDict[parsedVal.State] :
            fileDict[parsedVal.State][parsedVal.File] = []

       fileDict[parsedVal.State][parsedVal.File].append(parsedVal) 
       return fileDict     

    """
    Window is calculated by looking at the iterations of the first state in the list.
    """
    def initHeat(self) :
        firstState = self._stateEpiDict.itervalues().next()
        self._minIter = 0
        self._maxIter = len(firstState)
        self.getMax()
        
    def __repr__(self) :

       return "Window = " + str(self._window) + "\n" \
              "MaxIter = " + str(self._maxIter) + "\n" \
              "MinIter = " + str(self._minIter) + "\n" \
              "MaxVal = " + str(self._maxVal) + "\n" \
   
if __name__ == "__main__" :

    import sys
    import os
    if len(sys.argv) < 2 :
        print "Exiting insufficient arguments." 
        sys.exit(1)

    simFile = os.path.abspath(sys.argv[1])    
    simHeat = SimHeatMap() 
    simHeat.refresh(simFile)
    print simHeat
