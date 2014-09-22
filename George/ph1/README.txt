README
=====

`Task 1` : 

Files : 
task1.py    --> Imports dataNorm and then calls the utility functions in them. 
dataNorm.py -->  Data Normalizer corresponds to one file rather than a set of file. Given an input file, window length w, shift length h & no of levels r. It normalises and quantized the data in the file and dumps it into the epidemic_word_file

Invoking :  python task1.py <directory with input Csv> <r> <h> <w>
Pass the directory with the csv files and the values of r, h and w to run the 
program. 

Output : Normalized, Quantized and windowed output in a file named epidemic_word_file given an input shift and window length under the current working directory. 

`Task 2` :

Files 
task2.py --> takes the graphFile and weight of alpha as input. 
avgdiff.py --> calculates the neighbours of each state and uses the information
to find the average and diff.

Invocation : python task2.py ../sampledata_P1_F14/Graphs/LocationMatrix.csv
Output : The average and diff files dumped into files.  

`Task 3` :

Files 

Invocation : python task3.py <path to csv> <epidemic_file_name> <path to location matrix> <window length>
Output : The heat map of the csv file with max, min values from the epidemic file with the specified window length. 

General
=======

run.sh file should run all the tasks together. 
