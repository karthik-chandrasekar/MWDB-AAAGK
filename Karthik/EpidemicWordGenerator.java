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
    
     public HashMap<String, List<String>> readCsv(File inputFile, Logger logger) throws Exception
        {
            List<String> headerList = new ArrayList<String>();
            List<List<String>> valuesList = new ArrayList(new ArrayList<String>());
            Scanner scannerObj = new Scanner(inputFile);
            
            //Collect header list
            headerList  = Arrays.asList(scannerObj.nextLine().split(","));
                 
            //Collect values list
            while(scannerObj.hasNextLine())
            {
                valuesList.add(Arrays.asList(scannerObj.nextLine().split(",")));
                
            }
            
            //Map header to value
            
            return formHeaderValueHash(headerList, valuesList, inputFile, logger);
          
        }
     
      public HashMap<String, List<String>> formHeaderValueHash(List<String> headerList, List<List<String>> valuesList, File inputFile, Logger logger) throws Exception
      {
          HashMap<String, List<String>> keyValueHash = new HashMap<String, List<String>>();

          for(int i=0;i<headerList.size();i++)
          {
              keyValueHash.put(headerList.get(i), valuesList.get(i));
          }

          return keyValueHash;
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

  
    
    public double findMax(HashMap<String, List<String>> headerValueMap)
    {
        Double maxValue = 0.0;
        int count;
        
        for(List<String> valueList : headerValueMap.values())
        {
            count = 0;
            for(String value : valueList)
            {
                 count ++;
                 if (count > 2)
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
    
    public List<List<String>> normalizeData(File inputFile, double maxValue) throws Exception
    {
        List<List<String>> valuesList = new ArrayList(new ArrayList<String>());
        List<String> valueList;
        Scanner scannerObj = new Scanner(inputFile);

        //Add file headers
        valuesList.add(Arrays.asList(scannerObj.nextLine().split(",")));
        
        //Add file rows
        while(scannerObj.hasNextLine())
        {
            int count = 0;
            valueList = new ArrayList<String>();
          
            for(String value : scannerObj.nextLine().split(","))
            {
                count ++;
                if (count > 2)
                {
                    valueList.add(String.valueOf((Double.valueOf(value))/maxValue));
                }
                else
                {
                    valueList.add(value);
                }
            }
            valuesList.add(valueList);
        }
        
        return valuesList;
    }
    
    public void main(Logger logger)
    {
        
        String dirPath = "/Users/karthikchandrasekar/Downloads/sampledata_P1_F14/Epidemic Simulation Datasets";
        MyUtil muObj = new MyUtil();
        Double maxValue = 0.0;
        HashMap<String, List<String>> headerValueMap;
        List<List<String>> valuesList;
        
        try
        {       
            for(File file : muObj.getFilesInDir(dirPath, logger))
            {
                headerValueMap = muObj.readCsv(file, logger);
                maxValue = findMax(headerValueMap);
                valuesList = normalizeData(file, maxValue);
                dumpInFile(valuesList, file.getName());
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
        BandsGenerator bg = new BandsGenerator();
        bg.main(logger);
        
        //Epidemic word file generator
        EpidemicFileGenerator efg = new EpidemicFileGenerator();
        efg.main(logger);
    }   
}
