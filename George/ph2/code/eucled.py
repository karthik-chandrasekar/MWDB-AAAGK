import similarity
import sys

class Eucledian(similarity.Similarity) :

    def __init__(self) :
        pass 

    def refresh(self, simFile1, simFile2) :

        print "#### Checking similarity of", simFile1, " and ", simFile2
        similarity.Similarity.__init__(self, simFile1, simFile2)

    def setEpiSuffix(self, epiFolder, suffix) :
       pass       

    def similarity(self) : 

      numOfStates = len(self._stateF1Dict)
      # Doing 1 + sum of Euc Avg for each state
      denom = 0
      for state in self._stateF1Dict: 
          denom = denom + self.getEuc(state)

      denom = denom / numOfStates
      denom = denom + 1
      return 1.0/denom

    def getEuc(self, state) :

         parsedList1 = self._stateF1Dict[state]         
         parsedList2 = self._stateF2Dict[state]

         sumDist = 0
         length = len(parsedList1)
         # Could assert if both are same should be. 
         for time in parsedList1 :
             if time not in parsedList2 :
                 print "Error The data " + str(time) + "not found in" + self._simFile2 
             vector1 = parsedList1[time]
             vector2 = parsedList2[time]
             sumDist  = sumDist + self.getEucledian(vector1, vector2)

         return sumDist

    def __repr__(self) :

       return "This is the eucledian obj" + str(self._stateF1Dict)

if __name__ == "__main__" :

    if len(sys.argv) < 3 :
        print "Error Inputs not passsed to the script."
        sys.exit(1)

    print "#### Starting task 1 a."

    simFile1 = sys.argv[1]
    simFile2 = sys.argv[2]

    eucled = Eucledian() 
    eucled.refresh(simFile1, simFile2) 
    print eucled.similarity()
