Take inputs to the program. 

Work length w, 
Shift Length h,
Resolution r,
Directory dir.

Iterate through all the csv files in the directory.
Transform data according to the requirements. 
Dump it to another file. 

reader.py
 --> Reads the file and creates 
 --> Quantizes everything into r levels. 
 --> Have a dictionary which keeps track of the state to maps. 

 --> Move h time units at a time
 --> When the slice expires. Call the dumper to do the dumping.  
 
dumper.py


 -----------------------
|                       |
|			|
|                       |
|                       |
