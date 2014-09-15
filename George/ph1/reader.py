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
def process(directory, fileName, dataNormalizer) :

    r = 6 # Number of levels. 
    h = 1 #the shift
    w = 4 # window length.
    std = 0.25 #std deviation for bell curve. 
    dataNormalizer = dataNorm.DataNormalizer(r, h, w, std, fileName)
    csvHandle = open(directory + "/" + fileName)
    lines = csvHandle.readlines()

    relHeader = lines[0].split(',')[2:-1]
    for index, header in enumerate(relHeader) :
        dataNormalizer.addState(header, index) 

    for line in lines[2:] :
        for index, data in enumerate(line.split(',')[2:-1]) :
             dataNormalizer.addData(data, index) 
      
    dataNormalizer.normalize()
    # 0 is the first iteration in the list. 
    dataNormalizer.dumpEpidemicWord(0)
    dataNormalizer.dump()
    csvHandle.close()

### The Main of the program starts from here. 

def usage() :

    print "\nPlease pass the directory with csv files as an argument."

if len(sys.argv) < 2 : 
     usage()	
     sys.exit(1)

directory = sys.argv[1]

# for each file f in the directory do the processing.
for fileName in os.listdir(directory) :

    if '.csv' in fileName: 

         process(directory, fileName, dataNorm)

