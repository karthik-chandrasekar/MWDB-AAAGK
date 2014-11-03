Working of part1a
=================

python code/eucled.py <f1> <f2>
prints the similarity on the screen.

Here the sum of the values of eucledian over a state is calculated.
These values are summed up and then divided by the number of states.

1/(1 + (SumofEucledian on All States)/noOfStates)

Working of part1b
=================

python code/dtwdynamic.py <f1> <f2>
prints the similarity on the screen.

Here the sum of the values of dtw over a state is calculated.
These values are summed up and then divided by the number of states.

1/(1 + (SumofDTW on All States)/noOfStates)

Task2
======

python code/2.py <query> <simFiles Folder> <epi Folder> k methodName

query - query file.
simFiles Folder - Folder containing the simulation files in csv format.
epiFolder - For some Methods the simulations run using the word files. So this is the file which contains all the word files.
k - Top k files to load on the heat Map
methodName - The Name of the method to be invoked with. 

The list of methods are : 

Method Codes

Name           | Message
BinaryAvg      | Computes the similarity of Average Files using binary vector (1e)
Eucledian      | Computes the eucledian to give the similarity (1a)
Weighted       | Computes the weighted similarity (1f)
BinaryDiff     | Computes the weighted similarity of avg files (1d)
WeightedDiff   | Computes the weighted similarity of diff files (1g)
Binary         | Computes the similarity using binary vector (1c)
DTW            | Computes the dtw between 2 simFiles (1b)
WeightedAvg    | Computes the weighted similarity of avg files (1h)

Important classes in the code base
==================================

dtwdynamic.py(DTW) - Extends from Similatity and computes DTW when 2
sim files are passed.
heatmap.py(HeatMap) - Draws the heatmap when the heat Points, and max Lengths Plots StatesXIterations
parsecsv.py(SimParser) - Returns the data structure containing the dictionary of the stateNames when lines are passed to it. 
eucled.py(Eucledian) - Extends similarity and computes eucledian when two simfiles are passed to it.
similarity.py(Similarity) - Base class for all the similarities. Has the eucledian function as it is helpful in most distances.
shellrunner.py(ShellRunner) - Wraper for running programs on the shell. Also adds the prefix to the files so as to use the naming convention.
