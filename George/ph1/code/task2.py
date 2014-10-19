"""
Input  - epidemic_word_file and graph_file 
Output - epidemic_word_file_avg & epidemic_word_file_diff
""" 

import sys

import avgdiff

## The main starts here for the program. 

def usage() :

    print "\nPlease pass graph file and weight as an argument."

if __name__ == "__main__" :

    if len(sys.argv) < 3 : 
        usage()	
        sys.exit(1)

    graphFile = sys.argv[1]
    weight    = sys.argv[2]

    avgDiff = avgdiff.AvgDiff(graphFile, weight) 
    avgDiff.dumpAvg()
    avgDiff.dumpDiff()
    avgDiff.dumpObj()
