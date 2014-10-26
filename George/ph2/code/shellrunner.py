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
        self._commandLine.append(os.path.abspath(simFile1))
        self._commandLine.append(os.path.abspath(simFile2))

    def similarity(self) :
        self._output = subprocess.check_output(self._commandLine)
        return self._output

    def __repr__(self) :
 
        return "Shell Runner Command line = " + str(self._commandLine)

if __name__ == "__main__" :

    if len(sys.argv) < 3 :
        print "Error Inputs not passsed to the script."
        sys.exit(1)

    print "#### Shell Runner"

    simFile1 = sys.argv[1]
    simFile2 = sys.argv[2]

    print "#### Checking similarity of", simFile1, " and ", simFile2
    shellRunner = ShellRunner("java -jar inputs/hello.jar") 
    shellRunner.refresh(simFile1, simFile2) 
    print shellRunner.similarity()
    print shellRunner
