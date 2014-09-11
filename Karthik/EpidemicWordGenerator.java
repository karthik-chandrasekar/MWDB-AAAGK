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
}
     

class DataNormalizer{
    
 
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
    
    public void main(Logger logger)
    {
        
        String dirPath = "/Users/karthikchandrasekar/Downloads/sampledata_P1_F14/Epidemic Simulation Datasets";
        //String dirPath = "/Users/karthikchandrasekar/Desktop/ThirdSem/MWDB/Phase1/EpidemicWordOutput/";
        MyUtil muObj = new MyUtil();
        Double maxValue = 0.0;
        List<List<String>> valuesList;
        
        try
        {       
            for(File file : muObj.getFilesInDir(dirPath, logger))
            {
                //Task 1 - a
                valuesList = muObj.readCsv(file, logger);
                maxValue = findMax(valuesList);
                logger.info(String.valueOf(maxValue));
                valuesList = normalizeData(valuesList, maxValue);
                dumpInFile(valuesList, file.getName());
                
                //Task 1 - c
                muObj.formHeaderValueHash(valuesList);
            }
        }
        catch(Exception e)
        {
        }
    }    
}

class BandsGenerator{
    public void main(Logger logger) throws MatlabConnectionException, MatlabInvocationException{
        
        //Create a proxy, which we will use to control MATLAB
         MatlabProxyFactory factory = new MatlabProxyFactory();
         MatlabProxy proxy = factory.getProxy();
         
        //set matlab path
         String path = "cd('/Users/karthikchandrasekar/Documents/MATLAB')";
         proxy.eval(path);
         proxy.eval("JavaMatConn");
         
         /***
          * Call matlab function to get back band values
          * 
          */
         
         proxy.disconnect();
        
    }
}


class EpidemicFileGenerator{
    
    public void main(Logger logger){
        
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

        //Data Normalizer
        DataNormalizer dn = new DataNormalizer();
        dn.main(logger);
        
        //Bands Generator
        //BandsGenerator bg = new BandsGenerator();
        //bg.main(logger);
        
        //Epidemic word file generator
        //EpidemicFileGenerator efg = new EpidemicFileGenerator();
        //efg.main(logger);
    }   
}
