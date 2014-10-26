"""
Parses one line epidemic Simulation File 
and returns the contents split into objects. 
"""
class SimParser(object) :

    def __init__(self, header, lines) :

       states     = header.split(',')[2:]
       self._stateDict = {}
       mapper = [0] * len(states)

       for index, state in enumerate(states) :
           self._stateDict[state] = {} 
           mapper[index] = state

       for line in lines :
           dataList = line.split(',')[2:]
           time     = int(line.split(',')[0])
           for index, data in enumerate(dataList) :
               self._stateDict[mapper[index]][time] = [float(data)]

    def getDict(self) :
   
        return self._stateDict    

    def __repr__(self) :
 
        return "Number Of States " + str(len(self._stateDict)) + "\n" \
               + "State Dict " + str(self._stateDict)
        

if __name__ == "__main__" :

     header = "iteration,time,US-AK,US-AL,US-AR"
     data = []
     data.append("1,2012-01-01 12:00:00,1.0,2.0,3.0")
     data.append("2,2012-01-01 12:00:00,5.0,6.0,7.0")
     parser = SimParser(header, data)
     print parser
