import matplotlib
import matplotlib.pyplot as plt
import matplotlib.patches as patches

import math

"""
Draws the heatmap when the heat Points, and max Lengths
Plots states X iterations
"""
class HeatMap(object) :

    def __init__(self, heatPoints, maxVal, maxX, maxY, heatLength, markList, highList) :

        self._heatPoints = heatPoints
        self._markList   = []
        self._maxVal     = maxVal
        self._maxX       = maxX
        self._maxY       = maxY
        self._heatLength = heatLength
        self._markList   = markList
        self._highList   = highList

    def addMark(self, toMarkList) :

        for mark in toMarkList : 
           self._markList.append(mark) 

    def highLight(self, highList) :
 
        for high in highList :
           self._highList.append(high)       
         
    def draw(self) :
 
        myCm = plt.get_cmap('afmhot')
        norm = matplotlib.colors.Normalize(0, self._maxVal)

        for mark in self._markList :
            plt.text(int(mark[1]), int(mark[2]), mark[0], color=mark[3])

           
        for heatP in self._heatPoints : 
            col = myCm(norm(heatP[2]))
            rect  = patches.Rectangle((heatP[0], heatP[1]), self._heatLength, 1, color=col)
            ax = plt.gca()
            ax.add_patch(rect) 

        for high in self._highList : 

            rect  = patches.Rectangle((high[0], high[1]), self._heatLength, 1, color='c')
            ax = plt.gca()
            ax.add_patch(rect) 

        cbar = matplotlib.cm.ScalarMappable(norm, myCm)
        cbar.set_array(range(0, int(math.ceil(self._maxVal))))
        plt.axis([0, self._maxX, 0, self._maxY])
        plt.grid(True)
        plt.colorbar(cbar)
        plt.ylabel('States')
        plt.xlabel('Iterations')

        plt.show()
