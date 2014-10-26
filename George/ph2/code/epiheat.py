import heatmap
import parser
import math

"""
Given a similarity measure find k - 1 similar files. 
"""
class EpiHeatMap(object) :

    def __init__(self, epiFile) :
 
        self._file = epiFile
        self._stateEpiDict = {}
        epiHandle = open(self._file)
        
        for line in epiHandle.readlines() :
            parsedLine = parser.Parser(line)
            self._stateEpiDict = self.addToDict(parsedLine, self._stateEpiDict)

        self._minIter = 0
        self._maxIter = 0
        self._maxVal  = 0
        self._window  = 0
        self._noOfEntries = 0
        self._noOfFiles  = self.getNoOfFiles()
        self.initHeat()
        self._noOfStates = len(self._stateEpiDict)
        self._heatPoints = self.formHeatPoints()
        hm = heatmap.HeatMap(self._heatPoints, self._maxVal, self._maxIter, self._noOfStates, self._window, [], [])
        hm.draw()

    def getNoOfFiles(self) : 
       count = 0
       firstState = self._stateEpiDict.itervalues().next()
       fileDict = {}
       for point in firstState : 
           if point.File not in fileDict :
               fileDict[point.File] = True
               count = count + 1
       return count 

    def formHeatPoints(self) :

       heatPoints = []    
       y = 0
       for stateName in self._stateEpiDict :
           y     = y + 1
           for point in self._stateEpiDict[stateName] :
               heatPoints.append([int(point.Time), y, point.Magnitude])
       return heatPoints
      
    def getMax(self) :
 
       for stateName in self._stateEpiDict :
           for point in self._stateEpiDict[stateName] :
               if self._maxVal < point.Magnitude :
                   self._maxVal = point.Magnitude
  
    def addToDict(self, parsedVal, epiDict) : 

       if parsedVal.State not in epiDict :
            epiDict[parsedVal.State] = []

#       if parsedVal.File not in fileDict[parsedVal.State] :
 #           fileDict[parsedVal.State][parsedVal.File] = []

       epiDict[parsedVal.State].append(parsedVal) 
       return epiDict     

    """
    Window is calculated by looking at the iterations of the first state in the list.
    """
    def initHeat(self) :
        firstState = self._stateEpiDict.itervalues().next()
        self._minIter = firstState[0].Time
        for data in firstState : 
            if float(data.Time) > self._maxIter :
                self._maxIter = float(data.Time)
            if float(data.Time) < self._minIter : 
                self._minIter = float(data.Time)
        
        multiplier = 1.0/self._noOfFiles
        self._window = math.ceil((self._maxIter - self._minIter)/(len(firstState) * multiplier))
        self._noOfEntries = len(firstState)
        self.getMax()
        
    def __repr__(self) :

       return "Window = " + str(self._window) + "\n" \
              "MaxIter (MaxX) = " + str(self._maxIter) + "\n" \
              "MinIter = " + str(self._minIter) + "\n" \
              "MaxVal = " + str(self._maxVal) + "\n" \
              "No Of State (MaxY) =" + str(self._noOfStates) + "\n" \
              "Window = " + str(self._window) + "\n" \
              "No Of Files =" + str(self._noOfFiles)
   
if __name__ == "__main__" :

    import sys
    import os
    if len(sys.argv) < 2 :
        print "Exiting insufficient arguments." 
        sys.exit(1)

    epiFile = os.path.abspath(sys.argv[1])    
    epiHeat = EpiHeatMap(epiFile) 
    print epiHeat
