# Reads one csv file. 
# quantifies and creates the gausian levels. 

# Has a dictionary of states to their data points.
# One data point has a timestamp and value assosciated with it. 

# For each time series. h units are move

import sys
import os

import dataNorm

"""
Process should be 
"""
def process(directory, fileName, dataNormalizer, r, h, w) :

    std = 0.25 #std deviation for bell curve. 
    dataNormalizer = dataNorm.DataNormalizer(r, h, w, std, fileName)
    csvHandle = open(directory + "/" + fileName)
    lines = csvHandle.readlines()

    relHeader = lines[0].split(',')[2:]
    for index, header in enumerate(relHeader) :
        dataNormalizer.addState(header.strip(), index) 

    for line in lines[1:] :
        for index, data in enumerate(line.split(',')[2:]) :
             dataNormalizer.addData(data, index) 
      
    dataNormalizer.normalize()
    # 0 is the first iteration in the list. 
    dataNormalizer.dumpEpidemicWord(0)
    #dataNormalizer.dump()
    csvHandle.close()

def usage() :

    print "\nPlease pass the directory with csv files as an argument."

if __name__ == "__main__" :

    if len(sys.argv) < 5 : 
        usage()	
        sys.exit(1)

    directory = sys.argv[1]
    r = int(sys.argv[2])      
    h = int(sys.argv[3])
    w = int(sys.argv[4])

    # for each file f in the directory do the processing.
    for fileName in os.listdir(directory) :
        if '.csv' in fileName: 

             process(directory, fileName, dataNorm, r, h, w)

