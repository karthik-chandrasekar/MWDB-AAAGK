import similarity

class DTW(similarity.Similarity) :

    def __init__(self) :

       pass

    def refresh(self, simFile1, simFile2) :

       print "#### Checking similarity of", simFile1, " and ", simFile2
       similarity.Similarity.__init__(self, simFile1, simFile2) 

    def similarity(self) : 

       numOfStates = len(self._stateF1Dict)
       # Doing 1 + sum of Euc Avg for each state
       denom = 0
       for state in self._stateF1Dict: 
           denom = denom + self.calcDTW(self._stateF1Dict[state], self._stateF2Dict[state])

       denom = denom / numOfStates
       denom = denom + 1
       return 1.0/denom
   
    """
    Initializes a 2 dimensional matrix of size N.
    """ 
    def init2D(self, mat, N, initVal) :
       
       mat = [0] * N
       for x in range(N) :
           mat[x] = [0] * N
           for y in range(N) :
               mat[x][y] = initVal
       return mat

    def calcDTW(self, dict1, dict2) :
       
       N = len(dict1) # Assert equal to dict2 in our case.
       M = len(dict2) 

       distMat = self.getDistMat(N, dict1, dict2)
       dtwMat = []
       dtwMat = self.init2D(dtwMat, N, float('inf'))
   
       dtwMat[0][0] = 0
       for x in range(N)[1:] :
           for y in range(N)[1:] :
               cost = distMat[x][y]
               dtwMat[x][y] = cost + min(dtwMat[x-1][y], dtwMat[x][y-1], dtwMat[x-1][y-1])

       return dtwMat[N-1][N-1]
 
    """
    Fill up if req for any logic comes 
    """
    def getIndex(self, time) :

        return (time - 1)

    def getDistMat(self, N, dict1, dict2) :
      
        distMat = []
        distMat = self.init2D(distMat, N, 0)

        for xTime in dict1 : 
           vector1 = dict1[xTime]
           for yTime in dict2 : 
               vector2 = dict2[yTime]     
               dist    = self.getEucledian(vector1, vector2)
               xIndex  = self.getIndex(xTime)
               yIndex  = self.getIndex(yTime)
            #   print "LOG : xIndex ", xIndex
             #  print "LOG : yIndex ", yIndex
               distMat[xIndex][yIndex] = dist
        return distMat
    
if __name__ == "__main__" :

    import sys
    
    if len(sys.argv) < 3 :
        print "Error Inputs not passsed to the script."
        sys.exit(1)

    print "#### Starting task 1 b."

    simFile1 = sys.argv[1]
    simFile2 = sys.argv[2]

    print "#### Checking similarity of", simFile1, " and ", simFile2
    dtw = DTW() 
    dtw.refresh(simFile1, simFile2)
    print dtw.similarity()
