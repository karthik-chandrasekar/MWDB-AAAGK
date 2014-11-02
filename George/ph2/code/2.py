import eucled, dtwdynamic, shellrunner
import simheat
import epiheat

import math
import sys
import os

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
                
    indexDict = {"Name" : 0, "Message" : 1, "heat" : 2, "object" : 3, "suffix" : 4}
    methodDict = {"Eucledian": ["Eucledian", "Computes the eucledian to give the similarity", simheat.SimHeatMap(), eucled.Eucledian(), ""],
                  "DTW" : ["DTW", "Computes the dtw between 2 simFiles",simheat.SimHeatMap(), dtwdynamic.DTW(), ""],
                  "Hello" : ["Hello", "Hello Test For Java Runs", simheat.SimHeatMap(), shellrunner.ShellRunner("java -jar input/hello.jar"), ""],
                  "Weighted" : ["Weighted", "Computes the weighted similarity ", simheat.SimHeatMap(), shellrunner.ShellRunner("java -jar input/karthik.jar input/LocationMatrix.csv"), "_word.txt"],
                  "WeightedDiff" : ["WeightedDiff", "Computes the weighted similarity of diff files", simheat.SimHeatMap(), shellrunner.ShellRunner("java -jar input/karthik.jar input/LocationMatrix.csv"), "_word.txt_diff"],
                  "WeightedAvg" : ["WeightedAvg", "Computes the weighted similarity of avg files", simheat.SimHeatMap(), shellrunner.ShellRunner("java -jar input/karthik.jar input/LocationMatrix.csv"), "_word.txt_avg"],
                  "Binary" : ["Binary", "Computes the similarity using binary vector", simheat.SimHeatMap(), shellrunner.ShellRunner("java -jar input/aneesh.jar"), "_word.txt"],
                  "BinaryAvg" : ["BinaryAvg", "Computes the similarity of Average Files using binary vector", simheat.SimHeatMap(), shellrunner.ShellRunner("java -jar input/aneesh.jar"), "_word.txt_avg"],
                  "BinaryDiff" : ["BinaryDiff", "Computes the weighted similarity of avg files", simheat.SimHeatMap(), shellrunner.ShellRunner("java -jar input/aneesh.jar"), "_word.txt_diff"],
    }

    def metCodes() :

        metCode = "Name    Message\n\n"
        for key in methodDict :
            metCode = metCode + methodDict[key][indexDict["Name"]] + "   " + \
                       methodDict[key][indexDict["Message"]] + "\n"
        return metCode

    if len(sys.argv) < 6 :
        print "Error Inputs not passsed to the script.\n" + \
          "Pass Arguments <query> <simFiles Folder> <epi Folder> k methodName \n\n" + \
          "Method Codes\n"
        
        print metCodes()
        sys.exit(1)
    
    query     = sys.argv[1]
    simFolder = os.path.abspath(sys.argv[2])
    epiFolder = os.path.abspath(sys.argv[3])
    k         = sys.argv[4]
    method    = sys.argv[5]
    
    print "\n#### Starting the generation of similar heatmeaps for method " + method 
    methodObj = methodDict[method][indexDict["object"]]
    heatObj   = methodDict[method][indexDict["heat"]]
    suffix    = methodDict[method][indexDict["suffix"]]

    simData = [] 
    for index, simFiles in enumerate(os.listdir(simFolder)) : 
            fileToCheck = os.path.join(simFolder, simFiles)  
            if os.path.exists(os.path.join(epiFolder, simFiles + suffix)) : 
                methodObj.setEpiSuffix(epiFolder, suffix)
                methodObj.refresh(query, fileToCheck)
                toAppend = [methodObj.similarity(), simFiles, fileToCheck]
                simData.append(toAppend)

    print "\n Loading ", k , " most similar sim files to query", query
    simData = sortSimData(simData)
    for rank, data in enumerate(simData[0:int(k)]) :
        print "\n Loading File " + str(data[1]) + " Rank =" + str(rank + 1) + \
              " Similarity = " + str(simData[rank][0])
        heatObj.refresh(data[2])
    
