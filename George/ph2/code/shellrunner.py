#print "Going to run a java program"
import subprocess
import sys
import os
#output = subprocess.check_output(['java', '-jar', 'inputs/hello.jar'])
#print incOut

class ShellRunner(object) :

    def __init__(self, commandLine) :

        self._commandLine = commandLine.split()

    def refresh(self, simFile1, simFile2) :

        self._simFile1 = simFile1
        self._simFile2 = simFile2
        self._commandLine.append(os.path.abspath(self.getEpiFileName(simFile1)))
        self._commandLine.append(os.path.abspath(self.getEpiFileName(simFile2)))

    def getEpiFileName(self, simFile1) :   
 
        folder, simFile = os.path.split(simFile1)       
        return os.path.join(self._epiFolder, self._prefix + simFile)

    def similarity(self) :
        self._output = subprocess.check_output(self._commandLine)
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
    shellRunner = ShellRunner("java -jar input/karthik.jar input/LocationMatrix.csv") 
    import os
    epiFolder = os.path.abspath(epiFolder)   
    shellRunner.setEpiSuffix(epiFolder, "_word.txt")
    shellRunner.refresh(simFile1, simFile2) 
    print shellRunner.similarity()
   # print shellRunner
