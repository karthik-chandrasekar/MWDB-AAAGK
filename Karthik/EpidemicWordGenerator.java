import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import matlabcontrol.*;


class MyUtil{
    
    public File[] getFilesInDir(String path, Logger logger) throws Exception
    {
        File dirObj = new File(path);
        File[] inputFiles = dirObj.listFiles();
        return inputFiles;
    }
    
     public List<List<String>> readCsv(File inputFile, Logger logger) throws Exception
        {
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
    
    public void dumpInFile(List<List<String>> valuesList, String outputFileName) throws Exception
    {
        String outputDir = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
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
        
        List<Double> maxEpidemicWordFileVector = null;
        List<Double> minEpidemicWordFileVector = null;
        Double maxStrength = 0.0;
        Double curStrength = 0.0;
        Double minStrength = 10000.0;
       
        
        for(String header: headerList)      
        {
            
            if (header.equals("iteration") || header.equals("time"))
            {
                continue;
            }
            
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
                    outputList.add(timeList.get(startIndex));
                    vectorList = new ArrayList<Double>();
                    for(int j=startIndex; j<startIndex+window;j++)
                    {
                        temp = getBandRep(valueList.get(j));
                        outputList.add(temp);
                        vectorList.add(Double.parseDouble(temp));
                    }
                    
                    if (outputList.get(0).equals(enteredFile))
                    {
                        curStrength = getTwoNorm(vectorList);
                        if (curStrength > maxStrength)
                        {
                            maxEpidemicWordFileVector = vectorList;
                            maxStrength = curStrength;
                        }
                        else if(curStrength < minStrength)
                        {
                            minEpidemicWordFileVector = vectorList;
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
    
    
    public HashMap<String, List<String>> formAdjacencyHashMap() throws Exception
    {
        
        //Parse adjacency matrix and form a state, adjacent states hash map
        
        HashMap<String, List<String>> adjacencyHashMap = new HashMap<String, List<String>>();
        List<String> headerList;
        List<String> valueList;
        int count;
        String header="";
        
        String inputFilePath = "/Users/karthikchandrasekar/Downloads/LocationMatrix.csv";
        
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
      
    
    void generateEpidemicAvgDiffFile(HashMap<String, String> epidemicWordFileHash, List<String> epidemicFileValuesList, HashMap<String, List<String>> adjacencyHashMap, String enteredFile) throws Exception
    {
        
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
        
        String outputDir = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
        PrintWriter epidemicAvgWriter = new PrintWriter(outputDir+"EpidemicWordFileAvg", "UTF-8");
        
        PrintWriter epidemicDiffWriter = new PrintWriter(outputDir+"EpidemicWordFileDiff", "UTF-8");
        
        List<Double> maxEpidemicWordFileAvgVector = null;
        List<Double> minEpidemicWordFileAvgVector = null;
        Double maxStrengthAvg = 0.0;
        Double curStrengthAvg = 0.0;
        Double minStrengthAvg = 10000.0;
        
        List<Double> maxEpidemicWordFileDiffVector = null;
        List<Double> minEpidemicWordFileDiffVector = null;
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
                        newEpidemicWordFileAvgList = new ArrayList<Double>();

                        for(int i=3;i<tempList.size();i++)
                        { 
                            newEpidemicWordFileAvgList.add(Double.parseDouble(tempList.get(i)));                        
                        }           
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
                            maxEpidemicWordFileAvgVector = newEpidemicWordFileAvgList;
                            maxStrengthAvg = curStrengthAvg;
                        }
                        else if(curStrengthAvg < minStrengthAvg)
                        {
                            minEpidemicWordFileAvgVector = newEpidemicWordFileAvgList;
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
                            newEpidemicWordFileDiffList.add(Double.parseDouble(tempList.get(i)));                        
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
                            maxEpidemicWordFileDiffVector = newEpidemicWordFileDiffList;
                            maxStrengthDiff = curStrengthDiff;
                        }
                        else if(curStrengthDiff < minStrengthDiff)
                        {
                            minEpidemicWordFileDiffVector = newEpidemicWordFileDiffList;
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
        System.out.println("maxStrength " + maxStrengthDiff);
        System.out.println("minStrength "+ minStrengthDiff );
    }
   
    Double getTwoNorm(List<Double> inputVector)
    {
        Double sum=0.0;
        for(Double value : inputVector)
        {
            sum+=value * value;
        }
        return Math.sqrt(sum);
    }
    
    List<Double> getEpidemicWordFileAvg(List<String> origVecList, List<Double> resultantList)
    {
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
        List<Double> finalList = new ArrayList<Double>();
        Double temp;
        
        for(int i=3;i<origVecList.size();i++)
        {
            temp = (Double.parseDouble(origVecList.get(i)) - resultantList.get(i-3))/Double.parseDouble(origVecList.get(i)); 
            finalList.add(temp);
        }
        return finalList;
    }
    
    
    
    public void main(Logger logger)
    {
        
        String dirPath = "/Users/karthikchandrasekar/Downloads/sampledata_P1_F14/Epidemic Simulation Datasets";
        //String dirPath = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
        String inputDirPath = "";
        MyUtil muObj = new MyUtil();
        Double maxValue = 0.0;
        List<List<String>> valuesList;
        List<String> headerList;
        HashMap<String, List<String>> headerValueColumnMap;
        HashMap<String, String> epidemicWordFileHash = new HashMap<String, String>();
        List<String> epidemicFileValuesList = new ArrayList<String>();
        int window,shift;
        
        try
        {                       
        
            String outputDir = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
            PrintWriter writer = new PrintWriter(outputDir+"EpidemicWordFile", "UTF-8");
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
            
            System.out.println("Enter window length");
            window = Integer.parseInt(scInput.nextLine());
            
            System.out.println("Enter shift length");
            shift = Integer.parseInt(scInput.nextLine());
            
            System.out.println("Enter alpha value");
            Alpha = Double.parseDouble(scInput.nextLine());
            
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
                //Task 1 - a
                valuesList = muObj.readCsv(file, logger);
                maxValue = findMax(valuesList);
                valuesList = normalizeData(valuesList, maxValue);
                dumpInFile(valuesList, file.getName());
                          
               
                //Task 1 - c
                headerList = valuesList.get(0);
                headerValueColumnMap = muObj.formHeaderValueHash(valuesList);
                generateEpidemicWordFile(headerValueColumnMap, file.getName(), headerList, writer, logger, epidemicWordFileHash, epidemicFileValuesList, window, shift, enteredFile);
               
                valuesList = null;
                headerValueColumnMap = null;
            }
            logger.info("End of task 1"); 

            
            writer.close();
            
            //Task 2
                     
            HashMap<String, List<String>> adjacencyHashMap;        
            adjacencyHashMap = formAdjacencyHashMap();
            generateEpidemicAvgDiffFile(epidemicWordFileHash, epidemicFileValuesList, adjacencyHashMap, enteredFile);
            logger.info("End of task 2");        
            
            
            //Task 3
           
            
            
            
            
            
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
        
                
        /***
         //Create a proxy, which we will use to control MATLAB
         MatlabProxyFactory factory = new MatlabProxyFactory();
         MatlabProxy proxy = factory.getProxy();
         
         //set matlab path
         String path = "cd('/Users/karthikchandrasekar/Documents/MATLAB')";
         proxy.eval(path);
         proxy.eval("JavaMatConn");
         
          * Call matlab function to get back band values
          * 
          *          proxy.disconnect();        
          */
        
        logger.info("Inside band generator");
 
        bandsList.add(0.0);
        bandsList.add(0.8176);
        bandsList.add(0.9924);
        bandsList.add(1.0);
        
        getBandRepList();
    }
    
    void getBandRepList()
    {
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


