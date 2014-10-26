import parsecsv

"""
A general similarity measure has parsing of input files as the work to be
done. The similarity takes care of  that and converts it into dataStructues.
"""

class Similarity(object) :

   def __init__(self, simFile1, simFile2) :

       self._simFile1 = simFile1
       self._simFile2 = simFile2
       self._stateF1Dict = {} 
       self._stateF2Dict = {}
       f1Handle = open(self._simFile1)
       f2Handle = open(self._simFile2)
      
       f1Lines = f1Handle.readlines()
       f1Header = f1Lines[0]
       f1Parser = parsecsv.SimParser(f1Header, f1Lines[1:])

       f2Lines = f2Handle.readlines()
       f2Header = f2Lines[0]
       f2Parser = parsecsv.SimParser(f2Header, f2Lines[1:])
      
       self._stateF1Dict = f1Parser.getDict()       
       self._stateF2Dict = f2Parser.getDict()

       f1Handle.close()
       f2Handle.close()

   """ 
   Returns the Eucledian distance for two points given two vectors as
   input.
   ((x1-y1)^2) ...)^(1/2))
   """
   def getEucledian(self, vect1, vect2) :
          
      distSum = 0
      for index in range(len(vect1)) : 

         distSum = distSum + pow(vect1[index] - vect2[index], 2)
         
      return pow(distSum, 0.5) #Equalent to sqrt
