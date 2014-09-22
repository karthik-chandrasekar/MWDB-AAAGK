from scipy import signal
import numpy

from scipy import integrate
from scipy.stats import norm 

"""
Data Normalizer corresponds to one file rather than a set of files.
Given an input file, window length w, shift length h & no of levels r.
it normalises and quantized the data in the file and dumps it into the 
epidemic_word_file
"""

class DataNormalizer(object) :

    """
    The state map is a mapping between states to its data
    """

    def __init__(self, r, h, w, std, fileName) :

        # Maps indexes to the stateName. 
        self._indexMap = {}
        self._headerMap = {}
        self._maxVal = 0.0
        self._bands = self.getBands(r, std)
        self._bandsMid = self.getMid()
        self._shift = h
        self._windowLength = w
        self._noOfBands = r 
        self._epidemicWordFile = "epidemic_word_file"
        self._file = fileName


    def getMid(self) :

        bandsMid = [0] * len(self._bands)
        for index in range(1, len(self._bands) - 1)   :

	    mid = (self._bands[index] - self._bands[index - 1])/2.0
            bandsMid[index] = self._bands[index - 1] + mid
        return bandsMid            

    """
    Returns the normal pdf from the value of x.
    """
    def normpdf(self, x, std) :

        return norm.pdf(x, loc=0, scale=std)

    """ 
    There would be r bands. Hence r bandlengths would be returned
    by the function.
    """
    def getBands(self, r, std) :

        bands = list()
        bands.append(0)
        denom = integrate.quad(self.normpdf, 0, 1, args=0.25)
        for i in range(1, r + 1) :
	    xmin = (i-1)*1.0/r
            xmax = i*1.0/r  
            numer = integrate.quad(self.normpdf, xmin, xmax, args=0.25)
            length = numer[0]/denom[0]
            bands.append(length)

        currVal = bands[0]
        for index, band in enumerate(bands) :
            bands[index] = currVal     
            if index + 1 < len(bands) - 1 :
                currVal = currVal + bands[index + 1]

        return bands

    """
    Creates the header map. 
    """
    def addState(self, header, index) :

        if header not in self._headerMap :
            self._indexMap[index] = header
            self._headerMap[header] = list()

        else :
            return

    """
    Adds the data into a dictionay corresponding to its header.
    Its a map of state names to a list of values from the file. 
    """
    def addData(self, data, index) :

        if index in self._indexMap :
            if self._indexMap[index] in self._headerMap :
		try : 
                    data = float(data)
                    if self._maxVal < data :
                       self._maxVal = data
		    self._headerMap[self._indexMap[index]].append(data)
                except ValueError : 
		    self._headerMap[self._indexMap[index]].append(data)
   
    """
    Given a window it returns the quantized window.
    """
    def getQuantized(self, window) :
         #compare with self._bands
         for winIndex, value in enumerate(window) : 

             for index, band in enumerate(self._bands) :
                 if value < band : 
                      window[winIndex] = self._bandsMid[index] 
                      break
                    
         return window

    """
    Dumps the pair <idx, win> 
    idx = <file, state, time>
    win is center of the band it corresponds to. 
    """             
    def dumpWindow(self, time, state, window) :
         quantizedComp = self.getQuantized(window)
         win = "<" 
         for comp in quantizedComp : 
 
             win = win + str(comp) + ","   
         # Replacing , at the end with > 
         win = win[:-1] + ">"

         idx = "<" + self._file + "," + state + "," + str(time) + ">" 
         line = "<" + idx + ", " + win + ">\n"
         epHandle = open(self._epidemicWordFile, "a")
         epHandle.write(line)
         epHandle.close()  
         return quantizedComp
 
    def dumpEpidemicWord(self, startTime) :
        # In the list start at the startTime.
        # Call Process window which gets a block of window out. 
        index = startTime
        endIndex = len(self._headerMap[self._indexMap[0]]) 
        noOfStates = len(self._headerMap)
     
        while index <= endIndex - self._windowLength :
            for stateIndex in self._indexMap :
                stateData = self._headerMap[self._indexMap[stateIndex]] 
                self.dumpWindow(index, self._indexMap[stateIndex], stateData[index : index + self._windowLength]) 
            index = index + self._shift

    """
    normalizes data between 0 and 1
    """
    def normalize(self) :
 
       for header in self._headerMap :
            if header is not "time" or header is not "iteration" :
                for iteration, data in enumerate(self._headerMap[header]) :
                    self._headerMap[header][iteration] = data/self._maxVal
    """
    For debugging purposes.
    """ 
    def dump(self) :

        for header in self._headerMap :
            print header + "--> " + str(self._headerMap[header])

	for band in self._bands :

            print "Log: Gausian bands used =", str(float(band))
        print "Max value in this file =", self._maxVal
        print "Bands", self._bands
        print "Band Mid", self._bandsMid
