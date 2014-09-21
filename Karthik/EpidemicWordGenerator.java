import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import matlabcontrol.*;


class MyUtil{
    
    public File[] getFilesInDir(String path, Logger logger) throws Exception
    {
        //Return list of files present in a given directory path
        File dirObj = new File(path);
        File[] inputFiles = dirObj.listFiles();
        return inputFiles;
    }
    
     public List<List<String>> readCsv(File inputFile, Logger logger) throws Exception
        {
            //Return list of lines of input csv file     
            List<String> headerList = new ArrayList<String>();
            List<List<String>> valuesList = new ArrayList(new ArrayList<String>());
            Scanner scannerObj = new Scanner(inputFile);
            
            //Collect header list
            headerList  = Arrays.asList(scannerObj.nextLine().split(","));
            valuesList.add(headerList);     
            
            //Collect values list
            while(scannerObj.hasNextLine())
            {
                valuesList.add(Arrays.asList(scannerObj.nextLine().split(",")));               
            }
            
            return valuesList;        
        }   
     
     public HashMap<String, List<String>> formHeaderValueHash(List<List<String>> valuesList)
     {
         
         // Form a hash with column header as key and column as values. 
         HashMap<String, List<String>> headerValueColumnMap = new HashMap<String, List<String>>();
         List<String> headerList = new ArrayList<String>();
         int valueCount;
         List<String> mapValueList;
         String key;
         
         headerList = valuesList.get(0);
         valuesList.remove(0);
         
         for(List<String> valueList : valuesList)
         {
             valueCount = 0;
             for(String value : valueList)
             {
                 key = headerList.get(valueCount);
                 mapValueList = headerValueColumnMap.get(key);
                 if (mapValueList == null)
                 {
                     mapValueList = new ArrayList<String>();
                 }
                 mapValueList.add(value);

                 headerValueColumnMap.put(key, mapValueList);
                 valueCount++;
             }
             
         }
         return headerValueColumnMap;
     }
}
     

class EpidemicDataHandler{
    
 
    List<Double> bandsList;
    List<String> bandRepList;
    Double Alpha;
    String maxState;
    String minState;
    int maxTimeIteration;
    int minTimeIteration;
    HashMap<String,Integer> timeStringIterationMap = new HashMap<String,Integer>();
    List<String> stateList;
    List<String> maxEpidemicWordFileVector;
    List<String> minEpidemicWordFileVector;
    List<String> maxEpidemicWordFileDiffVector;
    List<String> minEpidemicWordFileDiffVector;
    List<String> maxEpidemicWordFileAvgVector;
    List<String> minEpidemicWordFileAvgVector;
    
    public void dumpInFile(List<List<String>> valuesList, String outputFileName, String outputDir) throws Exception
    {
        //Dump normalized input files
        
        PrintWriter writer = new PrintWriter(outputDir+outputFileName+"_Normalized", "UTF-8");
        String outputString = "";
   

        for(List<String> valueList: valuesList)
        {
            outputString = "";
            for(String dataVal: valueList)
            {
                outputString = outputString + dataVal + ',';
            }
            outputString = outputString.substring(0, outputString.length()-1);
            writer.println(outputString);
        }
        writer.close();
    }

  
    
    public double findMax(List<List<String>> valuesList)
    {
        //Find the max value in a file to normalize the file data. 
        Double maxValue = 0.0;
        int valueCount;
        int loopCount = 0; 
        
        for(List<String> valueList : valuesList)
        {
            valueCount = 0;
            loopCount ++;
            if(loopCount == 1){continue;}
            
            for(String value : valueList)
            {
                 valueCount ++;
                 if (valueCount > 2)
                 {
                     if(Double.parseDouble(value) > maxValue)
                     {
                         maxValue = Double.parseDouble(value);
                     }
                 }
            }   
        }
        return maxValue;
    }
    
    public List<List<String>> normalizeData(List<List<String>> valuesList, double maxValue) throws Exception
    {
        //Normalize the input file data with the max value given
        int valueCount;
        int loopCount = 0; 
        
        List<List<String>> normalizedValuesList = new ArrayList();
        List<String> normalizedValueList;
        
        for(List<String> valueList : valuesList)
        {
            valueCount = 0;
            loopCount ++;
            if(loopCount == 1)
            {
                normalizedValuesList.add(valueList);
                continue;
            }
            
            normalizedValueList = new ArrayList<String>();
            for(String value : valueList)
            {
                 valueCount ++;
                 if (valueCount > 2)
                 {
                     normalizedValueList.add(String.valueOf((Double.parseDouble(value)/maxValue)));            
                    
                 }
                 else
                 {
                     normalizedValueList.add(value);
                 }
            }
            normalizedValuesList.add(normalizedValueList);
        }
        
        return normalizedValuesList;
    }
    
    void generateEpidemicWordFile(HashMap<String, List<String>> headerValueColumnMap, String fileName, List<String> headerList, PrintWriter writer, Logger logger, HashMap<String, String> epidemicWordFileHash, List<String> epidemicWordFileValuesList, int window, int shift, String enteredFile)
    {
        //Generate the epidemic word file
        List<String> valueList = null;
        List<String> timeList;
        List<String> outputList;
        List<Double> vectorList;
        int valueSize ;
        String temp;
        String delim = ",";
        String outputString;
        String opListString;
        String opListStringDelim="#";
        timeList = headerValueColumnMap.get("time");
        
        logger.info(fileName);
        
        
        Double maxStrength = 0.0;
        Double curStrength = 0.0;
        Double minStrength = 10000.0;
        int timeIterationCount;
        
        stateList = new ArrayList<String>();
        for(String header: headerList)      
        {
            
            if (header.equals("iteration") || header.equals("time"))
            {
                continue;
            }
            stateList.add(header);
            timeIterationCount = 1;
            
            valueList = headerValueColumnMap.get(header);       
            valueSize = valueList.size();
            
            opListString = "";
            //System.out.println("Value size " + valueSize);
            for(int startIndex=0;startIndex<valueSize;)
            {
                
                outputList = new ArrayList<String>();
                outputList.add(fileName);
                outputList.add(header);
                
                //System.out.println("Start index " + startIndex);
                outputString = "";
                if (startIndex+window < valueSize)
                {
                    //outputList.add(timeList.get(startIndex));
                    timeStringIterationMap.put(timeList.get(startIndex), timeIterationCount);
                    outputList.add(String.valueOf(timeIterationCount));
                    timeIterationCount = timeIterationCount + shift;
                    vectorList = new ArrayList<Double>();
                    for(int j=startIndex; j<startIndex+window;j++)
                    {
                        temp = getBandRep(valueList.get(j));
                        outputList.add(temp);
                        vectorList.add(Double.parseDouble(temp));
                    }
                    
                    {
                        curStrength = getTwoNorm(vectorList);
                        if (curStrength > maxStrength)
                        {
                            maxEpidemicWordFileVector = outputList;
                            maxStrength = curStrength;
                        }
                        else if(curStrength < minStrength)
                        {
                            minEpidemicWordFileVector = outputList;
                            minStrength = curStrength;
                        }
                    }
                    
                    for(String output: outputList)
                    {
                        outputString = outputString + output + delim;
                    }
                    outputString = outputString.substring(0, outputString.length()-1);
                    writer.println(outputString);
                    epidemicWordFileValuesList.add(outputString);
                                      
                    opListString += outputString + opListStringDelim;                    
                    startIndex = startIndex + shift;
                }
                else
                {
                    break;
                }
            }
            opListString = opListString.substring(0, opListString.length()-1);
            epidemicWordFileHash.put(fileName+"-"+header, opListString);
        }  
        
        if (fileName.equals(enteredFile))
        {
            System.out.println("maxEpidemicWordFileVector  " + maxEpidemicWordFileVector);
            System.out.println("minEpidemicWordFileVector " + minEpidemicWordFileVector);
            System.out.println("maxStrength " + maxStrength);
            System.out.println("minStrength "+ minStrength );
        }   
    }
    
    String getBandRep(String value)
    {
        //For a given value, return the representation point of the range in which the input value belongs. 
        Double inputValue = Double.valueOf(value);
        String bandRep = "";
        
        for(int i=0;i< bandsList.size();i++)
        {
            if( inputValue >= bandsList.get(i) && inputValue <= bandsList.get(i+1))
            {
                bandRep = bandRepList.get(i);
                break;
            }
        }
        return bandRep;
    }
    
    
    public HashMap<String, List<String>> formAdjacencyHashMap(String inputFilePath) throws Exception
    {
        
        //Parse adjacency matrix and form a state, adjacent states hash map
        
        HashMap<String, List<String>> adjacencyHashMap = new HashMap<String, List<String>>();
        List<String> headerList;
        List<String> valueList;
        int count;
        String header="";
        
        
        Scanner scannerObj = new Scanner(new File(inputFilePath));
        
        headerList = Arrays.asList(scannerObj.nextLine().split(","));
        while(scannerObj.hasNextLine())
        {
            count = 0; 
            valueList = new ArrayList<String>();
            for(String value : scannerObj.nextLine().split(","))
            {
                if(count == 0)
                {
                    header = value;
                    count++;
                    continue;
                }
                else if (Integer.parseInt(value)==1)
                {
                    valueList.add("US-"+headerList.get(count));
                }
                count ++;
            }
            adjacencyHashMap.put("US-"+header, valueList);
        }     
        return adjacencyHashMap;
    }
      
    
    void generateEpidemicAvgDiffFile(HashMap<String, String> epidemicWordFileHash, List<String> epidemicFileValuesList, HashMap<String, List<String>> adjacencyHashMap, String enteredFile, String outputDir) throws Exception
    {
        
        //Generate EpidemicWordFileAvg and EpidemicWordFileDiff
        List<String> tempList;
        String hashOutputListString;
        String key;
        Double tempDouble;
        int count;
        List<Double> resultantList;
        List<Double> newEpidemicWordFileAvgList=null;
        List<Double> newEpidemicWordFileDiffList=null;
        int hashOutputListSize;
        String diffString;
        String avgString;
        List<String> neighborList;
        
        PrintWriter epidemicAvgWriter = new PrintWriter(outputDir+"epidemic_word_file_avg", "UTF-8");
        
        PrintWriter epidemicDiffWriter = new PrintWriter(outputDir+"epidemic_word_file_diff", "UTF-8");
        
       
        Double maxStrengthAvg = 0.0;
        Double curStrengthAvg = 0.0;
        Double minStrengthAvg = 10000.0;
        
       
        Double maxStrengthDiff = 0.0;
        Double curStrengthDiff = 0.0;
        Double minStrengthDiff = 10000.0;
        
           
        
        for(String entry : epidemicFileValuesList)
        {                       
                    tempList = Arrays.asList(entry.split(",")); 
                    if (tempList.isEmpty()){continue;}
                    resultantList = new ArrayList<Double>();
                    
                    for(int i=0;i<tempList.size()-3;i++)
                    {
                        resultantList.add(0.0);
                    }
                    key = tempList.get(0)+"-"+tempList.get(1);
                    
                    neighborList = adjacencyHashMap.get(tempList.get(1));
                    
                    if(neighborList.isEmpty())
                    {

                        newEpidemicWordFileAvgList = getEpidemicWordFileAvg(tempList, new ArrayList<Double>());
            
                    }
                    else
                    {
                        hashOutputListString = "";
                        for(String neighbor: neighborList)
                        {
                            hashOutputListString += epidemicWordFileHash.get(tempList.get(0)+"-"+neighbor) + "#";
                        }
                        hashOutputListString = hashOutputListString.substring(0, hashOutputListString.length()-1);
                    
                        if (hashOutputListString == null)
                        {
                            System.out.println("Value not present for key  " + key);
                            continue;
                        }
                        List<String> tempValList;
                        hashOutputListSize = 0;
                        for(String temp: hashOutputListString.split("#"))
                        {
                            if (temp.isEmpty()){continue;}
                            tempValList = Arrays.asList(temp.split(","));
                            if (tempValList.isEmpty() || tempList.isEmpty()){continue;}
                            if(!(tempValList.get(2).equals(tempList.get(2))))
                            {
                                continue;
                            }
                            hashOutputListSize++;
                            count = 0;
                            for(String tempValue : tempValList)
                            {
                                if(count < 3){count++;continue;}
                                tempDouble = resultantList.get(count-3);
                                tempDouble += Double.parseDouble(tempValList.get(count));
                                resultantList.set(count-3, tempDouble);
                                count++;
                            }
                        }
                    
                        for(int i=0;i<resultantList.size();i++)
                        {
                            resultantList.set(i,resultantList.get(i)/hashOutputListSize);
                        }
                        
                        //EpidemicWordFileAvg 
                        newEpidemicWordFileAvgList = getEpidemicWordFileAvg(tempList, resultantList);

                    }
                   
                    
                    if (tempList.get(0).equals(enteredFile))
                    {
                        curStrengthAvg = getTwoNorm(newEpidemicWordFileAvgList);
                        if (curStrengthAvg > maxStrengthAvg)
                        {
                            maxEpidemicWordFileAvgVector = tempList;
                            maxStrengthAvg = curStrengthAvg;
                        }
                        else if(curStrengthAvg < minStrengthAvg)
                        {
                            minEpidemicWordFileAvgVector = tempList;
                            minStrengthAvg = curStrengthAvg;
                        }
                    }
                        
                    for(int i=3;i<tempList.size();i++)
                    {
                        tempList.set(i, String.valueOf(newEpidemicWordFileAvgList.get(i-3)));
                    }
                    avgString = "";
                    for(String temp: tempList)
                    {
                        avgString = avgString + temp + ",";
                    
                    }
                    avgString = avgString.substring(0, avgString.length()-1);
                    epidemicAvgWriter.println(avgString);
                                    
                    if(neighborList.isEmpty())
                    {
                        newEpidemicWordFileDiffList = new ArrayList<Double>();

                        for(int i=3;i<tempList.size();i++)
                        { 
                            newEpidemicWordFileDiffList = getEpidemicWordFileDiff(tempList, new ArrayList<Double>());                        
                        }           
                    }
                    else
                    {
                        //EpidemicWordFileDiff
                        newEpidemicWordFileDiffList = getEpidemicWordFileDiff(tempList, resultantList);
                    }
                    if (tempList.get(0).equals(enteredFile))
                    {
                        curStrengthDiff = getTwoNorm(newEpidemicWordFileDiffList);
                        if (curStrengthDiff > maxStrengthDiff)
                        {
                            maxEpidemicWordFileDiffVector = tempList;
                            maxStrengthDiff = curStrengthDiff;
                        }
                        else if(curStrengthDiff < minStrengthDiff)
                        {
                            minEpidemicWordFileDiffVector = tempList;
                            minStrengthDiff = curStrengthDiff;
                        }
                    }
                    
                    
                    for(int i =3;i<tempList.size();i++)
                    {
                        tempList.set(i, String.valueOf(newEpidemicWordFileDiffList.get(i-3)));
                    }
                    diffString = "";
                    for(String temp: tempList)
                    {
                        diffString = diffString + temp + ",";
                    }
                    diffString = diffString.substring(0, diffString.length()-1);
                    epidemicDiffWriter.println(diffString);
            
        } 
        epidemicAvgWriter.close();
        epidemicDiffWriter.close(); 
        
        System.out.println("maxEpidemicWordFileVectorAvg  " + maxEpidemicWordFileAvgVector);
        System.out.println("minEpidemicWordFileVectorAvg " + minEpidemicWordFileAvgVector);
        System.out.println("maxStrengthAvg " + maxStrengthAvg);
        System.out.println("minStrengthAvg "+ minStrengthAvg);
        
        System.out.println("maxEpidemicWordFileVectorDiff  " + maxEpidemicWordFileDiffVector);
        System.out.println("minEpidemicWordFileVectorDiff " + minEpidemicWordFileDiffVector);
        System.out.println("maxStrengthDiff " + maxStrengthDiff);
        System.out.println("minStrengthDiff "+ minStrengthDiff );
    }
   
    Double getTwoNorm(List<Double> inputVector)
    {
        //Return the two norm of the input vector
        Double sum=0.0;
        for(Double value : inputVector)
        {
            sum+=value * value;
        }
        return Math.sqrt(sum);
    }
    
    List<Double> getEpidemicWordFileAvg(List<String> origVecList, List<Double> resultantList)
    {
        //Perform epidemic word file average computation operation
        List<Double> finalList = new ArrayList<Double>();
        Double temp;
        
        for(int i=3;i<origVecList.size(); i++)
        {
            finalList.add(Alpha*(Double.parseDouble(origVecList.get(i))));
        }
        
        for(int i=0;i<resultantList.size();i++)
        {
            temp = finalList.get(i);
            temp += (1-Alpha) * resultantList.get(i);
            finalList.set(i, temp);
        }   
        return finalList;
    }
    
    List<Double>  getEpidemicWordFileDiff(List<String> origVecList, List<Double> resultantList)
    {
        //Perform epidemic word file diff computation operation
        List<Double> finalList = new ArrayList<Double>();
        Double temp;
        
        if(resultantList.isEmpty())
        {
            for(int i=3;i<origVecList.size();i++)
            {
                resultantList.add(0.0);
            }
        }
        
        for(int i=3;i<origVecList.size();i++)
        {
            temp = (Double.parseDouble(origVecList.get(i)) - resultantList.get(i-3))/Double.parseDouble(origVecList.get(i)); 
            finalList.add(temp);
        }
        return finalList;
    }
    
    
    void formHeatMap(int [] heatMapMaxState, int [] heatMapMinState, String heatMapFileName, String locationFileName) throws Exception
    {
        Scanner scInput = new Scanner(System.in);
        
        String path = "cd('/Users/karthikchandrasekar/Documents/MATLAB')";
        String inputPath;
        System.out.println("Enter matlab source code directory");
        if(scInput.hasNextLine())
        {
            inputPath = scInput.nextLine();
            if (!inputPath.isEmpty())
            {
                path = inputPath;
            }
        }
        
      //Create a proxy, which we will use to control MATLAB
        MatlabProxyFactory factory = new MatlabProxyFactory();
        MatlabProxy proxy = factory.getProxy();
        
        //set matlab path
        proxy.eval(path);
        proxy.setVariable("max_elements", heatMapMaxState);
        proxy.setVariable("min_elements", heatMapMinState);
        System.out.println(heatMapFileName);
        proxy.setVariable("f_name", heatMapFileName);
        proxy.setVariable("location_fname", locationFileName);

       
        proxy.eval("plot_graph(f_name, location_fname, max_elements, min_elements)");
        proxy.disconnect();
        
    }
    
    public void main(Logger logger)
    {
        
        String dirPath = "/Users/karthikchandrasekar/Downloads/sampledata_P1_F14/Epidemic Simulation Datasets";
        //String dirPath = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
        String inputFilePath = "/Users/karthikchandrasekar/Downloads/LocationMatrix.csv";

        String inputDirPath = "";
        MyUtil muObj = new MyUtil();
        Double maxValue = 0.0;
        List<List<String>> valuesList;
        List<String> headerList;
        HashMap<String, List<String>> headerValueColumnMap;
        HashMap<String, String> epidemicWordFileHash = new HashMap<String, String>();
        List<String> epidemicFileValuesList = new ArrayList<String>();
        int window,shift;
        String outputDirPath = "";
        
        try
        {                       
        
            String outputDir = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
            Scanner scInput = new Scanner(System.in);
            
            System.out.println("Enter input directory");
            if(scInput.hasNextLine())
            {
                inputDirPath = scInput.nextLine();
                if (!inputDirPath.isEmpty())
                {
                    dirPath = inputDirPath;
                }
            }
            System.out.println("Entered input directory" + dirPath);
            
            System.out.println("Enter output directory");
            if(scInput.hasNextLine())
            {
                outputDirPath = scInput.nextLine();
                if (!outputDirPath.isEmpty())
                {
                    outputDir = outputDirPath;
                }
            }
            PrintWriter writer = new PrintWriter(outputDir+"epidemic_word_file", "UTF-8");

            
            System.out.println("Entered output directory" + outputDir );
            
            System.out.println("Enter window length");
            window = Integer.parseInt(scInput.nextLine());
            
            System.out.println("Enter shift length");
            shift = Integer.parseInt(scInput.nextLine());
            
            System.out.println("Enter alpha value");
            Alpha = Double.parseDouble(scInput.nextLine());
            
            
            String inputLocationFile;
            
            System.out.println("Enter location matrix input file location");
            Scanner sysInput = new Scanner(System.in);
            if(sysInput.hasNextLine())
            {
                inputLocationFile = sysInput.nextLine();
                if (!inputLocationFile.isEmpty())
                {
                    inputFilePath = inputLocationFile;
                }
            }
            
            //Bands Generator
            logger.info("Bands generator");
            BandsGenerator bg = new BandsGenerator();
            bg.main(logger);
            bandsList = bg.bandsList;
            bandRepList = bg.bandRepList;
            
            
            String enteredFile;
            
            System.out.println("Enter a file name for task 3");
            enteredFile = scInput.nextLine();
            
            for(File file : muObj.getFilesInDir(dirPath, logger))
            {
                if(!(file.getName().equals(enteredFile)))
                {continue;}
                //Task 1 - a
                //Normalize the input set of files
                valuesList = muObj.readCsv(file, logger);
                maxValue = findMax(valuesList);
                valuesList = normalizeData(valuesList, maxValue);
                dumpInFile(valuesList, file.getName(), outputDir);
                          
               
                //Task 1 - c
                //Generate EpidemicWordFile
                headerList = valuesList.get(0);
                headerValueColumnMap = muObj.formHeaderValueHash(valuesList);
                generateEpidemicWordFile(headerValueColumnMap, file.getName(), headerList, writer, logger, epidemicWordFileHash, epidemicFileValuesList, window, shift, enteredFile);
               
                valuesList = null;
                headerValueColumnMap = null;
            }
            logger.info("End of task 1"); 
            
            writer.close();
            
            //Task 2
            // Generate EpidemicWordFileAvg,EpidemicWordFileDiff         
            HashMap<String, List<String>> adjacencyHashMap;        
            adjacencyHashMap = formAdjacencyHashMap(inputFilePath);
            generateEpidemicAvgDiffFile(epidemicWordFileHash, epidemicFileValuesList, adjacencyHashMap, enteredFile, outputDir);
            logger.info("End of task 2");        
            
            
            
            //Task 3
            //Generate values for heat map and call mat lab function for given file
            String heatMapFileName = "/tmp/MWDBInput/Sample1.csv";
            
            String heatMapFile;
            System.out.println("Enter a file name for heatmap");
            heatMapFile = scInput.nextLine();
            System.out.println("Entered file name is " + heatMapFile);
            
            if(heatMapFile.equals("epidemic_word_file"))
            {
                maxState = maxEpidemicWordFileVector.get(1);
                minState = minEpidemicWordFileVector.get(1);
                maxTimeIteration = Integer.valueOf(maxEpidemicWordFileVector.get(2));
                minTimeIteration = Integer.valueOf(minEpidemicWordFileVector.get(2));
            }
            else if(heatMapFile.equals("epidemic_word_file_avg"))
            {
                System.out.println(maxEpidemicWordFileAvgVector);
                System.out.println(minEpidemicWordFileAvgVector);
                
                maxState = maxEpidemicWordFileAvgVector.get(1);
                minState = minEpidemicWordFileAvgVector.get(1);
                maxTimeIteration = Integer.valueOf(maxEpidemicWordFileAvgVector.get(2));
                minTimeIteration = Integer.valueOf(minEpidemicWordFileAvgVector.get(2));
            }
            else if(heatMapFile.equals("epidemic_word_file_diff"))
            {
                maxState = maxEpidemicWordFileDiffVector.get(1);
                minState = minEpidemicWordFileDiffVector.get(1);
                maxTimeIteration = Integer.valueOf(maxEpidemicWordFileDiffVector.get(2));
                minTimeIteration = Integer.valueOf(minEpidemicWordFileDiffVector.get(2));
            }
            
            
            
            
            ArrayList<Integer> heatMapMaxState = new ArrayList<Integer>();
            ArrayList<Integer> heatMapMinState = new ArrayList<Integer>();
            
            heatMapMaxState.add(stateList.indexOf(maxState));
            heatMapMaxState.add(maxTimeIteration);
            heatMapMaxState.add(window);
            
            int count = 1;
            System.out.println("Maxstate " + maxState);
            System.out.println("Minstate " + minState);
            System.out.println("MaxTimeIteration" + maxTimeIteration);
            System.out.println("MinTimeIteration" + minTimeIteration);
            
            for(String neighbor: adjacencyHashMap.get(maxState))
            {
                heatMapMaxState.add(stateList.indexOf(neighbor));
                count ++;
            }
            
            
            heatMapMinState.add(stateList.indexOf(minState));
            heatMapMinState.add(minTimeIteration);
            heatMapMinState.add(window);
            count = 1;
            for(String neighbor : adjacencyHashMap.get(minState))
            {
                heatMapMinState.add(stateList.indexOf(neighbor));
                count ++;
            }

            
            int[] heatMapMax = new int[heatMapMaxState.size()];
            for(int i=0;i<heatMapMaxState.size();i++)
            {
                heatMapMax[i] = heatMapMaxState.get(i);
            }
           
            
            int[] heatMapMin = new int[heatMapMinState.size()];
            for(int j=0; j<heatMapMinState.size(); j++)
            {
                heatMapMin[j] = heatMapMinState.get(j);
            }
            
            System.out.println("StateList " + stateList);
            System.out.println("HeatMapMax " + heatMapMaxState);
            System.out.println("HeatMapMin " + heatMapMinState);
            System.out.println("Location file name" + inputFilePath);
            formHeatMap(heatMapMax, heatMapMin, heatMapFileName, inputFilePath); 
          
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
        }   
    }    
}

class BandsGenerator{
    
    List<Double> bandsList = new ArrayList<Double>();
    List<String> bandRepList; 
    
    public void main(Logger logger) throws MatlabConnectionException, MatlabInvocationException{
        
        int r;
        System.out.println("Enter r");
        Scanner scInput = new Scanner(System.in);
        r = Integer.parseInt(scInput.nextLine());
        System.out.println("Entered r value is " + r);
        
        String path = "cd('/Users/karthikchandrasekar/Documents/MATLAB')";
        String inputPath;
        System.out.println("Enter matlab source code directory");
        if(scInput.hasNextLine())
        {
            inputPath = scInput.nextLine();
            if (!inputPath.isEmpty())
            {
                path = inputPath;
            }
        }
                
        
         //Create a proxy, which we will use to control MATLAB
         MatlabProxyFactory factory = new MatlabProxyFactory();
         MatlabProxy proxy = factory.getProxy();
         
         //set matlab path
         proxy.eval(path);
         proxy.setVariable("r", r);
         proxy.eval("res=quantization(r)");
         double[] bands = (double[]) proxy.getVariable("res");
         proxy.disconnect();
         logger.info("Inside band generator");
         double temp=0.0;
        
        bandsList.add(temp);
        for(int i=0;i< bands.length;i++)
        {
            temp += bands[i];
            bandsList.add(temp);
        }
    
        /**bandsList.add(0.0000);
        bandsList.add(0.5763);
        bandsList.add(0.8904);
        bandsList.add(0.9836);
        bandsList.add(0.9986);
        bandsList.add(1.0);
        **/
        
        getBandRepList();    
        System.out.println("band rep list" + bandRepList);

    }
    
    void getBandRepList()
    {
        //Find the representation point for every band
        bandRepList = new ArrayList<String>();
        String output;
        
        for(int i=0;i+1<bandsList.size();i++)
        {
            output = String.valueOf((bandsList.get(i) + bandsList.get(i+1))/2.0);
            bandRepList.add(output);
        }
    }
}



public class EpidemicWordGenerator {

public static void main(String args[]) throws Exception
    {
        Logger logger = Logger.getLogger("MyLogger");
        FileHandler fh;

        fh = new FileHandler("EpidemicWordGenerator.log");
        logger.addHandler(fh);

        logger.info("Logger starts");

        EpidemicDataHandler dn = new EpidemicDataHandler();
        dn.main(logger);
    }   
}


