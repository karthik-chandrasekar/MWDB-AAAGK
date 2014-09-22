import os

class AvgDiff(object) :

    def __init__(self, graphFileName, weight) :

        self._epidFile   = "epidemic_word_file"
        self._graphFile  = graphFileName
        self._outputAvg  = "epidemic_word_file_avg"
        self._outputDiff = "epidemic_word_file_diff"
        self._alpha      = weight
   
        # Dictionary of idx to window. 
        self._stateDict = self.parseEpidemicFile()

        # Dictionary of states to its neigbours. 
        self._graphData = self.parseGraph()

    """
    Picks up values from the diff file and forms the dictionary out of it.
    """        
    def parseEpidemicFile(self) :
        epiHandle = open(self._epidFile)
        lines = epiHandle.readlines()
        epiData = {}

        for index, line in enumerate(lines) :
            key = line.split()[0][2:-2]
            epiData[key] = line.split()[1][1:-2]
        
        epiHandle.close()
        return epiData 

    """
    Currently parses the modified csv to get the work done. 
    Puts them in the dictionary of graphData. 
    """
    def parseGraph(self) :

        # Create the graphData dictionary.
        graphHandle = open(self._graphFile)
        lines  = graphHandle.readlines() 
        graphData = {} 
        # Splitting by , Accessing by index should give the
        # name of the neighbour. 
        stateIndex = lines[0].split(',')
        for state in stateIndex[1:] : 
            graphData[state] = list()

        for line in lines[1:] :
            neighData = line.split(',')
            stateName = neighData[0]
            for index, neigh in enumerate(line.split(',')[1:]) :
                if neigh == '1' :
                    graphData[stateName].append(stateIndex[index + 1])

        graphHandle.close()
        return graphData

    """
    Returns idx of all the neighbours in a list for the avg and diff 
    functions. 
    35.csv, US-AL, 0
    Useful for searching in the files. 
    """
    def getNeighbours(self, idx) : 
        # Look up at the graphData and return the idx of the neighbours in a list
        # given the current idx
        neighIdx = []
        fst = idx.split(',')
        filename = fst[0]
        state    = fst[1].split('-')[1]
        time     = fst[2]

        for neigh in self._graphData[state] :
            newIdx = filename + ",US-" + neigh + "," + time
            neighIdx.append(newIdx)

        return neighIdx

    def getDiffString(self, idx, neigh) :

        wini    = self._stateDict[idx].split(',')
        length  = len(wini)
        winDiff = [0] * length
        winSum  = [0] * length
        
        for id in neigh : 
           data = self._stateDict[id]   
           for index, value in enumerate(data.split(',')) :
               winSum[index] = winSum[index] + float(value)

        for index in range(length) : 
           winDiff[index] = (float(wini[index]) - (winSum[index]/length))/float(wini[index])

        stringDiff = ""
        for diff in winDiff : 
            stringDiff = stringDiff + str(diff) + ","

        return stringDiff[:-1]
         

    def getAvgString(self, idx, neigh) : 
     
        wini   = self._stateDict[idx].split(',')
        length = len(wini)
        winAvg = [0] * length
        winSum = [0] * length
        
        for id in neigh : 
           data = self._stateDict[id]   
           for index, value in enumerate(data.split(',')) :
               winSum[index] = winSum[index] + float(value)

        for index in range(length) : 
            try :
                winAvg[index] = ((1 - float(self._alpha)) * (winSum[index]/length))
                winAvg[index] = winAvg[index] + float(wini[index]) * float(self._alpha)
            except IndexError : 
                pass

        stringAvg = ""
        for avg in winAvg : 
            stringAvg = stringAvg + str(avg) + ","

        return stringAvg[:-1]

    """
    Gets the idx of the neighbours and dumps the average.
    """
    def dumpAvg(self) :
        # open file to write.
        outHandle = open(self._outputAvg, 'a')
        outLine = ""
        for idx in self._stateDict: 
            line = ""
            neigh = self.getNeighbours(idx) 
            avg = self.getAvgString(idx, neigh)
            line = line + "<<" + idx + ">, <" + avg +">>\n"
            outHandle.write(line)
        outHandle.close()

    """
    Dumps the diff.
    """
    def dumpDiff(self):
        # open file to write.

        outHandle = open(self._outputDiff, 'a')
        outLine = ""
        for idx in self._stateDict: 
            line = ""
            neigh = self.getNeighbours(idx) 
            avg = self.getDiffString(idx, neigh)
            line = line + "<<" + idx + ">, <" + avg +">>\n"
            outHandle.write(line)
        outHandle.close()

    def dumpObj(self) :

        print self._graphData
