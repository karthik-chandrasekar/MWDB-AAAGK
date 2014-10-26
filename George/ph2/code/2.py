import eucled, dtwdynamic, shellrunner
import simheat
import epiheat

import math

if __name__ == "__main__" :

    def sortSimData(simData) :
       for index, data in enumerate(simData) : 
           highest = data[0]
           highData = data
           for insideIndex in range(len(simData))[index:len(simData)] :
               if simData[insideIndex][0] > highest :
                   simData[index] = simData[insideIndex]
                   simData[insideIndex] = highData
                   highest = simData[index][0]
                   highData = simData[index]
       return simData
                
    indexDict = {"Name" : 0, "Message" : 1, "heat" : 2, "object" : 3 }
    methodDict = {"Eucledian": ["Eucledian", "Computes the eucledian to give the similarity", simheat.SimHeatMap(), eucled.Eucledian()],
                  "DTW" : ["DTW", "Computes the dtw between 2 simFiles",simheat.SimHeatMap(), dtwdynamic.DTW()],
                  "Hello" : ["Hello", "Hello Test For Java Runs", simheat.SimHeatMap(), shellrunner.ShellRunner("java -jar inputs/hello.jar")]
    }

    def metCodes() :

        metCode = "Name    Message\n\n"
        for key in methodDict :
            metCode = metCode + methodDict[key][indexDict["Name"]] + "   " + \
                       methodDict[key][indexDict["Message"]] + "\n"
        return metCode

    import sys
    import os

    if len(sys.argv) < 5 :
        print "Error Inputs not passsed to the script.\n" + \
          "Pass Arguments <epidemic file> <folder with simFiles> k methodName \n\n" + \
          "Method Codes\n"
        
        print metCodes()
        sys.exit(1)
    
    query     = sys.argv[1]
    simFolder = os.path.abspath(sys.argv[2])
    k         = sys.argv[3]
    method    = sys.argv[4]
    
    print "\n#### Starting the generation of similar heatmeaps for method " + method 

    methodObj = methodDict[method][indexDict["object"]]
    heatObj   = methodDict[method][indexDict["heat"]]

    simData = [] 
    for index, simFiles in enumerate(os.listdir(simFolder)) :
        fileToCheck = os.path.join(simFolder, simFiles)  
        methodObj.refresh(query, fileToCheck)
        toAppend = [methodObj.similarity(), simFiles, fileToCheck]
        simData.append(toAppend)

    print "\n Loading ", k , " most similar sim files to query", query
    simData = sortSimData(simData)
    for rank, data in enumerate(simData[0:int(k)]) :
        print "\n Loading File " + str(data[1]) + " Rank =" + str(rank + 1) + \
              " Similarity = " + str(simData[rank][0])
        heatObj.refresh(data[2])
    
