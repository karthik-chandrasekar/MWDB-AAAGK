from scipy import signal
import numpy

from scipy import integrate
from scipy.stats import norm 

"""
Data Normalizer corresponds to one file rather than a set of files.
"""

class DataNormalizer(object) :

    """
    The state map is a mapping between states to its data
    """

    def __init__(self, r, h, w, std, fileName) :

        self._indexMap = {}
        self._headerMap = {}
        self._maxVal = 0.0
        self._bands = self.getBands(r, std)
        self._shift = h
        self._windowLength = w
        self._noOfBands = r 
        self._epidemicWordFile = "epidemic_word_file"
        self._file = fileName

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
            print "LOG i, x, y", i, xmin, xmax
            numer = integrate.quad(self.normpdf, xmin, xmax, args=0.25)
            length = numer[0]/denom[0]
            bands.append(length)

        bands.sort()
        return bands

    def addState(self, header, index) :

        if header not in self._headerMap :
            self._indexMap[index] = header
            self._headerMap[header] = list()

        else :
            return

    def addData(self, data, index) :

        if index in self._indexMap :
            if self._indexMap[index] in self._headerMap :
                # TODO Add a function which does the processing of data.
		try : 
                    data = float(data)
                    if self._maxVal < data :
                       self._maxVal = data
		    self._headerMap[self._indexMap[index]].append(data)
                except ValueError : 
		    self._headerMap[self._indexMap[index]].append(data)
   
    ## The math work goes here.  
    def getQuantized(self, window) :
         #compare with self._bands
         for winIndex, value in enumerate(window) : 

             for index, band in enumerate(self._bands) :
                 if value < band : 
                      window[winIndex] = (band - self._bands[index - 1])/2  
                    
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
         line = "<" + idx + "," + win + ">\n"
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
     
    def dump(self) :

  #      for header in self._headerMap :
   #         print header + "--> " + str(self._headerMap[header])

	for band in self._bands :

            print "Log: Gausian bands used =", str(float(band))
        print "Max value in this file =", self._maxVal
        print "Bands", self._bands
