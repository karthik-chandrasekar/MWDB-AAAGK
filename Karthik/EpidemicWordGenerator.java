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
    Double Alpha = 0.3;
    
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
    
    void generateEpidemicWordFile(HashMap<String, List<String>> headerValueColumnMap, String fileName, List<String> headerList, PrintWriter writer, Logger logger, HashMap<String, String> epidemicWordFileHash, List<String> epidemicWordFileValuesList)
    {
        
        List<String> valueList = null;
        List<String> timeList;
        List<String> outputList;
        int window = 3;
        int shift = 2;
        int valueSize ;
        String temp;
        String delim = ",";
        String outputString;
        String opListString="";
        String opListStringDelim="#";
        timeList = headerValueColumnMap.get("time");
        
        System.out.println(bandsList);
        System.out.println(bandRepList);
        logger.info(fileName);
        System.out.println(headerList);
        
        for(String header: headerList)      
        {
            
            if (header.equals("iteration") || header.equals("time"))
            {
                continue;
            }
            
            logger.info("File name " + fileName + "  " + header);
            valueList = headerValueColumnMap.get(header);       
            valueSize = valueList.size();
            
            
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
                    for(int j=startIndex; j<startIndex+window;j++)
                    {
                        temp = getBandRep(valueList.get(j));
                        outputList.add(temp);
                    }
                    
                    for(String output: outputList)
                    {
                        outputString = outputString + output + delim;
                    }
                    outputString = outputString.substring(0, outputString.length()-1);
                    writer.println(outputString);
                    epidemicWordFileValuesList.add(outputString);
                                      
                    opListString += (outputString)+ opListStringDelim;                    
                    startIndex = startIndex + shift;
                }
                else
                {
                    break;
                }
            }
            epidemicWordFileHash.put(fileName+"-"+header, opListString);
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
        System.out.println(headerList);
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
                    valueList.add(headerList.get(count));
                }
                count ++;
            }
            adjacencyHashMap.put("US-"+header, valueList);
        }     
        return adjacencyHashMap;
    }
      
    
    void generateEpidemicAvgDiffFile(HashMap<String, String> epidemicWordFileHash, List<String> epidemicFileValuesList, HashMap<String, List<String>> adjacencyHashMap) throws Exception
    {
        
        List<String> tempList;
        String hashOutputListString;
        String key;
        Double tempDouble;
        int count;
        List<Double> resultantList;
        List<Double> newEpidemicWordFileAvgList;
        List<Double> newEpidemicWordFileDiffList;
        int hashOutputListSize;
        String diffString;
        String avgString;
        List<String> neighborList;
        
        String outputDir = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
        PrintWriter epidemicAvgWriter = new PrintWriter(outputDir+"EpidemicWordFileAvg", "UTF-8");
        
        PrintWriter epidemicDiffWriter = new PrintWriter(outputDir+"EpidemicWordFileDiff", "UTF-8");
        
        for(String entry : epidemicFileValuesList)
        {                       
                    System.out.println(entry);
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
                        continue;
                    }
                    
                    hashOutputListString = "";
                    for(String neighbor: neighborList)
                    {
                        hashOutputListString += epidemicWordFileHash.get(key) + "#";
                    }
                    hashOutputListString = hashOutputListString.substring(0, hashOutputListString.length()-1);
                    
                    if (hashOutputListString == null)
                    {
                        System.out.println("Value not present for key  " + key);
                        continue;
                    }
                    
                    List<String> tempValList;
                    for(String temp: hashOutputListString.split("#"))
                    {
                        if (temp.isEmpty()){continue;}
                        tempValList = Arrays.asList(temp.split(","));
                        System.out.println(tempValList);
                        if (tempValList.isEmpty() || tempList.isEmpty()){continue;}
                        if(!(tempValList.get(3).equals(tempList.get(3))))
                        {
                            continue;
                        }
                        
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
                    
                    hashOutputListSize = hashOutputListString.split("#").length;
                    System.out.println(resultantList.size());
                    for(int i=0;i<resultantList.size();i++)
                    {
                        System.out.println(resultantList);
                        resultantList.set(i,resultantList.get(i)/hashOutputListSize);
                    }
                    
                    //EpidemicWordFileAvg 
                    System.out.println("EpidemicWordFileAvg");
                    newEpidemicWordFileAvgList = getEpidemicWordFileAvg(tempList, resultantList);
                    for(int i=3;i<tempList.size();i++)
                    {
                        tempList.set(i, String.valueOf(newEpidemicWordFileAvgList.get(i-3)));
                    }
                    avgString = "";
                    System.out.println("Temp list " + tempList);
                    for(String temp: tempList)
                    {
                        avgString = avgString + temp + ",";
                    
                    }
                    avgString = avgString.substring(0, avgString.length()-1);
                    System.out.println("Average string" + avgString);
                    epidemicAvgWriter.println(avgString);
                                    
                    
                    //EpidemicWordFileDiff
                    System.out.println("EpidemicWordFileDiff");
                    newEpidemicWordFileDiffList = getEpidemicWordFileDiff(tempList, resultantList);
                    
                    for(int i =3;i<tempList.size();i++)
                    {
                        tempList.set(i, String.valueOf(newEpidemicWordFileDiffList.get(i-3)));
                    }
                    avgString = "";
                    for(String temp: tempList)
                    {
                        avgString = avgString + temp + ",";
                    }
                    avgString = avgString.substring(0, avgString.length()-1);
                    epidemicDiffWriter.println(avgString);
                    System.out.println(avgString);                                  
            
        } 
        epidemicAvgWriter.close();
        epidemicDiffWriter.close(); 
    }
    
    List<Double> getEpidemicWordFileAvg(List<String> origVecList, List<Double> resultantList)
    {
        List<Double> finalList = new ArrayList<Double>();
        Double temp;
        
        System.out.println("OrigVecList " + origVecList);
        System.out.println("ResultantList" + resultantList);
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
        System.out.println("Final list" + finalList);
        return finalList;
    }
    
    List<Double>  getEpidemicWordFileDiff(List<String> origVecList, List<Double> resultantList)
    {
        List<Double> finalList = new ArrayList<Double>();
        Double temp;
        
        System.out.println("OrigVecList" + origVecList);
        for(int i=3;i<origVecList.size();i++)
        {
            temp = (Double.parseDouble(origVecList.get(i)) - resultantList.get(i-3))/Double.parseDouble(origVecList.get(i)); 
            finalList.add(temp);
        }
        System.out.println("Final list" + finalList);
        return finalList;
    }
    
    
    
    public void main(Logger logger)
    {
        
        String dirPath = "/Users/karthikchandrasekar/Downloads/sampledata_P1_F14/Epidemic Simulation Datasets";
        //String dirPath = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
        MyUtil muObj = new MyUtil();
        Double maxValue = 0.0;
        List<List<String>> valuesList;
        List<String> headerList;
        HashMap<String, List<String>> headerValueColumnMap;
        HashMap<String, String> epidemicWordFileHash = new HashMap<String, String>();
        List<String> epidemicFileValuesList = new ArrayList<String>();

        
        try
        {                       
        
            String outputDir = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
            PrintWriter writer = new PrintWriter(outputDir+"EpidemicWordFile", "UTF-8");

            for(File file : muObj.getFilesInDir(dirPath, logger))
            {
                System.out.println("File " + file.getName());
                //Task 1 - a
                logger.info("Task 1 -a");
                valuesList = muObj.readCsv(file, logger);
                maxValue = findMax(valuesList);
                logger.info(String.valueOf(maxValue));
                valuesList = normalizeData(valuesList, maxValue);
                dumpInFile(valuesList, file.getName());
                
                //Bands Generator
                logger.info("Bands generator");
                BandsGenerator bg = new BandsGenerator();
                bg.main(logger);
                bandsList = bg.bandsList;
                bandRepList = bg.bandRepList;
               
                //Task 1 - c
                logger.info("Task 1-c");
                headerList = valuesList.get(0);
                headerValueColumnMap = muObj.formHeaderValueHash(valuesList);
                generateEpidemicWordFile(headerValueColumnMap, file.getName(), headerList, writer, logger, epidemicWordFileHash, epidemicFileValuesList);
               
                valuesList = null;
                headerValueColumnMap = null;
                logger.info("End of task 1"); 
            }
            
            writer.close();
            
            //Task 2
                     
            HashMap<String, List<String>> adjacencyHashMap;        
            adjacencyHashMap = formAdjacencyHashMap();
            generateEpidemicAvgDiffFile(epidemicWordFileHash, epidemicFileValuesList, adjacencyHashMap);
            logger.info("End of task 2");        
            
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
        bandsList.add(0.25);
        bandsList.add(0.5);
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

