import math
import heatmap

class Plotter(object) : 

    def __init__(self, csv, epiFile, graph, window) :

       self._epidemicSimFile = epiFile
       self._csv = csv
       self._graphFile = graph
       self._max = []
       self._min = []
       self._maxIterations = 0
       self._windowLength = int(window)
      
       self._minNeighbours = []
       self._maxNeighbours = []

       self._graphData = self.parseGraph()
       self._stateDict = self.parseEpidemicFile()
       self._maxCsv = 0

       self._numStates  = len(self._graphData)
       self._heatPoints = self.getHeatPoints() 
       self.update()

    """
    Returns the heatPoints to be used. 
    """
    def getHeatPoints(self) : 

        heatPoints = []
        csvHandle = open(self._csv) 
        lines = csvHandle.readlines()
        
        states = lines[0].split(',')[2:]
        
        for iteration, line in enumerate(lines[2:]) :
            for index, data in enumerate(line.split(',')[2:]) :
                y = self.getStateIndex(states[index].strip().split('-')[1])
                data = float(data.strip())
                if data > self._maxCsv : 
                    self._maxCsv = data
                heatPoints.append([iteration, y, data])
        return heatPoints
    
    """
    Returns the heat map.
    """
    def getStrength(self, valStr) :

        values = valStr.split(',')
        sum = 0
        for val in values :
           sum = sum + (float(val) * float(val))
        return math.sqrt(sum) 

    """
    Returns the neighbours of the graph points. 
    """
    def getNeighbours(self, idx) : 
        # Look up at the graphData and return the idx of the neighbours in a list
        # given the current idx
        fst = idx.split(',')
        state    = fst[1].split('-')[1]

        return self._graphData[state]

    """
    Updates the values of the class variables like min and max. 
    """
    def update(self) :

        minKey, minVal = self._stateDict.popitem()
        maxX = 0
        maxY = 0
        min    = self.getStrength(minVal)
        max    = self.getStrength(minVal)

        for key, vect in self._stateDict.iteritems() : 
            strength = self.getStrength(vect) 

            state = key.split(',')[1].split('-')[1]
            x = int(key.split(',')[2])
            y = self._graphData[state][0]

            if x > maxX : 
                maxX = x

            if strength <= min :
                
               min    = strength
               minKey = key

            elif strength >= max :

               max    = strength
               maxKey = key 
        
        self._minNeighbours = self.getNeighbours(minKey)
        self._maxNeighbours = self.getNeighbours(maxKey)
        minState = minKey.split(',')[1].split('-')[1]
        maxState = maxKey.split(',')[1].split('-')[1]        
        self._maxIterations = maxX 

        self._min = [int(minKey.split(',')[2]), int(self._graphData[minState][0]), min]
        self._max = [int(maxKey.split(',')[2]), int(self._graphData[maxState][0]), max]
        return

    """
    Currently parses the modified csv to get the work done. 
    Parses the csv to form the graph.
    """
    def parseGraph(self) :

        # Create the graphData dictionary.
        graphHandle = open(self._graphFile)
        lines  = graphHandle.readlines() 
        graphData = {} 
        # Splitting by , Accessing by index should give the
        # name of the neighbour. 
        stateIndex = lines[0].split(',')
        for index, state in enumerate(stateIndex[1:]) : 
            graphData[state] = list()
            graphData[state].append(index)

        for line in lines[1:] :
            neighData = line.split(',')
            stateName = neighData[0]
            for index, neigh in enumerate(line.split(',')[1:]) :
                if neigh == '1' :
                    graphData[stateName].append(stateIndex[index + 1])

        graphHandle.close()
        return graphData
 
    def parseEpidemicFile(self) :

        epiHandle = open(self._epidemicSimFile)
        lines = epiHandle.readlines()
        epiData = {}

        for index, line in enumerate(lines) :
            
            dirtyKey = line.split()[0][2:-2]
            key = dirtyKey.split(',')[0]
            # FIXME There is no need to save all the csv's
            # Only self._csvName Would do. Add as optimization. 
            if key == self._csv.split('/')[-1] :
                epiData[dirtyKey] = line.split()[1][1:-2]
        
        epiHandle.close()
        return epiData 

    """ 
    Return index given a name.
    """
    def getStateIndex(self, name) : 

       return self._graphData[name][0]
    
    """
    Returns name given an index.
    """
    def getStateName(self, index) :  
       
       for name, neigh in self._graphData.iteritems() :
           if neigh[0] == index :
               return name

    """
    Draws the heatmap using the data in the class. 
    """
    def draw(self) :

      markList = []
      highList = []
      highList.append([self._max[0], self._max[1]])
      highList.append([self._min[0], self._min[1]])
      maxName = self.getStateName(self._maxNeighbours[0])
      minName = self.getStateName(self._minNeighbours[0])

      maxMark = [maxName + "(Max)", 0, self._maxNeighbours[0], 'w']
      minMark = [minName + "(Min)", 0, self._minNeighbours[0], 'w']

      markList.append(minMark)
      markList.append(maxMark)

      for state in self._maxNeighbours[1:] : 
            index = self.getStateIndex(state)
            color = 'r'
            mark  = [state, 0, index, color] 
            markList.append(mark)
      
      for state in self._minNeighbours[1:] : 
            index = self.getStateIndex(state)
            color = 'g'
            mark  = [state, 0, index, color] 
            markList.append(mark)

      print "Mark List ", markList
      hm = heatmap.HeatMap(self._heatPoints, self._maxCsv, self._maxIterations, self._numStates, self._windowLength, markList, highList)
      hm.draw()

    """
    For debugging purposes.
    """
    def dump(self) :

        print "Length of graph =", len(self._graphData) 
        print "Length of state = ", len(self._stateDict) 
        print "Min =", self._min
        print "Max =", self._max
        print "Max Iterations =", self._maxIterations
        print "Min Neighbours", self._minNeighbours
        print "Max Neighbours", self._maxNeighbours
        print "Max Csv =", self._maxCsv
       # print "Heat Points", self._heatPoints

if __name__ == "__main__" :

    import sys

    ## The main starts here for the program. 

    def usage() :

       print "\nPlease pass csvName fileName and graphCsv and windowLength as an argument."

    if len(sys.argv) < 5 : 
        usage()	
        sys.exit(1)

    csvName  = sys.argv[1]
    fileName = sys.argv[2]
    graphCsv = sys.argv[3]
    windowLength = sys.argv[4]
    myPlot = Plotter(csvName, fileName, graphCsv, windowLength) 
    myPlot.dump()
    myPlot.draw()
