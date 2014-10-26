"""
Parses one line epidemic Simulation File 
and returns the contents split into objects. 
"""

class Parser(object) :

    def __init__(self, line) :

       header     = line.split()[0][2:-2]
       self.File  = header.split(',')[0]
       self.State = header.split(',')[1]
       self.Time  = header.split(',')[2] 
   
       vectorString = line.split()[1][1:-2]
       self.Vector = []

       sumVect = 0
       for elem in vectorString.split(',') :
           self.Vector.append(float(elem))
           sumVect = sumVect + float(elem) * float(elem)
       self.Magnitude = pow(sumVect, 0.5)

    def __repr__(self) :
 
       return "File = " + str(self.File) + "\n" \
              "State =" + str(self.State) + "\n" \
              "Time = " + str(self.Time) + "\n" \
              "Vector = " + str(self.Vector) + "\n" \
              "Magnitude = " + str(self.Magnitude)

if __name__ == "__main__" :

     parser = Parser("<<Sample1.csv,US-AK,0>, <0.288162854367,0.288162854367,0.288162854367,0.288162854367,0.288162854367>>")
     print parser
