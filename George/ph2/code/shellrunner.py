import subprocess
import sys
import os

"""
Wraper for running programs on the shell. 
Also adds the prefix to the files so as to use the naming convention.
"""
class ShellRunner(object) :

    def __init__(self, commandLine) :

        self._commandLine = commandLine.split()

    def refresh(self, simFile1, simFile2) :

        self._simFile1 = simFile1
        self._simFile2 = simFile2

    def getEpiFileName(self, simFile1) :   
 
        folder, simFile = os.path.split(simFile1)       
        return os.path.join(self._epiFolder, self._prefix + simFile)

    def similarity(self) :
        commandLine = list(self._commandLine)
        commandLine.append(os.path.abspath(self.getEpiFileName(self._simFile1)))
        commandLine.append(os.path.abspath(self.getEpiFileName(self._simFile2)))
        self._output = subprocess.check_output(commandLine)
        return self._output
    
    def setEpiSuffix(self, epiFolder, prefix) :
        self._epiFolder = epiFolder
        self._prefix    = prefix
 
    def __repr__(self) :
 
        return "Shell Runner Command line = " + str(self._commandLine)

if __name__ == "__main__" :

    if len(sys.argv) < 3 :
        print "Error Inputs not passsed to the script."
        sys.exit(1)

    print "#### Shell Runner"

    simFile1 = sys.argv[1]
    simFile2 = sys.argv[2]
    epiFolder = sys.argv[3]

    print "#### Checking similarity of", simFile1, " and ", simFile2
    shellRunner = ShellRunner("java -jar input//karthik.jar input//LocationMatrix.csv") 
    import os
    epiFolder = os.path.abspath(epiFolder)   
    shellRunner.setEpiSuffix(epiFolder, "_word.txt")
    shellRunner.refresh(simFile1, simFile2) 
    print shellRunner.similarity()
   # print shellRunner
