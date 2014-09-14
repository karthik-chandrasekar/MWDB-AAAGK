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
    HashMap<String, List<String>> epidemicWordFileHash = new HashMap<String, List<String>>();
    
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
    
    void generateEpidemicWordFile(HashMap<String, List<String>> headerValueColumnMap, String fileName, List<String> headerList, PrintWriter writer, Logger logger)
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
        List<String> opList;
        
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
            opList = new ArrayList<String>();
            
            
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
                                      
                    opList.add(outputString);                    
                    startIndex = startIndex + shift;
                }
                else
                {
                    break;
                }
            }
            epidemicWordFileHash.put(fileName+"-"+header, opList);
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
    
    
    public HashMap<String, List<String>> formAdjacencyHashMap()
    {
        
        //Parse adjacency matrix and form a state, adjacent states hash map
        
        HashMap<String, List<String>> adjacencyHashMap = new HashMap<String, List<String>>();
        List<String> headerList;
        List<String> valueList;
        int count;
        String header="";
        
        String inputFilePath = "/Users/karthikchandrasekar/Downloads/LocationMatrix.csv";
        
        Scanner scannerObj = new Scanner(inputFilePath);
        
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
                    continue;
                }
                else if (Integer.parseInt(value)==1)
                {
                    valueList.add(headerList.get(count));
                }
                count ++;
            }
            adjacencyHashMap.put(header, valueList);
        }     
        return adjacencyHashMap;
    }
    
    
    public void generateEpidemicFile()
    {
        
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
                generateEpidemicWordFile(headerValueColumnMap, file.getName(), headerList, writer, logger);
               

                logger.info("End of all tasks"); 
            }
            
            writer.close();
            
            //Task 2
            
            
            HashMap<String, List<String>> adjacencyHashMap;        
            adjacencyHashMap = formAdjacencyHashMap();
            //generateEpidemicAvgFile()
       
            
            
            
        }
        catch(Exception e)
        {
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

