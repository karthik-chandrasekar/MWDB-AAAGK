"""
Input  - epidemic_word_file and graph_file 
Output - epidemic_word_file_avg & epidemic_word_file_diff
""" 

class AvgDiff(object) :

    def __init__(self, graphFileName, weight) :

        self.epidFile   = "epidemic_word_file"
        self.graphFile  = graphFileName
	
        self.outputAvg  = "epidemic_word_file_avg"
        self.outputDiff = "epidemic_word_file_diff"
        self.alpha      = weight
   
        # Dictionary of idx to window. 
        self._stateDict = self.parseEpidemicFile()

        # Dictionary of states to its neigbours. 
        self._graphData = self.parseGraph()
        

    def parseEpidemicFile(self) :
        pass

    def parseGraph(self) :
        pass

    """
    Returns idx of all the neighbours in a list for the avg and diff 
    functions. 
    """
    def getNeighbours(self, idx) : 
        # Look up at the graphData and return the idx of the neighbours in a list
        # given the current idx
        pass

    """
    Needs to get the neighbours out for each idx
    """
    def dumpAvg(self) :
        pass

    def dumpDiff(self):
        pass

## The main starts here for the program. 

def usage() :

    print "\nPlease graph file as an argument."

if len(sys.argv) < 2 : 
     usage()	
     sys.exit(1)

graphFile = sys.argv[1]
weight    = sys.argv[2]

avgDiff = AvgDiff(graphFile, weight) 
avgDiff.dumpAvg()
avgDiff.dumpDiff()
# for each file f in the directory do the processing.
